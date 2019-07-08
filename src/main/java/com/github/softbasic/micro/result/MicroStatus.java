package com.github.softbasic.micro.result;

/**
 * 系统状态码
 */
public enum MicroStatus implements IMicroStatus{

    VERSION("00000","版本过期"),
    OK("00001","请求成功"),
    ERROR("00002","系统异常"),
    REDIS_LOCK_WAIT_OVERTIME("00003","获取Redis锁超时"),
    FASTDFS_UPLOAD_ERROR("00004","文件上传错误"),
    FASTDFS_UPLOAD_NOT_IMAGE("00005","图片格式错误"),
    UNAUTHORIZED("00401","未登录"),
    NO_PERMIT("00402","无权限"),
    LANDING_TIMEOUT("00403","登陆超时！"),
    USER_ID_IS_NULL("00403","登陆信息异常！")

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
