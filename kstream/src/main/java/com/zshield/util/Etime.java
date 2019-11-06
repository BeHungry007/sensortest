package com.zshield.util;

import java.time.format.DateTimeFormatter;


public class Etime {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd\'T\'HH:mm:ss+08:00");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_FORMATTER_INDEX = DateTimeFormatter.ofPattern("yyyy.MM");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("1971-01-01\'T\'HH:mm:ss+08:00");
}
