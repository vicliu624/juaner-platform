package indi.vicliu.juaner.authorization.config.custom;

import com.google.common.collect.Maps;
import indi.vicliu.juaner.authorization.security.core.userdetails.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-10 17:50
 * @Description:
 */
@Slf4j
public class CustomTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        log.info( "authentication name:{} ",authentication.getName());
        Map<String, Object> additionalInfo = Maps.newHashMap();
        //自定义token内容
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        additionalInfo.put("user_id",customUser.getUserId());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
