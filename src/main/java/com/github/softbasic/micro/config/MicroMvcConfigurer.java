package com.github.softbasic.micro.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.github.softbasic.micro.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MicroMvcConfigurer implements WebMvcConfigurer {
    /**
     * 是否启用安全验证，过滤器中使用该值
     */
    @Value("${micro.auth}")
    private Boolean auth;

    @Value("${micro.exclude}")
    private String exclude;

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
     * 配置过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean securityFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SecurityFilter());
        registration.addInitParameter("auth", auth+"");
        registration.addInitParameter("exclude",exclude+"");
        registration.addUrlPatterns(new String[]{"/*"});
        registration.setOrder(100);
        return registration;
    }

    /**
     * 跨域请求配置
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //registry.addMapping("/**").allowedOrigins(new String[]{"*"}).allowCredentials(true).allowedMethods(new String[]{"GET", "POST", "DELETE", "PUT"}).maxAge(3600L);
        //registry.addMapping("/**").allowedOrigins("*").allowCredentials(true).allowedMethods(new String[]{"GET", "POST", "DELETE", "PUT"}).exposedHeaders("Access-Control-Allow-Origin").maxAge(3600L);
        registry.addMapping("/**")
                .allowedOrigins("*").allowCredentials(true)
                .allowedMethods(new String[]{"GET", "POST", "DELETE", "PUT"})
                .allowedHeaders(new String[]{"Access-Control-Allow-Headers",
                        "Origin", "No-Cache", "X-Requested-With", "If-Modified-Since", "Pragma", "Last-Modified", "Cache-Control", "Expires", "Content-Type", "X-E4M-With","t","d"})
                .maxAge(3600L);
    }



}
