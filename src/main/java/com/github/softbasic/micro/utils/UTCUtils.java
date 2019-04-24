package com.github.softbasic.micro.utils;

import com.github.softbasic.micro.utils.date.DateStyle;
import com.github.softbasic.micro.utils.date.DateUtil;

public class UTCUtils {
    public static String getDateTime(){
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
}
