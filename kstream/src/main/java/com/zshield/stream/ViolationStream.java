package com.zshield.stream;


import com.zshield.stream.violation.proc.ViolationDetect;
import com.zshield.stream.violation.proc.ViolationProcessing;
import com.zshield.util.ConfigUtil;
import com.zshield.util.ParamAnalysisUtil;
import com.zshield.util.ThreadUtil;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ViolationStream {
    private Topology topo;
    private StoreBuilder builder;
    private static Thread thread;
    private static CountDownLatch latch;
    private static final Logger logger = LoggerFactory.getLogger(ViolationStream.class);

    public ViolationStream() {
        topo = new Topology();
        //Create an in-memory {@link KeyValueBytesStoreSupplier}.
        // @param name  name of the store (cannot be {@code null})
        KeyValueBytesStoreSupplier tsup = Stores.inMemoryKeyValueStore("vioQstore");
        //Creates a {@link StoreBuilder} that can be used to build a {@link KeyValueStore}.
        builder = Stores.keyValueStoreBuilder(tsup, Serdes.String(), Serdes.String());
    }

    public static void startUp(String threadName) {
        if (thread == null) {
            ViolationStream stream = new ViolationStream();
            thread = ThreadUtil.nonDaemon(threadName, new Runnable() {
                @Override
                public void run() {
                    stream.doWork();
                }
            });
            thread.start();
        }
    }

    private void doWork() {
        topo.addSource("source", ParamAnalysisUtil.input_topic)
                .addProcessor("vio_pre_proc", () -> new ViolationProcessing(), "source")
                .addProcessor("vioDection", () -> new ViolationDetect(), "vio_pre_proc")
                .addStateStore(builder, "vio_pre_proc", "vioDection");

        final KafkaStreams streams = new KafkaStreams(topo , ConfigUtil.violationStreamProps);

        streams.setUncaughtExceptionHandler((Thread thread, Throwable e) -> {
            logger.error("[stream caught exception]", e );
            latch.countDown();
        });

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        try {
            latch = new CountDownLatch(1);
            streams.start();
            latch.await();
        } catch (InterruptedException e) {
            logger.error("[CountDownLatch await exception]", e );
            System.exit(1);
        } finally {
            streams.close(5, TimeUnit.SECONDS);
            System.exit(0);
        }
    }
}
