package com.github.softbasic.micro.model;

/**
 * 系统状态码
 */
public enum SuccessStatus {

    OK(200,"请求成功");

    //状态码
    private int statusCode;
    //状态说明
    private String statusMsg;

    SuccessStatus(int statusCode, String statusMsg) {
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
