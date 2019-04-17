package com.github.softbasic.micro.model;

/**
 * 系统状态码
 */
public enum ExceptionStatus {

    SERVER_ERROR(500,"服务器处理异常");

    //状态码
    private int statusCode;
    //状态说明
    private String statusMsg;

    ExceptionStatus(int statusCode, String statusMsg) {
        this.statusCode=statusCode;
        this.statusMsg=statusMsg;
    }
    public int statusCode() {
        return this.statusCode;
    }
    public String statusMsg() {
        return this.statusMsg;
    }
}
