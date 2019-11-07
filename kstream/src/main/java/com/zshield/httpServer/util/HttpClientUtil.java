package com.zshield.httpServer.util;

import com.zshield.httpServer.config.ViolationServerConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Set;

public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private Client client = ClientBuilder.newClient();
    private String ADDRESS = ViolationServerConfig.BACKSTAGE_SERVER_ADDRESS;

    public HttpClientUtil() {
        //连接建立超时时间
        client.property(ClientProperties.CONNECT_TIMEOUT, ViolationServerConfig.HTTPCLIENT_CONNECT_TIMEOUT);
        //读取内容超时时间
        client.property(ClientProperties.READ_TIMEOUT, ViolationServerConfig.HTTPCLIENT_READ_TIMEOUT);
    }

    public Response post(String uri, String json) throws Throwable {
        String url = ADDRESS + uri;
        WebTarget target = client.target(url);
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
        Response post;
        try {
            post = builder.post(Entity.entity(json, MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof ConnectException) {
                logger.error("[Connect exception to the background] {} {} method:post", url, json, cause);
            } else if(cause instanceof SocketTimeoutException) {
                logger.error("[SocketTimeout exception to the background] {} {} method:post", url, json, cause);
            }
            throw e.getCause();
        }
        return post;
    }

    public static class SingletonPatternHolder {
        public static final HttpClientUtil httpClient = new HttpClientUtil();
    }

    public static final HttpClientUtil getInstance() {
        return SingletonPatternHolder.httpClient;
    }

    public Response get(String uri, Map<String, String> parameters) throws Throwable {
        String url = ADDRESS + uri;
        if(parameters != null && parameters.size() > 0) {
            url = url + "?";
            Set<String> keySet = parameters.keySet();
            for (String key : keySet) {
                String value = parameters.get(key);
                url = url + key + "=" + value + "&";
            }
            url = url.substring(0, url.length() - 1);
        }
        //Build a new web resource target.
        WebTarget target = client.target(url);
        //Start building a request to the targeted web resource and define the accepted response media types.
        //传递的参数为："application/json"
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
        Response get;
        try {
            //invoke HTTP GET method for the current request synchronously.
            //同步调用当前请求的HTTP GET方法。
            get = builder.get();
        } catch (ProcessingException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ConnectException) {
                logger.error("[Connect exception to the background] {} {} method:get", url, parameters, cause);
            } else if (cause instanceof SocketTimeoutException) {
                logger.error("[SocketTimeout exception to the background] {} {} method:get", url, parameters, cause);
            }
            throw e.getCause();
        }
        return get;
    }
}
