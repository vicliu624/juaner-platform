package indi.vicliu.juaner.authorization.config.custom;

import com.google.common.collect.Maps;
import indi.vicliu.juaner.authorization.provider.UpmsProvider;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-10 17:50
 * @Description:
 */
@Slf4j
public class CustomTokenEnhancer implements TokenEnhancer {

    private UpmsProvider upmsProvider;

    public CustomTokenEnhancer(UpmsProvider upmsProvider){
        this.upmsProvider = upmsProvider;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        log.info( "authentication name:{} ",authentication.getName());
        Map<String, Object> additionalInfo = Maps.newHashMap();
        //自定义token内容
        additionalInfo.put("user", authentication.getName());
        try{
            Result ret = this.upmsProvider.getFullUserInfo(authentication.getName());
            if(ret.isSuccess()){
                Map<String,Object> data = (HashMap<String,Object>)ret.getData();
                additionalInfo.put("user_id",data.get("id"));
            }
        } catch (Exception e){
            log.error("获取用户编号时失败",e);
        }

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
