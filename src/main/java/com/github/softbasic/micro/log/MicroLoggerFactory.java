package com.github.softbasic.micro.log;

import org.slf4j.LoggerFactory;

public class MicroLoggerFactory {
    public static MicroLogger getLogger(Class cls) {
        return new MicroLogger(LoggerFactory.getLogger(cls));
    }
}