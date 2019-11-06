package com.zshield.util;

import com.zshield.run.KafkaPreCompute;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class KafkaClusterStatus {
    public static AdminClient adminClient;
    public static final Logger logger = LoggerFactory.getLogger(KafkaClusterStatus.class);

    /**
     * kafka的client创建，
     */
    public static void clietnInit() {
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,KafkaPreCompute.bootstrap_server);
        //错误重试的次数。
        properties.put(CommonClientConfigs.RETRIES_CONFIG,5);
        adminClient = KafkaAdminClient.create(properties);
    }

    public static void isClusterConnected() {
        clientInit();
        //获取集群当前node的信息；
        //Get information about the nodes in the cluster, using the default options.
        DescribeClusterResult describeClusterResult = adminClient.describeCluster();
        try {
            //Returns a future which yields the current cluster id.
            // The future value will be non-null if the broker version is 0.10.1.0 or higher and null otherwise.
            describeClusterResult.clusterId().get();
            logger.info("[connected kafka successfully]");
        } catch (Exception e) {
            adminClient.close();
            logger.error("[get clusterID exception,please check out if cluster is running");
            System.exit(1);
        }
    }

    private static void clientInit() {
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, ParamAnalysisUtil.bootstrap_server);
        properties.put(CommonClientConfigs.RETRIES_CONFIG, 5);
        adminClient = KafkaAdminClient.create(properties);
    }

    public static boolean isTopicCreated(String inputTopic) {
        while (true) {
            try {
                Set<String> topics = listAllTopic();
                if (topics.contains(inputTopic)) {
                    break;
                } else {
                    logger.info("[kafka do not have sensor_input topic,please check if topic was created]");
                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                logger.error("[get topic exception,please check out if cluster is normal] [The reason for error {" + "");
                System.exit(1);
            }
        }
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Set<String> listAllTopic() {
        ListTopicsResult topicsResult = adminClient.listTopics();
        KafkaFuture<Set<String>> names = topicsResult.names();
        try {
            Set<String> topics = names.get();
            for (String topic : topics) {
                logger.info("list kafka topic:[" + topic + "]");
            }
            return topics;
        } catch (Exception e) {
            logger.error("[get topic exception, please check out if cluster is normal]", e );
            System.exit(1);
        }
        return null;
    }

    public static void deleteTopic() {
        while (true) {
            try {
                Set<String> topics = listAllTopic();
                List<String> topicList = new ArrayList<>();
                if (topics.contains("sensor-stream-qstore-changelog")) {
                    topicList.add("sensor-stream-qstore-changelog");
                }
                if (topics.contains("sensor-violation-vioQstore-changelog")) {
                    topicList.add("sensor-violation-vioQstore-changelog");
                }
                if (topics.contains("sensor-output_medium")) {
                    topicList.add("sensor-output_medium");
                }
                if (topics.contains("sensor-input_upgrade")) {
                    topicList.add("sensor-input_upgrade");
                }
                if(topicList.size() > 0) {
                    adminClient.deleteTopics(topicList);
                    for (String topic : topicList) {
                        logger.info("delete kafka topic:[" + topic + "]");
                    }
                }
            } catch (Exception e) {
                logger.error("[delete topic exception]", e);
                continue;
            }
            break;
        }
    }
}
