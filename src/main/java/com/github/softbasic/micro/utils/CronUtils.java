package com.github.softbasic.micro.utils;

import com.github.softbasic.micro.utils.date.DateStyle;
import com.github.softbasic.micro.utils.date.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CronUtils {

    private static final String CRON_DATE_FORMAT = "ss mm HH dd MM ? yyyy";


    /**
     * 获取Cron表达式
     * @param date
     * @return
     */
    public static String getCron( Date  date){
        SimpleDateFormat sdf = new SimpleDateFormat(CRON_DATE_FORMAT);
        return  sdf.format(date);
    }

    /**
     * 获取UTC时间的Cron表达式
     * @param date
     * @return
     */
    public static String getCronByUtc( String  date) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat(CRON_DATE_FORMAT);
        String local=UTCUtils.utcToLocal(date);
        return  sdf.format(DateUtil.StringToDate(local, DateStyle.YYYY_MM_DD_HH_MM_SS));
    }
}
