package indi.vicliu.juaner.authorization.config.oauth.custom.provider.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.authorization.data.mapper.TblWeixinAppConfigMapper;
import indi.vicliu.juaner.authorization.domain.entity.TblWeixinAppConfig;
import indi.vicliu.juaner.authorization.provider.UpmsProvider;
import indi.vicliu.juaner.authorization.vo.AccessTokenResp;
import indi.vicliu.juaner.authorization.vo.BaseResp;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ResourceOwnerWeChatTokenGranter extends AbstractTokenGranter {
    private static final String GRANT_TYPE = "wx_code";

    private final static String ACHIEVE_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type"
            + "=authorization_code";

    private TblWeixinAppConfigMapper weixinAppConfigMapper;

    private RestTemplate restTemplate;

    private UpmsProvider upmsProvider;

    private final AuthenticationManager authenticationManager;

    public ResourceOwnerWeChatTokenGranter(AuthenticationManager authenticationManager,
                                        AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory
                                        ,TblWeixinAppConfigMapper weixinAppConfigMapper,RestTemplate restTemplate,UpmsProvider upmsProvider) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE,weixinAppConfigMapper,restTemplate,upmsProvider);
    }

    protected ResourceOwnerWeChatTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                                           ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType
            ,TblWeixinAppConfigMapper weixinAppConfigMapper,RestTemplate restTemplate,UpmsProvider upmsProvider) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
        this.weixinAppConfigMapper = weixinAppConfigMapper;
        this.restTemplate = restTemplate;
        this.upmsProvider = upmsProvider;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        log.debug("parameters:{}",JSON.toJSONString(parameters));
        String code = parameters.get("code");
        // Protect from downstream leaks of password
        parameters.remove("code");

        //根据code取到unionId
        TblWeixinAppConfig weixinAppConfig = weixinAppConfigMapper.selectByPrimaryKey(client.getClientId());
        if(weixinAppConfig == null){
            throw new InvalidGrantException("查询不到微信开发配置");
        }

        String s = buildAccessTokenUrl(ACHIEVE_ACCESS_TOKEN, weixinAppConfig.getAppId(),
                weixinAppConfig.getAppSecret(), code);
        ResponseEntity<String> stringResponseEntity = restTemplate.getForEntity(s, String.class);
        AccessTokenResp accessTokenResp = convertAndCheck(stringResponseEntity, AccessTokenResp.class);
        log.info("微信返回:{}", JSONObject.toJSONString(accessTokenResp));

        //取unionId接口文档 https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html#%E8%AF%B7%E6%B1%82%E5%9C%B0%E5%9D%80
        String unionId = accessTokenResp.getUnionid();
        String openId = accessTokenResp.getOpenid();

        Result result = upmsProvider.bindUserFromWeChat(unionId);
        if(result.isFail()){
            throw new InvalidGrantException(result.getData().toString());
        }

        Map<String,Object> map = (HashMap<String,Object>)result.getData();
        String username = (String)map.get("userName");
        parameters.put("username",username);

        Authentication userAuth = new WeChatCodeAuthenticationToken(username, code);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        }
        catch (AccountStatusException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new InvalidGrantException(ase.getMessage());
        }
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }

    private <T extends BaseResp> T convertAndCheck(ResponseEntity<String> entity, Class<T> clazz) {
        HttpStatus statusCode = entity.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throw new InvalidGrantException(statusCode.getReasonPhrase());
        }
        String body = entity.getBody();
        T t = JSON.parseObject(body, clazz);
        if (t.getErrorCode() != 0) {
            throw new InvalidGrantException(t.getErrorMsg());
        }
        return t;
    }

    private String buildAccessTokenUrl(String type, String appid, String secret, String code) {
        return String.format(type, appid, secret, code);
    }
}
