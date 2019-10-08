package indi.vicliu.juaner.authorization.config.oauth;

import indi.vicliu.juaner.authorization.vo.UserInfo;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Auther: liuweikai
 * @Date: 2019-10-04 20:59
 * @Description:
 */
public abstract class AbstractCustomTokenGranter extends AbstractTokenGranter {
    protected AbstractCustomTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap(tokenRequest.getRequestParameters());
        UserInfo customUser = getCustomUser(parameters);
        if (customUser == null) {
            throw new InvalidGrantException("无法获取用户信息");
        }
        OAuth2Authentication authentication = super.getOAuth2Authentication(client, tokenRequest);
        authentication.setDetails(customUser);
        authentication.setAuthenticated(true);
        return authentication;
    }

    protected abstract UserInfo getCustomUser(Map<String, String> parameters);
}
