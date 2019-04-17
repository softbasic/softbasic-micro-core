package com.github.softbasic.micro.model;

import lombok.Data;

@Data
public class MicroResult {
    //请求成功
    private boolean isSuccess;
    //状态码
    private int statusCode;
    //状态说明
    private String statusMsg;
    //数据
    private Object data;

    /**
     * 构造器
     * @param isSuccess
     * @param statusCode
     * @param statusMsg
     * @param data
     */
    public MicroResult(boolean isSuccess,int statusCode,String statusMsg,Object data){
        this.isSuccess=isSuccess;
        this.statusCode=statusCode;
        this.statusMsg=statusMsg;
        this.data=data;
    }

    /**
     * 请求处理成功
     * @param status
     * @param data
     */
    public MicroResult(SuccessStatus status, Object data){
        this(true,status.statusCode(),status.statusMsg(),data);
    }

    /**
     * 请求处理成功
     * @param status
     */
    public MicroResult(SuccessStatus status){
        this(true,status.statusCode(),status.statusMsg(),"");
    }

    /**
     * 请求处理错误
     * @param status
     * @param data
     */
    public MicroResult(ExceptionStatus status, Object data){
        this(false,status.statusCode(),status.statusMsg(),data);
    }

    /**
     * 请求处理错误
     * @param status
     */
    public MicroResult(ExceptionStatus status){
        this(false,status.statusCode(),status.statusMsg(),"");
    }
}
