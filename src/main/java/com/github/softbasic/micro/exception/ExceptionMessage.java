package com.github.softbasic.micro.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * 异常消息
 */
@Data
public class ExceptionMessage implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    //地址
    private String address ;
    //请求地址
    private String requestUri;
    //异常日志编码
    private String errorLogId;
    //状态码
    private String statusCode;
    //状态说明
    private String statusMsg;
    //异常堆栈
    private String stackTraceMsg;
}