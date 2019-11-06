package com.zshield.httpServer;

import com.zshield.httpServer.config.ViolationServerConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HttpSrever {
    public static final String ADDRESS = ViolationServerConfig.KSTREAM_SERVER_ADDRESS;
    private static final Logger logger = LoggerFactory.getLogger(HttpSrever.class);
    private CountDownLatch latch = null;
    private HttpServer server = null;
    private HttpSrever(){};

    public static class SingletonPatternHolder{
        public static final HttpSrever httpSrever = new HttpSrever();
    }

    public static HttpSrever getInstance() {
        return SingletonPatternHolder.httpSrever;
    }

    public static void startUp() {
        HttpSrever httpSrever = HttpSrever.getInstance();
        httpSrever.doWork();
    }

    private void doWork() {
        if (server == null && latch == null ) {
            logger.info("[kstream httpSever start up]");
            try {
                server = startServer();
                server.start();
                latch = new CountDownLatch(1);
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        server.shutdown(5, TimeUnit.SECONDS);
                    }
                }));
            } catch (Exception e) {
                logger.error("[kstream httpServer start error]", e);
                server.shutdown();
                latch.countDown();
            }
            logger.info("[kstream httpServer start successfully, Listener {}]", HttpSrever.ADDRESS);
        }
    }

    public HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("com.zshield.httpServer.controller");

        //create方法：             Creates a URI by parsing the given string.
        //createHttpServer方法：   Create new {@link HttpServer} instance.
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(HttpSrever.ADDRESS), rc, false);
    }



}
