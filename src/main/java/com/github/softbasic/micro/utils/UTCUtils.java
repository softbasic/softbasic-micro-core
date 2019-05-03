package com.github.softbasic.micro.utils;

import com.github.softbasic.micro.utils.date.DateStyle;
import com.github.softbasic.micro.utils.date.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class UTCUtils {
    public static String getUtcTime(){
        //1、取得本地时间：
        final java.util.Calendar cal = java.util.Calendar.getInstance();

        //2、取得时间偏移量：

        final int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

        //3、取得夏令时差：

        final int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

        //4、从本地时间里扣除这些差量，即可以取得UTC时间：

        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        return DateUtil.DateToString(cal.getTime(), DateStyle.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * utc时间转成local时间
     * @param utcTime
     * @return
     */
    public static String utcToLocal(String utcTime) throws Exception{
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date utcDate = sdf.parse(utcTime);
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(utcDate.getTime());
        }catch (Exception e){
           throw e;
        }
    }
}
