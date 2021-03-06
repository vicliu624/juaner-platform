package indi.vicliu.juaner.authorization.config.oauth;

import indi.vicliu.juaner.authorization.config.oauth.custom.provider.sms.AbstractSmsCodeUserDetailsAuthenticationProvider;
import indi.vicliu.juaner.authorization.config.oauth.custom.provider.wechat.AbstractWeChatCodeUserDetailsAuthenticationProvider;
import indi.vicliu.juaner.authorization.domain.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Auther: liuweikai
 * @Date: 2019/3/18 13:40
 * @Description:
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AbstractSmsCodeUserDetailsAuthenticationProvider smsCodeUserDetailsAuthenticationProvider;

    @Autowired
    private AbstractWeChatCodeUserDetailsAuthenticationProvider weChatCodeUserDetailsAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.requestMatchers()
                .antMatchers("/login", "/oauth/authorize","/oauth/token")
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll();

        http.authenticationProvider(smsCodeUserDetailsAuthenticationProvider);
        http.authenticationProvider(weChatCodeUserDetailsAuthenticationProvider);
    }

    /**
     * 注入自定义的userDetailsService实现，获取用户信息，设置密码加密方式
     *
     * @param authenticationManagerBuilder
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    /**
     * 将 AuthenticationManager 注册为 bean , 方便配置 oauth server 的时候使用
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
