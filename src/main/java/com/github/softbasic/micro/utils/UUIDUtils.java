package com.github.softbasic.micro.utils;

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
