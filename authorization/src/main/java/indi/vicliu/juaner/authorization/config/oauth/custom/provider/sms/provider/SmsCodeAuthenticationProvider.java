package indi.vicliu.juaner.authorization.config.oauth.custom.provider.sms.provider;

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.authorization.Constant;
import indi.vicliu.juaner.authorization.config.oauth.custom.provider.sms.SmsCodeAuthenticationToken;
import indi.vicliu.juaner.authorization.domain.service.impl.CustomUserDetailsService;
import indi.vicliu.juaner.authorization.provider.SmsProvider;
import indi.vicliu.juaner.authorization.provider.UpmsProvider;
import indi.vicliu.juaner.authorization.utils.RedisStringUtil;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: liuweikai
 * @Date: 2019-12-08 11:49
 * @Description:
 */
@Component
@Data
@Slf4j
public class SmsCodeAuthenticationProvider extends AbstractSmsCodeUserDetailsAuthenticationProvider {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SmsProvider smsProvider;

    @Autowired
    private UpmsProvider upmsProvider;

    @Autowired
    private RedisStringUtil redisStringUtil;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, SmsCodeAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            log.debug("授权失败: 为提供短信验证码");

            throw new BadCredentialsException(messages.getMessage(
                    "SmsCodeAuthenticationProvider.badCredentials",
                    "未提供验证码"));
        }

        if (userDetails.getAuthorities().isEmpty()){
            log.debug("授权失败: 该用户未分配角色");

            throw new BadCredentialsException(messages.getMessage(
                    "SmsCodeAuthenticationProvider.badCredentials",
                    "未分配角色"));
        }

        String presentedSmsCode = authentication.getCredentials().toString();

        String activeProfile = context.getEnvironment().getActiveProfiles()[0];
        log.debug("active profile:{}",activeProfile);

        //测试环境开发环境都是固定的短信码
        if(activeProfile.toLowerCase().startsWith("dev")){
            if(!"111111".equals(presentedSmsCode)){
                throw new BadCredentialsException(messages.getMessage(
                        "SmsCodeAuthenticationProvider.badCredentials",
                        "短信验证码错误"));
            }
        } else {
            String userName = (String)authentication.getPrincipal();
            String key = Constant.LOCK_KEY + userName;
            String value = redisStringUtil.getValue(key);
            if(!StringUtils.isEmpty(value)){
                log.warn("用户{},短信验证码输入错误次数{}",userName,value);
                if(StringUtils.isNumeric(value)){
                    int failCount = Integer.parseInt(value);
                    if(failCount >= 3){
                        //在这里加上错误次数限制 达到上限后锁定账户
                        Result ret = upmsProvider.lockUser(userName);
                        if(ret.isSuccess()){
                            throw new BadCredentialsException(messages.getMessage(
                                    "SmsCodeAuthenticationProvider.badCredentials",
                                    (String) ret.getData()));
                        }
                        throw new BadCredentialsException(messages.getMessage(
                                "SmsCodeAuthenticationProvider.badCredentials",
                                "用户被锁定"));
                    }
                } else {
                    throw new BadCredentialsException(messages.getMessage(
                            "SmsCodeAuthenticationProvider.badCredentials",
                            "短信验证码错误"));
                }
            }

            Result result = smsProvider.verifyV2ByUsername(userName,presentedSmsCode);
            if(result.isFail()){
                log.warn("smsProvider.verifyByUsername 应答:{}", JSONObject.toJSONString(result));
                if(StringUtils.isEmpty(value)){
                    value = "1";
                } else {
                    if(StringUtils.isNumeric(value)){
                        int temp = Integer.parseInt(value);
                        temp += 1;
                        value = String.valueOf(temp);
                    } else {
                        value = "1";
                    }
                }
                log.warn("smsProvider应答用户{}当前短信验证码错误次数{}",userName,value);
                redisStringUtil.setKeyExpire(key,value,1, TimeUnit.DAYS);
                throw new BadCredentialsException(messages.getMessage(
                        "SmsCodeAuthenticationProvider.badCredentials",
                        "短信验证码错误,剩余尝试次数" + (3 - Integer.parseInt(value))));
            } else {
                redisStringUtil.delKey(key);
            }
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        authentication = super.authenticate(authentication);
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(authenticationToken.getPrincipal(), authenticationToken.getCredentials() ,authentication.getAuthorities());
        return authenticationResult;
    }

    @Override
    protected UserDetails retrieveUser(String username, SmsCodeAuthenticationToken authentication) throws AuthenticationException {
        return customUserDetailsService.loadUserByUsername((String) authentication.getPrincipal());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
