package com.github.softbasic.micro.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.softbasic.micro.config.MicroContext;
import com.github.softbasic.micro.exception.BusinessException;
import com.github.softbasic.micro.exception.ExceptionMessage;
import com.github.softbasic.micro.log.MicroLoggerFactory;
import com.github.softbasic.micro.result.MicroResult;
import com.github.softbasic.micro.result.MicroStatus;
import com.github.softbasic.micro.utils.BaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.github.softbasic.micro.result.MicroStatus.UNAUTHORIZED;

public class SecurityFilter implements Filter {
    private static final Logger log = MicroLoggerFactory.getLogger(SecurityFilter.class);

    /**
     * 是否启用安全验证
     */
    private Boolean auth;



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        //获取请求和相应对象
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        log.debug("安全验证开关："+auth+"");

        //AJAX跨域预检查
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpStatus.OK.value());
            chain.doFilter(request, response);
            return;
        }

        //如果需要安全验证
        if(this.auth != null && this.auth){
            String token = request.getHeader("token");

            /**
             * 如果token验证不通过，则抛401异常
             * filter在controller之前，所以，自定义异常，ExceptionController接收不到
             */
            if(BaseUtils.isBlank(token)){

                log.error("请求: " + request.getRequestURI() + "无操作权限");
                MicroResult microResult=new MicroResult(false, MicroStatus.UNAUTHORIZED);
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Headers", "accept,content-type");
                response.setHeader("Access-Control-Allow-Methods", "OPTIONS,GET,POST,DELETE,PUT");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Cache-Control", "no-cache, must-revalidate");
                response.getWriter().write(JSON.toJSONString(microResult));
                return;
            }
            //验证缓存中是否有
            if(1==1){

            }

            //存入全局变量
            MicroContext.setToken(token);


        }

        chain.doFilter(request, response);

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String authParam = filterConfig.getInitParameter("auth");
        if(BaseUtils.isBlank(authParam)){
            this.auth=false;
        }else{
            this.auth=Boolean.parseBoolean(authParam);
        }
    }

    public void destroy() {
    }
}