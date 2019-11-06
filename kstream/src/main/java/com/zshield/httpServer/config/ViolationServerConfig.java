package com.zshield.httpServer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.spec.PSSParameterSpec;

public class ViolationServerConfig {

    public static final Logger logger = LoggerFactory.getLogger(ViolationServerConfig.class);

    //kstream server 接口信息
    public static final String KSTREAM_SERVER_ADDRESS = System.getenv("CURRENT_IP") == null ? "http://0.0.0.0:6211" : "http://" + System.getenv("CURRENT_IP") + ":6211";

    //后台接口信息
    public static final String BACKSTAGE_SERVER_ADDRESS = System.getenv("MASTER_IP") == null ? "http://0.0.0.0:12000" : "http://" + System.getenv("MASTER_IP") + ":12000";
    public static final String BACKSTAGE_ALL_VIOLATION = "/febg/v1/violation/kstream_rule";

    //http客户端 TIMEOUT设置
    public static final int HTTPCLIENT_CONNECT_TIMEOUT = 3000;
    public static final int HTTPCLIENT_READ_TIMEOUT = 3000;

}
