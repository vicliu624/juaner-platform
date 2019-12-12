package indi.vicliu.juaner.authorization.config.oauth.custom.provider.wechat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;

/**
 * @Auther: liuweikai
 * @Date: 2019-12-09 21:40
 * @Description:
 */
@Slf4j
public class WechatAuthorizationCodeServicesImpl implements AuthorizationCodeServices {


    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        log.info("wechat createAuthorizationCode");
        return null;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        log.info("wechat consumeAuthorizationCode");
        OAuth2Request storedRequest = null;
        Authentication userAuthentication = null;
        OAuth2Authentication authentication = new OAuth2Authentication(storedRequest,userAuthentication);
        return authentication;
    }
}
