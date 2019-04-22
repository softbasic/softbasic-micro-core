package com.github.softbasic.micro.h.utils;

import java.util.UUID;

public class UUIDUtils {
    /**
     * 生成唯一标识码
     * @return
     */
    public static String create() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
