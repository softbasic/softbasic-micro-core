package com.github.softbasic.micro.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.github.softbasic.micro.model.BaseDto;
import com.github.softbasic.micro.model.PageParam;
import com.github.softbasic.micro.security.SecurityFilter;
import com.github.softbasic.micro.utils.BaseUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.*;

@Configuration
public class MicroMvcConfigurer implements WebMvcConfigurer {

    /**
     * 配置fastJson消息处理器
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        // 处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(fastConverter);
    }

    /**
     * 自定义参数解析器，解析DTO，其中分页参数特殊处理
     * @param argumentResolvers
     */
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new HandlerMethodArgumentResolver() {
            public boolean supportsParameter(MethodParameter methodParameter) {
                return BaseDto.class.isAssignableFrom(methodParameter.getParameterType());
            }

            public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory)
                    throws Exception {
                HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest(HttpServletRequest.class);
                Class cls = methodParameter.getParameterType();
                Object obj = cls.newInstance();
                String pageJson = request.getParameter("page");
                if (BaseUtils.isNotBlank(pageJson)) {
                    ((BaseDto) obj).setPage((PageParam) JSON.parseObject(pageJson, PageParam.class));
                }
                return BaseUtils.parseObject(obj, request);
            }
        });
    }
    /**
     * 配置过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean securityFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SecurityFilter());
        registration.addUrlPatterns(new String[]{"/*"});
        registration.setOrder(1);
        return registration;
    }



}
