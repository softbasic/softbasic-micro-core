package com.github.softbasic.micro.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.softbasic.micro.config.MicroContext;
import com.github.softbasic.micro.log.MicroLoggerFactory;
import com.github.softbasic.micro.result.MicroResult;
import com.github.softbasic.micro.result.MicroStatus;
import com.github.softbasic.micro.utils.BaseUtils;
import com.github.softbasic.micro.utils.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class SecurityFilter implements Filter {
    private static final Logger log = MicroLoggerFactory.getLogger(SecurityFilter.class);

    /**
     * 是否启用安全验证
     */
    private Boolean auth;

    private String exclude;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        //获取请求和相应对象
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //AJAX跨域预检查
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpStatus.OK.value());
            chain.doFilter(request, response);
            return;
        }


        //如果需要安全验证
        if (this.auth != null && this.auth) {
            String path = request.getRequestURI();
            String[] excludes = StringUtils.isEmpty(this.exclude)?new String[0]:this.exclude.contains(",")?this.exclude.split(","):new String[]{this.exclude};

            for(String p : excludes){
                if(path.contains(p)){
                    chain.doFilter(request, response);
                    return;
                }
            }



            ISecurityCacheDao securityCacheDao = (SecurityCacheDao) SpringContextUtils.getBean("securityCacheDao");

            String token = request.getHeader("t");
            /*
             * 如果token验证不通过，则抛401异常
             * filter在controller之前，所以，自定义异常，ExceptionController接收不到
             */
            if (BaseUtils.isBlank(token)) {
                log.error("请求: " + request.getRequestURI() + "用户尚未登陆！");
                MicroResult microResult = new MicroResult(false, MicroStatus.UNAUTHORIZED);
                response.setStatus(HttpStatus.OK.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(JSON.toJSONString(microResult));
                return;
            }

            JSONObject userInfo = securityCacheDao.getUserInfo(token);
            //验证缓存中是否有
            if (userInfo == null) {
                log.error("请求: " + request.getRequestURI() + "登陆已超时！");
                MicroResult microResult = new MicroResult(false, MicroStatus.LANDING_TIMEOUT);
                response.setStatus(HttpStatus.OK.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(JSON.toJSONString(microResult));
                return;
            } else {
                //存入全局变量
                MicroContext.setToken(token);
            }

        }
        chain.doFilter(request, response);

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String authParam = filterConfig.getInitParameter("auth");
        String excludeParam = filterConfig.getInitParameter("exclude");
        if (BaseUtils.isBlank(authParam)) {
            this.auth = false;
        } else {
            this.auth = Boolean.parseBoolean(authParam);
        }
        this.exclude = excludeParam;
    }

    public void destroy() {
    }

}