package com.zshield.httpServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zshield.httpServer.common.ViolationCommon;
import com.zshield.httpServer.common.ViolationShared;
import com.zshield.httpServer.config.ViolationServerConfig;
import com.zshield.httpServer.domain.Violation;
import com.zshield.httpServer.domain.ViolationFactory;
import com.zshield.httpServer.util.HttpClientUtil;
import com.zshield.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ViolationIssuedThread {

    private static final Logger logger = LoggerFactory.getLogger(ViolationIssuedThread.class);
    private static ScheduledExecutorService service;
    private static CountDownLatch latch;
    private ViolationShared shared = ViolationShared.getInstance();
    private ViolationCommon violationCommon = ViolationCommon.getInstance();
    public static Map<String, Violation> prepareProcessedViolations = new ConcurrentHashMap();

    private ViolationIssuedThread(){}

    private static class SingletonPatternHolder {
        private static final ViolationIssuedThread violationIssuedThread = new ViolationIssuedThread();
    }

    public static ViolationIssuedThread getInstance() {
        return SingletonPatternHolder.violationIssuedThread;
    }

    public static void startUp(String threadName) {
        if(service == null) {
            ViolationIssuedThread violationIssuedThread = ViolationIssuedThread.getInstance();
            logger.info("[ViolationIssuedThread start up]");
            service = Executors.newScheduledThreadPool(2);
            //全量拉取Violation
            violationIssuedThread.initViolationData();

            Thread IssuedThread = ThreadUtil.nonDaemon(threadName + " - IssuedThrea", new Runnable() {
                @Override
                public void run() {
                    logger.info("{} - IssuedThread start", threadName);
                    violationIssuedThread.IssuedViolation();
                }
            })
        }
    }

    private void IssuedViolation() {
        Set<String> keys = prepareProcessedViolations.keySet();
        for (String key : keys) {
            Violation violation = prepareProcessedViolations.get(key);
            try {
                violationCommon.detectionDependence(prepareProcessedViolations.get(key));
            } catch (Throwable throwable) {
                logger.error("[InitViolationData Dependence error: /violation post ruleid:{}, dependence registered request error]", violation.getRule_id(), throwable);
                continue;
            }
            violation = isPrepareIssued(prepareProcessedViolations.get(key));

        }
    }

    private Violation isPrepareIssued(Violation violation) {
        boolean prepareSign = true;
        Set<Integer> sensor_groupids = violation.getSensor_groupids();
        for (Integer sensorGroupid : sensor_groupids) {
            if (!violationCommon.isRegisteredSensorGroupId(sensorGroupid)) {
                prepareSign = false;
                break;
            }
        }
        Integer programGroup = ((Violation.Conversion) violation.getRule()).getProgramGroup();
        if (programGroup != null) {
            if (!violationCommon.isRegisteredProgramGroupId(programGroup)) {
                prepareSign = false;
            }
        }

    }

    /**
     * 初始化拉取violationData；
     * 程序启动时执行这个线程，拉取成功后退出。
     */
    private void initViolationData() {
        ThreadUtil.nonDaemon("InitViolationData-Thread", new Runnable() {
            @Override
            public void run() {
                logger.info("[InitViolationData-Thread start up]");
                while (true) {
                    //Defines the contract between a returned instance and the runtime
                    // when an application needs to provide meta-data to the runtime.
                    Response response;
                    try {
                        response = HttpClientUtil.getInstance().get(ViolationServerConfig.BACKSTAGE_ALL_VIOLATION, null);
                        if (response.getStatus() != 200) {
                            throw new Throwable();
                        } else {
                            JsonObject parse = new JsonParser().parse(response.readEntity(String.class)).getAsJsonObject();
                            if (parse.get("success").getAsString().equals("true")) {
                                JsonArray data = parse.get("data").getAsJsonArray();
                                for (JsonElement datum : data) {
                                    JsonObject jsonViolation = datum.getAsJsonObject();
                                    Violation violation = ViolationFactory.parsingData(jsonViolation);
                                    if (violation != null){
                                        if(violation.isIssued()){
                                            try {
                                                violationCommon.detectionDependence(violation);
                                            } catch (Throwable throwable) {
                                                logger.error("[InitViolationData Dependence error: /violation post ruleid:{},dependence registered request error]", violation.getRule_id(), throwable);
                                                continue;
                                            }
                                            violationCommon.violationChange(violation);
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    } catch (Throwable e) {
                        logger.info("[InitViolationData request error ]", e);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                logger.info("[InitViolationData-Thread execution succeed, stop... {}]",prepareProcessedViolations);
            }
        }).start();
    }

}
