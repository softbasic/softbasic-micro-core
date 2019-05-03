package com.github.softbasic.micro.exception;

import com.github.softbasic.micro.config.MicroServerConfig;
import com.github.softbasic.micro.result.ErrorResult;
import com.github.softbasic.micro.result.MicroResult;
import com.github.softbasic.micro.result.MicroStatus;
import com.github.softbasic.micro.utils.StackTraceUtils;
import com.github.softbasic.micro.utils.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by LCR on 2018/1/15.
 */
@RestControllerAdvice
public class ExceptionController {
    private static Logger log = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    private MicroServerConfig serverConfig;

    @ExceptionHandler(Exception.class)
    public MicroResult handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        //如果有框架把自定义异常又封装成了系统异常，则需要重新指向自定义处理流程
        if(exception instanceof BusinessException){
            return handleBusinessException(request,response,(BusinessException)exception);
        }
        //异常日志ID
        String errorLogId= UUIDUtils.create();
        //日志堆栈
        String stackTrace=StackTraceUtils.getInfo(exception);

        //装配异常消息，异步发送
        ExceptionMessage exceptionMessage=new ExceptionMessage();
        exceptionMessage.setAddress(serverConfig.getUrl());
        exceptionMessage.setRequestUri(request.getRequestURI());
        exceptionMessage.setErrorLogId(errorLogId);
        exceptionMessage.setStatusCode(MicroStatus.ERROR.statusCode());
        exceptionMessage.setStatusMsg(MicroStatus.ERROR.statusMsg());
        exceptionMessage.setStackTraceMsg(stackTrace);

        log.error("请求: " + request.getRequestURI() + " 引起异常：");
        log.error("日志编码："+exceptionMessage.getErrorLogId());
        log.error("状态码："+exceptionMessage.getStatusCode());
        log.error("状态说明："+exceptionMessage.getStatusMsg());
        log.error(stackTrace);



        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ErrorResult();
    }

    /**
     * 自定义业务异常处理
     * @param request
     * @param response
     * @param exception
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public MicroResult handleBusinessException(HttpServletRequest request, HttpServletResponse response, BusinessException exception) {
        //日志堆栈,方法体中主动抛出的，还是catch中抛出的，二者不同
        String stackTrace= StringUtils.isBlank(exception.getStackTraceMsg())?StackTraceUtils.getInfo(exception):exception.getStackTraceMsg();

        //装配异常消息，异步发送
        ExceptionMessage exceptionMessage=new ExceptionMessage();
        exceptionMessage.setAddress(serverConfig.getUrl());
        exceptionMessage.setRequestUri(request.getRequestURI());
        exceptionMessage.setErrorLogId(exception.getErrorLogId());
        exceptionMessage.setStatusCode(exception.getMicroStatus().statusCode());
        exceptionMessage.setStatusMsg(exception.getMicroStatus().statusMsg());
        exceptionMessage.setStackTraceMsg(stackTrace);

        log.error("请求: " + request.getRequestURI() + " 引起异常：");
        log.error("日志编码："+exception.getErrorLogId());
        log.error("状态码："+exception.getMicroStatus().statusCode());
        log.error("状态说明："+exception.getMicroStatus().statusMsg());
        log.error(stackTrace);

        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new MicroResult(false,exception.getMicroStatus());
    }
}