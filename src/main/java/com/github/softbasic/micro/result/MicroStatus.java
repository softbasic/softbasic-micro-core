package com.github.softbasic.micro.result;

/**
 * 系统状态码
 */
public enum MicroStatus implements IMicroStatus{

    OK("00001","请求成功"),
    ERROR("00002","系统异常"),
    REDIS_LOCK_WAIT_OVERTIME("00003","获取Redis锁超时"),
    FASTDFS_UPLOAD_ERROR("00004","文件上传错误"),
    FASTDFS_UPLOAD_NOT_IMAGE("00005","图片格式错误")


    ;

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
