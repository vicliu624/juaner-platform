package indi.vicliu.juaner.authorization.config.oauth.custom.provider.wechat;

import indi.vicliu.juaner.authorization.data.mapper.TblWeixinAppConfigMapper;
import indi.vicliu.juaner.authorization.domain.service.impl.CustomUserDetailsService;
import indi.vicliu.juaner.authorization.provider.UpmsProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
@Data
@Slf4j
public class WeChatCodeAuthenticationProvider extends AbstractWeChatCodeUserDetailsAuthenticationProvider {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UpmsProvider upmsProvider;

    @Autowired
    private TblWeixinAppConfigMapper weixinAppConfigMapper;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, WeChatCodeAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            log.debug("授权失败: 未提供code");

            throw new BadCredentialsException(messages.getMessage(
                    "SmsCodeAuthenticationProvider.badCredentials",
                    "未提供code"));
        }

        if (userDetails.getAuthorities().isEmpty()){
            log.debug("授权失败: 该用户未分配角色");

            throw new BadCredentialsException(messages.getMessage(
                    "WeChatCodeAuthenticationProvider.badCredentials",
                    "未分配角色"));
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        authentication = super.authenticate(authentication);
        WeChatCodeAuthenticationToken authenticationToken = (WeChatCodeAuthenticationToken) authentication;
        WeChatCodeAuthenticationToken authenticationResult = new WeChatCodeAuthenticationToken(authenticationToken.getPrincipal(), authenticationToken.getCredentials() ,authentication.getAuthorities());
        return authenticationResult;
    }

    @Override
    protected UserDetails retrieveUser(String username, WeChatCodeAuthenticationToken authentication) throws AuthenticationException {
        return customUserDetailsService.loadUserByUsername((String) authentication.getPrincipal());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WeChatCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
