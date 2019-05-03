package com.github.softbasic.micro.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Value("${micro.auth}")
    private Boolean auth;


    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        //获取请求和相应对象
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        logger.debug("安全验证开关："+auth+"");

        //如果不需要安全验证，则直接处理请求
        if(auth==null||(!auth))
            chain.doFilter(request, response);
        else
            chain.doFilter(request, response);


    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }
    public void destroy() {
    }
}