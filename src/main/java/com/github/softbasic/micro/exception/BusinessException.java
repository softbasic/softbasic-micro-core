package com.github.softbasic.micro.exception;

import com.github.softbasic.micro.result.IMicroStatus;
import com.github.softbasic.micro.utils.StackTraceUtils;
import com.github.softbasic.micro.utils.UUIDUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

@Data
@EqualsAndHashCode(callSuper=true)
public class BusinessException extends RuntimeException{
    private static final Logger logger = LoggerFactory.getLogger(BusinessException.class);

    private IMicroStatus microStatus;//状态码
    private String errorLogId;//异常日志编码
    private String stackTraceMsg="";//异常堆栈
    /**
     * 主动创建异常
     * @param microStatus
     */
    public BusinessException(IMicroStatus microStatus){
        super(microStatus.statusMsg());
        this.microStatus=microStatus;
        this.errorLogId= UUIDUtils.create();
    }

    /**
     * 传递底层异常堆栈
     * @param microStatus
     * @param exception
     */
    public BusinessException(IMicroStatus microStatus, Exception exception){
        super(microStatus.statusMsg());
        this.errorLogId= UUIDUtils.create();
        this.stackTraceMsg=getInfo(exception);
        //如果捕获的是自定义异常，则类型定为源异常，否则使用捕获时异常
        if(exception instanceof BusinessException){
            this.microStatus=((BusinessException) exception).microStatus;
        }else{
            this.microStatus=microStatus;
        }
    }

    /**
     * 获取异常的堆栈信息
     *
     * @param t
     * @return
     */
    public  String getInfo(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            t.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

}
