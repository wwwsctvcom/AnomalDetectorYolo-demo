package com.mtsahakis.yolodetection.utils;

import java.util.Date;

public class TimeUtils {

    public static long getCurrentTimeSeconds() {
        Date date = new Date(System.currentTimeMillis());
        return date.getTime() / 1000;
    }

}
