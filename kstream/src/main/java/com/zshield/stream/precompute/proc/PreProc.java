package com.zshield.stream.precompute.proc;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zshield.httpServer.controller.KstreamHealthController;
import com.zshield.queryEs.MarkUpgrade;
import com.zshield.stream.precompute.entry.EntryFactory;
import com.zshield.stream.precompute.entry.EntryInterface;
import com.zshield.util.KafkaClusterStatus;
import com.zshield.util.ParamAnalysisUtil;
import com.zshield.util.TimeUtil;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PreProc extends AbstractProcessor<String, String> {
    private ProcessorContext context;
    private JsonParser jp;
    private Gson gson;
    private int count;
    private Map<String, String> deleteSensorIds;
    private EntryFactory entryFactory;
    private KeyValueStore<String,String> kv;
    private static AtomicInteger mark = new AtomicInteger(0);
    private static AtomicInteger countMark = new AtomicInteger(0);
    private long offset = 0;
    private final static Logger logger = LoggerFactory.getLogger(PreProc.class);

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        jp = new JsonParser();
        gson = new Gson();
        deleteSensorIds = new ConcurrentHashMap();
        kv = (KeyValueStore<String, String>) context.getStateStore("qstore");
        entryFactory = EntryFactory.getInstance();

        this.context.schedule(Duration.ofMinutes(20), PunctuationType.WALL_CLOCK_TIME,timestamp ->{
            if (ParamAnalysisUtil.is_upgrade) {
                logger.info("[check if log is processing]");
                countMark.addAndGet(1);
                if (offset != this.context.offset()) {
                    mark.addAndGet(1);
                    offset = this.context.offset();
                }
                if (countMark.get() >= 24) {
                    logger.info("[last task is processing]");
                    if (mark.get() == 0) {
                        logger.info("[all data processing of this upgrade has been completed]");
                        KafkaClusterStatus.deleteTopic();
                        MarkUpgrade mark = new MarkUpgrade();
                        mark.createFile();
                    }
                    mark.set(0);
                    countMark.set(0);
                }
            }
        });

        this.context.schedule(Duration.ofMinutes(1), PunctuationType.WALL_CLOCK_TIME,timestamp ->{
            if (count > 0) {
                logger.info("[stream calculate speed] [Count:" + count + "| Time:60s|Speed:" + String.format("%d", count/60) + "]");
                count = 0;
                this.context.commit();
            }
        });

        this.context.schedule(Duration.ofSeconds(3), PunctuationType.WALL_CLOCK_TIME,timestamp ->{
            KstreamHealthController.streamChangingTimeMillis = System.currentTimeMillis();
        });

        context.schedule(Duration.ofMinutes(20), PunctuationType.WALL_CLOCK_TIME,timestamp ->{
            if (deleteSensorIds.size() > 0) {
                logger.info(Thread.currentThread().getName() + " start clear StateStore");
                clearKV(kv, deleteSensorIds);

                deleteSensorIds.forEach((sensorId,updateSensorIdDayTime -> {
                    LocalDate pendingUpgradeDateTime = LocalDate.parse(updateSensorIdDayTime);
                    String oldDayTime = kv.get(sensorId);
                    if (pendingUpgradeDateTime.isAfter(LocalDate.parse(oldDayTime))) {
                        logger.info("Updata " + sensorId + " current processing time" + pendingUpgradeDateTime.format(TimeUtil.DATE_FORMATTER));
                        kv.put(sensorId, pendingUpgradeDateTime.format(TimeUtil.DATE_FORMATTER));
                    }
                });
                deleteSensorIds.clear();
                logger.info(Thread.currentThread().getName() + "clear StateStore Stop");
            }
        });


    }

    private void clearKV(KeyValueStore<String,String> kv, Map<String,String> deleteSensorIds) {
        if (deleteSensorIds == null || deleteSensorIds.size() == 0) {
            return;
        }
        int i = 0;
        long t = System.currentTimeMillis();
        KeyValueIterator<String, String> it = kv.all();
        while (it.hasNext()) {
            String key = it.next().key;
            String[] split = key.split("=", 2)[0].split("-");
            if (split.length > 1) {
                String sensorId = split[0];
                String day = split[1];
                if (deleteSensorIds.containsKey(sensorId) && deleteSensorIds.get(sensorId).compareTo(day) > 0) {
                    i++;
                    kv.delete(key);
                }
            }
        }
        it.close();
        if (i > 0) {
            logger.info(kv.name() + " delete " + i + " pieces,spend " + (System.currentTimeMillis() - t) + "ms");
        }
    }

    @Override
    public void process(String key, String value) {
        try {
            JsonObject obj = verification(jp.parse(value).getAsJsonObject());
            if (obj != null) {
                String sensorId = obj.get("SENSOR_ID").getAsString();
                String logTime = obj.get("TIME").getAsString();
                count++;
                List<EntryInterface> entryInterfaces = entryFactory.create(obj);
                if (entryInterfaces.size() > 0) {
                    clearStateStore(sensorId, logTime);
                    for (int i = 0; i < entryInterfaces.size(); i++) {
                        EntryInterface entry = entryInterfaces.get(i);
                        context.forward(entry.getDocId(), entry, "query_proc");
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        } catch (Throwable eh) {
            logger.error("[PreProc process exception, log {}]", value, eh);
        }
    }

    private void clearStateStore(String sensorId, String logTime) {
        String newdayTime = TimeUtil.parseToEastEightZoneTime(logTime).plusHours(-1).format(TimeUtil.DATE_FORMATTER);

        String oldDayTime = kv.get(sensorId);
        if (oldDayTime == null) {
            kv.put(sensorId, newdayTime.split("T")[0]);
        } else {
            if (oldDayTime.compareTo(newdayTime) >0) {
                throw new IllegalArgumentException(String.format("%s log DAY_TIME out-of-order, old log DAY_TIME is: %s, new log DAY_TIME is: %s", sensorId, oldDayTime,newdayTime));
            } else if (LocalDate.parse(oldDayTime, TimeUtil.DATE_FORMATTER).isBefore(LocalDate.parse(newdayTime, TimeUtil.DATE_FORMATTER))){
                deleteSensorIds.put(sensorId, newdayTime);
            }
        }
    }

    private JsonObject verification(JsonObject jsonObject) {
        if (jsonObject.get("SENSOR_ID") == null || jsonObject.get("SENSOR_ID").getAsString().equals("")) {
            return null;
        }

        String logTime = jsonObject.get("TIME").getAsString();
        //检查日志时间是否比服务器时间大一天
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(logTime).withOffsetSameInstant(ZoneOffset.of("+08:00"));
        LocalDateTime nowAddOneDay = LocalDateTime.now().plusDays(1);
        if (nowAddOneDay.isBefore(ChronoLocalDateTime.from(offsetDateTime))) {
            logger.warn("[log time exceeds server time by more than one day , log {} ]", jsonObject);
            return null;
        }

        //统一做时间转换
        String time = TimeUtil.zoneParse(logTime, "+08:00");
        LocalDateTime parse = LocalDateTime.parse(time, TimeUtil.DATETIME_FORMATTER);
        String dayTime = parse.format(TimeUtil.DATE_FORMATTER);
        String timeHms = parse.format(TimeUtil.TIME_FORMATTER);
        jsonObject.addProperty("TIME",time);
        jsonObject.addProperty("DAY_TIME", dayTime);
        jsonObject.addProperty("TIME_hms", timeHms);
        jsonObject.addProperty("@timestamp", TimeUtil.getTimeStamp());
        return jsonObject;
    }
}

