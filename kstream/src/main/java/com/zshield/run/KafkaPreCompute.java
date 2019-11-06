package com.zshield.run;

import com.zshield.httpServer.HttpSrever;
import com.zshield.httpServer.ViolationIssuedThread;
import com.zshield.stream.Stream;
import com.zshield.stream.ViolationStream;
import com.zshield.util.ConfigUtil;
import com.zshield.util.KafkaClusterStatus;
import com.zshield.util.ParamAnalysisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaPreCompute {
    public static String es_host;
    public static int es_number_of_replica;
    public static String bootstrap_server;
    public static String consumer_client_id;
    public static String stream_client_id;
    public static boolean is_upgrade;
    public static final Logger logger = LoggerFactory.getLogger(KafkaPreCompute.class);

    public static void main(String[] args) {
        try {
            //获取到相应的参数（boostrap_sever等）
            ParamAnalysisUtil.Parsing(args);
            //判断集群是否正在运行。
            KafkaClusterStatus.isClusterConnected();
            //加载kakfa在服务器中的配置文件，对各个stream进行配置。
            ConfigUtil.loadProps("/opt/kstream/config/server.properties");
            HttpSrever.startUp();
            ViolationIssuedThread.startUp("Violation-Issued-Thread");
            if (KafkaClusterStatus.isTopicCreated(ParamAnalysisUtil.input_topic)) {
                ViolationStream.startUp("violation_stream");
                Stream.startUp("stream");
            }
        } catch (Exception e) {
            logger.error("[main thread exception]", e);
        }

    }

}
