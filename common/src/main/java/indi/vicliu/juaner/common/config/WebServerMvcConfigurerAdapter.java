package indi.vicliu.juaner.common.config;

import indi.vicliu.juaner.common.web.interceptor.UserInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 13:59
 * @Description:
 */
public class WebServerMvcConfigurerAdapter implements WebMvcConfigurer {

    @Bean
    public HandlerInterceptor userInterceptor() {
        return new UserInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * 服务需要获取用户信息的话添加此拦截器
         */
        registry.addInterceptor(userInterceptor());
    }
}
