package com.github.softbasic.micro.exception;

import com.github.softbasic.micro.result.ErrorResult;
import com.github.softbasic.micro.result.MicroResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by LCR on 2018/1/15.
 */
@RestControllerAdvice
public class ExceptionController {
    private static Logger log = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(Exception.class)
    public MicroResult handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) {

        log.error("Request: " + request.getRequestURI() + " raised " + getStackTrace(exception));

        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ErrorResult();
    }
    /**
     * 获取异常的堆栈信息
     *
     * @param t
     * @return
     */
    private static String getStackTrace(Throwable t) {
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