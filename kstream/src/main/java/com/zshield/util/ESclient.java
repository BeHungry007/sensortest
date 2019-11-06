package com.zshield.util;


import com.sun.org.slf4j.internal.LoggerFactory;
import com.zshield.run.KafkaPreCompute;

import java.util.logging.Logger;

public class ESclient {
    private static final Logger logger = LoggerFactory.getLogger(ESclient.class);

    private static RestHighLevelClient highClient = null;

    private ESclient(){}

    public static RestHighLevelClient getHighClient(){
        if(highClient == null){
            synchronized (ESclient.class){
                if (highClient == null){
                    HttpHost httpHost = new HttpHost(ParamAnalusisUtil.es_host, 9200, "http");
                    highClient = new RestHighLevelClient(RestClient.builder(httpHost));
                }
            }
        }
    }
}
