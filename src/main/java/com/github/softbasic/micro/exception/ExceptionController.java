package com.github.softbasic.micro.exception;

import com.github.softbasic.micro.model.ExceptionStatus;
import com.github.softbasic.micro.model.MicroResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by LCR on 2018/1/15.
 */
@RestControllerAdvice
public class ExceptionController {
    private static Logger log = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(Exception.class)
    public MicroResult handleException(Exception e) {
        log.info(getStackTrace(e));
        return new MicroResult(ExceptionStatus.SERVER_ERROR);
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