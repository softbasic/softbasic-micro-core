package com.github.softbasic.micro.result;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class MicroResult {
    //请求成功
    private boolean isSuccess;
    //状态码
    private String statusCode;
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
    public MicroResult(boolean isSuccess,String statusCode,String statusMsg,Object data){
        this.isSuccess=isSuccess;
        this.statusCode=statusCode;
        this.statusMsg=statusMsg;
        this.data=data;
    }

    /**
     * 请求处理设置返回值
     * @param status
     * @param data
     */
    public MicroResult(boolean isSuccess,IMicroStatus status, Object data){
        this(isSuccess,status.statusCode(),status.statusMsg(),data);
    }

    /**
     * 请求处理设置默认返回值
     * @param status
     */
    public MicroResult(boolean isSuccess,IMicroStatus status){
        this(isSuccess,status.statusCode(),status.statusMsg(),new JSONObject());
    }


}
