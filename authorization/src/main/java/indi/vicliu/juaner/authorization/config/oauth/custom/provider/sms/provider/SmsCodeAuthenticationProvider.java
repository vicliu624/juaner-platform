package indi.vicliu.juaner.authorization.config.oauth.custom.provider.sms.provider;

import indi.vicliu.juaner.authorization.config.oauth.custom.provider.CustomAuthenticationToken;
import indi.vicliu.juaner.authorization.config.oauth.custom.provider.AbstractCustomUserDetailsAuthenticationProvider;
import indi.vicliu.juaner.authorization.domain.service.impl.CustomUserDetailsService;
import indi.vicliu.juaner.authorization.provider.SmsProvider;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * @Auther: liuweikai
 * @Date: 2019-12-08 11:49
 * @Description:
 */
@Component
@Data
@Slf4j
public class SmsCodeAuthenticationProvider extends AbstractCustomUserDetailsAuthenticationProvider {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SmsProvider smsProvider;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, CustomAuthenticationToken authentication) throws AuthenticationException {

    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomAuthenticationToken authenticationToken = (CustomAuthenticationToken) authentication;
        //调用自定义的userDetailsService认证

        UserDetails user = customUserDetailsService.loadUserByUsername((String) authenticationToken.getPrincipal());

        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        if (authentication.getCredentials() == null) {
            log.debug("授权失败: 为提供短信验证码");

            throw new BadCredentialsException(messages.getMessage(
                    "SmsCodeAuthenticationProvider.badCredentials",
                    "未提供验证码"));
        }

        if (user.getAuthorities().isEmpty()){
            log.debug("授权失败: 该用户未分配角色");

            throw new BadCredentialsException(messages.getMessage(
                    "SmsCodeAuthenticationProvider.badCredentials",
                    "未分配角色"));
        }

        String presentedSmsCode = authentication.getCredentials().toString();

        String activeProfile = context.getEnvironment().getActiveProfiles()[0];
        log.debug("active profile:{}",activeProfile);

        //测试环境开发环境都是固定的短信码
        if(activeProfile.toLowerCase().startsWith("test") || activeProfile.toLowerCase().startsWith("dev")){
            if(!"111111".equals(presentedSmsCode)){
                throw new BadCredentialsException(messages.getMessage(
                        "SmsCodeAuthenticationProvider.badCredentials",
                        "短信验证码错误"));
            }
        } else {
            Result result = smsProvider.verifyByUsername((String)authentication.getPrincipal(),presentedSmsCode);
            if(result.isFail()){
                throw new BadCredentialsException(messages.getMessage(
                        "SmsCodeAuthenticationProvider.badCredentials",
                        "短信验证码错误"));
            }
        }

        CustomAuthenticationToken authenticationResult = new CustomAuthenticationToken(authenticationToken.getPrincipal(), authenticationToken.getCredentials() ,user.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    @Override
    protected UserDetails retrieveUser(String username, CustomAuthenticationToken authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
