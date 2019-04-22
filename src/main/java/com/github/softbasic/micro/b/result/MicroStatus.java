package com.github.softbasic.micro.b.result;

/**
 * 系统状态码
 */
public enum MicroStatus implements IMicroStatus{

    OK("0001","请求成功"),
    ERROR("0002","系统异常");;

    //状态码
    private String statusCode;
    //状态说明
    private String statusMsg;

    MicroStatus(String statusCode, String statusMsg) {
        this.statusCode=statusCode;
        this.statusMsg=statusMsg;
    }
    public String statusCode() {
        return this.statusCode;
    }
    public String statusMsg() {
        return this.statusMsg;
    }


}
