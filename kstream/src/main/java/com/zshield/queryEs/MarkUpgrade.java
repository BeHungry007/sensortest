package com.zshield.queryEs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class MarkUpgrade {

    private File f = new File("/var/log/kstream/upgrade_finished_mark");
    private static final Logger logger = LoggerFactory.getLogger(MarkUpgrade.class);

    public void createFile() {
        try {
            f.createNewFile();
        } catch (IOException e) {
            logger.error("[file create exception]", e);
        }
    }

    public boolean isFileExist() {
        return f.exists();
    }

    public void deleteDile() {
        f.delete();
    }
}
