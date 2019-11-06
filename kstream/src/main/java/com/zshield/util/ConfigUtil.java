package com.zshield.util;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

public class ConfigUtil {
    public static Properties producerProps = new Properties();
    public static Properties consumerProps = new Properties();
    public static Properties baseStreamProps = new Properties();
    public static Properties violationStreamProps = new Properties();
    public static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

    public static void loadProps(String fileName) {
        Properties props = new Properties();
        if (fileName != null) {
            try (InputStream propStream = Files.newInputStream(Paths.get(fileName))) {
                props.load(propStream);
                for(Map.Entry<Object, Object> entry : props.entrySet()) {
                    String key = entry.getKey().toString();
                    Object value = entry.getValue();
                    if (key.startsWith("producer")) {
                        producerProps.put(key.substring(9), value);
                        if (key.substring(9).equals("retries")) {
                            producerProps.put(key.substring(9), Integer.valueOf((String) value, 16));
                        }
                    } else if (key.startsWith("consumer")) {
                        consumerProps.put(key.substring(9), value);
                    } else if (key.startsWith("base")) {
                        baseStreamProps.put(key.substring(12), value);
                    } else if (key.startsWith("violation")) {
                        violationStreamProps.put(key.substring(17), value);
                    }
                }
                producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ParamAnalysisUtil.bootstrap_server);

                consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ParamAnalysisUtil.bootstrap_server);
                consumerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, ParamAnalysisUtil.consumer_client_id);

                baseStreamProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ParamAnalysisUtil.bootstrap_server);
                //定义客户端的ID
                baseStreamProps.put(StreamsConfig.CLIENT_ID_CONFIG, ParamAnalysisUtil.base_stream_client_id);
                baseStreamProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
                baseStreamProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

                violationStreamProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ParamAnalysisUtil.bootstrap_server);
                violationStreamProps.put(StreamsConfig.CLIENT_ID_CONFIG, ParamAnalysisUtil.violation_tream_client_id);
                violationStreamProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
                violationStreamProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

            } catch (IOException e) {
                logger.error("[config file is not found or error] config file name {}]", fileName, e);
            }
        } else {
            logger.info("[Did not load any properties since the peoperty file is not specified]");
        }
        System.out.println();
    }
}
