package indi.vicliu.juaner.authorization.config.oauth.wx;

import indi.vicliu.juaner.authorization.domain.entity.OauthClientDetails;
import indi.vicliu.juaner.authorization.domain.entity.TblWeixinMiniProgramClientConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.*;

/**
 * @Auther: liuweikai
 * @Date: 2019-10-04 20:30
 * @Description:
 */
public class WeixinMiniProgramClientDetails implements ClientDetails {

    private static final int IS_VALID = 1;

    private TblWeixinMiniProgramClientConfig weixinMiniProgramClientConfig;
    private OauthClientDetails oauthClientDetails;

    public WeixinMiniProgramClientDetails(TblWeixinMiniProgramClientConfig weixinMiniProgramClientConfig, OauthClientDetails oauthClientDetails){
        this.weixinMiniProgramClientConfig = weixinMiniProgramClientConfig;
        this.oauthClientDetails = oauthClientDetails;
    }

    @Override
    public String getClientId() {
        return weixinMiniProgramClientConfig.getChannelId();
    }

    @Override
    public Set<String> getResourceIds() {
        return null;
    }

    @Override
    public boolean isSecretRequired() {
        return false;
    }

    @Override
    public String getClientSecret() {
        return null;
    }

    @Override
    public boolean isScoped() {
        return false;
    }

    @Override
    public Set<String> getScope() {
        return null;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        String[] grantTypes = this.oauthClientDetails.getAuthorizedGrantTypes().split(",");
        return new HashSet<>(Arrays.asList(grantTypes));
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return null;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return this.oauthClientDetails.getAccessTokenValidity();
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return this.oauthClientDetails.getRefreshTokenValidity();
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return false;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }
}
