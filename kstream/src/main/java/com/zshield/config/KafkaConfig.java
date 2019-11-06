package com.zshield.config;

import com.zshield.util.TimeUtil;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class KafkaConfig {
    public static final String es_type = "datamap";
    public static final String es_tmpl = "datamap_precompute";

    public static final int BULK_SIZE = 3000;
    public static final int BULK_INTERVAL = 60 * 1000;

    public static String getIndex() {
        LocalDate nowDate = LocalDate.now();
        return "datamap_precompute" + "-" +nowDate.format(TimeUtil.DATE_FORMATTER_INDEX);
    }

    public static String getTreeIndex() {
        LocalDate nowDate = LocalDate.now();
        return "datamap_precompute_tree" + "-" + nowDate.format(TimeUtil.DATE_FORMATTER_INDEX);
    }
}
