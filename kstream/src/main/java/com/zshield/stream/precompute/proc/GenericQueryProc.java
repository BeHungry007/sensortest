package com.zshield.stream.precompute.proc;

import com.google.gson.Gson;
import com.zshield.stream.precompute.entry.EntryInterface;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

public class GenericQueryProc extends AbstractProcessor<String, EntryInterface> {
    private ProcessorContext context;
    private Gson gson;
    private KeyValueStore<String, String> kv;
    private Map<String, String> map;
    private static final Logger logger = LoggerFactory.getLogger(GenericQueryProc.class);


    @Override
    public void process(String s, EntryInterface newEntry) {
        String docId = newEntry.getDocId();
        String store = kv.get(docId);
        if (store != null) {
            newEntry.update(store);
        }
        String result = this.gson.toJson(newEntry);
        kv.put(docId, result);
        try {
            docId = URLEncoder.encode(docId, "UTF-8");
            map.put(docId, result);

        } catch (Throwable e) {
            logger.error(this.gson.toJson(newEntry));
            logger.error("[GenericQueryProc process exception]", e);
        }
    }
}
