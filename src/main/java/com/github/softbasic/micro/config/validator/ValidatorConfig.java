package com.github.softbasic.micro.config.validator;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Configuration
public class ValidatorConfig {
    @Value("${hibernateValidator.failFast}")
    private boolean failFast;

    /**
     * failFast(false):校验普通模式(会校验完所有的属性，然后返回所有的验证失败信息)
     * failFast(true):校验快速失败模式(只要有一个验证失败，则返回)
     * @return
     */
    @Bean
    public Validator validator(){
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(failFast)
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        return validator;
    }
}
