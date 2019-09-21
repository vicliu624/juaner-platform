package indi.vicliu.juaner.authorization.config;

import indi.vicliu.juaner.authorization.exception.CustomWebResponseExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

/**
 * @Auther: liuweikai
 * @Date: 2019/3/18 13:38
 * @Description:
 */
@Configuration
public class ServiceConfig {
    @Bean
    public WebResponseExceptionTranslator webResponseExceptionTranslator() {
        return new CustomWebResponseExceptionTranslator();
    }
}