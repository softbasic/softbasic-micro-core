package com.github.softbasic.micro.utils;

import com.github.softbasic.micro.log.MicroLogger;
import com.github.softbasic.micro.log.MicroLoggerFactory;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

@Component
public class SpringValueUtils implements EmbeddedValueResolverAware {

    private static final MicroLogger log = MicroLoggerFactory.getLogger(SpringValueUtils.class);

    private static StringValueResolver resolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        if (SpringValueUtils.resolver == null) {
            SpringValueUtils.resolver = resolver;
        }
    }

    //获取StringValueResolver
    private static StringValueResolver getStringValueResolver() {
        return resolver;
    }

    //通过name获取 .
    public static String getValue(String name) {
        String result = null;
        try {
            result = resolver.resolveStringValue("${" + name + "}");
        } catch (IllegalArgumentException i) {
            log.error("未从Spring容器中找到" + name + "的值");
        }
        return result;
    }
}
