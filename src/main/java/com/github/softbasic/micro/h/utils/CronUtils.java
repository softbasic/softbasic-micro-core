package com.github.softbasic.micro.h.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CronUtils {
    private static final String CRON_DATE_FORMAT = "ss mm HH dd MM ? yyyy";
    private static SimpleDateFormat sdf = new SimpleDateFormat(CRON_DATE_FORMAT);

    /**
     * 获取Cron表达式
     * @param date
     * @return
     */
    public static String getCron( Date  date){
        return  sdf.format(date);
    }

}
