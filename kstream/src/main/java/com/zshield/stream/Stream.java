package com.zshield.stream;


import com.zshield.stream.precompute.proc.GenericQueryProc;
import com.zshield.stream.precompute.proc.PreProc;
import com.zshield.util.ConfigUtil;
import com.zshield.util.ParamAnalysisUtil;
import com.zshield.util.ThreadUtil;
import com.zshield.util.TimeUtil;
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

public class Stream {
    private Topology topo;
    private StoreBuilder builder;
    private static Thread thread;
    private static CountDownLatch latch;
    private static final Logger logger = LoggerFactory.getLogger(Stream.class);

    public Stream() {
        topo = new Topology();
        KeyValueBytesStoreSupplier tsup = Stores.inMemoryKeyValueStore("qstore");
        builder = Stores.keyValueStoreBuilder(tsup, Serdes.String(), Serdes.String());
    }

    public static void startUp(String threadName) {
        if (thread == null) {
            Stream stream = new Stream();
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
                .addProcessor("pre_proc", () -> new PreProc(), "source")
                .addProcessor("query_proc", () -> new GenericQueryProc(), "pre_proc")
                .addStateStore(builder, "query_proc", "pre_proc")
                .addSink("Generic_query_proc", ParamAnalysisUtil.medium_topic, "query_proc");

        if (!ParamAnalysisUtil.is_upgrade) {
            topo.addSink("sensor_out", ParamAnalysisUtil.output_topic, "source");
        }

        final KafkaStreams streams = new KafkaStreams(topo , ConfigUtil.baseStreamProps);

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
