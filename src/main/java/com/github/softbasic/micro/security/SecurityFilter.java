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
            ISecurityCacheDao securityCacheDao = (SecurityCacheDao) SpringContextUtils.getBean("securityCacheDao");

            //TODO
            //为了兼容WAP，暂时不对客户端和版本号做强验证，当APP正式上线，用户逐步过渡以后,需要修改为强验证
            //为了减少开发工作，暂时将版本检测功能写死，日后需要将接口版本写入缓存，从缓存中读取和验证，同时下载地址也需要动态配置

            //验证客户端
            String client = request.getHeader("client");

            //验证接口版本
            String version = request.getHeader("version");

            if(BaseUtils.isNotBlank(version)){
                JSONObject versionServer=securityCacheDao.getVersion("version");
                if(versionServer!=null&&versionServer.get("version")!=null&&versionServer.get("downloadUrl")!=null){
                    //如果版本号不匹配
                    if(!(version.trim().equals(versionServer.getString("version").trim()))){
                        log.error("请求: " + request.getRequestURI() + "版本已过期！");
                        JSONObject data=new JSONObject();
                        data.put("downloadUrl",versionServer.get("downloadUrl"));
                        data.put("iosDownloadUrl",versionServer.get("iosDownloadUrl"));
                        MicroResult microResult = new MicroResult(true, MicroStatus.VERSION.statusCode(),versionServer.getString("info"),data);
                        response.setStatus(HttpStatus.OK.value());
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(JSON.toJSONString(microResult));
                        return;
                    }

                }
            }


            String interfaceAuth = securityCacheDao.getAuth(path);
            if (BaseUtils.isBlank(interfaceAuth)) {
                String token = request.getHeader("token");
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
                    //是否有权标识
                    boolean permit = false;
                    JSONArray interfaceInfoList = userInfo.getJSONArray("interfaceInfo");
                    for (Object o : interfaceInfoList) {
                        JSONObject interfaceInfo = JSONObject.parseObject(o.toString());
                        if (path.equals(interfaceInfo.get("url"))) {
                            permit = true;
                            break;
                        }
                    }
                    //没有匹配到权限url
                    if (!permit) {
                        log.error("请求: " + request.getRequestURI() + "无操作权限");
                        MicroResult microResult = new MicroResult(false, MicroStatus.NO_PERMIT);
                        response.setStatus(HttpStatus.OK.value());
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(JSON.toJSONString(microResult));
                        return;
                    }
                    //存入全局变量
                    MicroContext.setToken(token);
                }
            }
        }
        chain.doFilter(request, response);

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String authParam = filterConfig.getInitParameter("auth");
        if (BaseUtils.isBlank(authParam)) {
            this.auth = false;
        } else {
            this.auth = Boolean.parseBoolean(authParam);
        }
    }

    public void destroy() {
    }

}