package com.zshield.stream.precompute.entry;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface EntryInterface {
    static final Logger logger = LoggerFactory.getLogger(EntryInterface.class);
    static Gson gson = new Gson();

    EntryInterface create(JsonObject log);

    public String getDocId();

    public void update(String store);
}
