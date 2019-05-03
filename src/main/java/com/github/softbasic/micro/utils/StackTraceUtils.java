package com.github.softbasic.micro.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtils {
    /**
     * 获取异常的堆栈信息
     *
     * @param t
     * @return
     */
    public static String getInfo(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            t.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }
}
