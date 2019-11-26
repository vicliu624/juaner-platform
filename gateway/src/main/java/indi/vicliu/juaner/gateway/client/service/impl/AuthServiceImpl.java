package indi.vicliu.juaner.gateway.client.service.impl;

import indi.vicliu.juaner.common.core.CommonConstant;
import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.gateway.client.AuthProvider;
import indi.vicliu.juaner.gateway.client.service.AuthService;
import indi.vicliu.juaner.gateway.config.GatewayConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @Auther: liuweikai
 * @Date: 2019/3/21 18:16
 * @Description:
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthProvider authProvider;

    /**
     * 不需要网关签权的url配置(/oauth,/open)
     * 默认/oauth开头是不需要的
     */
    @Value("${gate.ignore.authentication.startWith}")
    private String ignoreUrls;//网关的uri + oauth的uri

    /**
     * jwt验签
     */
    private RsaVerifier verifier;

    @Override
    public Result authenticate(String authentication, String url, String method) {
        /*
        try{

        } catch (Exception e){
            log.error("authenticate fail",e);
            return Result.fail(ErrorType.AUTH_ERROR,"暂时不能访问");
        }
         */
        return authProvider.auth(authentication, url, method);
    }

    @Override
    public boolean ignoreAuthentication(String url) {
        log.info("ignoreUrls:{},url:{}",this.ignoreUrls,url);
        return Stream.of(this.ignoreUrls.split(",")).anyMatch(ignoreUrl -> url.startsWith(StringUtils.trim(ignoreUrl)));
    }

    @Override
    public boolean hasPermission(Result authResult) {
        return authResult.isSuccess() && (boolean) authResult.getData();
    }

    @Override
    public boolean invalidJwtAccessToken(String authentication) {
        verifier = Optional.ofNullable(verifier).orElse(new RsaVerifier(getRSAPublidKeyBybase64(GatewayConfig.RSA_PUBLIC_KEY)));
        //是否无效true表示无效
        boolean invalid = Boolean.TRUE;
        try {
            Jwt jwt = getJwt(authentication);
            jwt.verifySignature(verifier);
            invalid = Boolean.FALSE;
        } catch (InvalidSignatureException | IllegalArgumentException ex) {
            log.error("user token has expired or signature error,error authentication:" + authentication, ex);
        } catch (RuntimeException e){
            log.error("signature error,error authentication:" + authentication, e);
        }
        return invalid;
    }

    @Override
    public boolean hasPermission(String authentication, String url, String method) {
        //token是否有效
        if (invalidJwtAccessToken(authentication)) {
            return Boolean.FALSE;
        }
        //从认证服务获取是否有权限
        return hasPermission(authenticate(authentication, url, method));
    }

    @Override
    public Jwt getJwt(String authentication) {
        return JwtHelper.decode(StringUtils.substring(authentication, CommonConstant.BEARER_BEGIN_INDEX));
    }

    private RSAPublicKey getRSAPublidKeyBybase64(String base64s) {
        Base64.Decoder decoder = Base64.getDecoder();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoder.decode(base64s));
        RSAPublicKey publicKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = (RSAPublicKey)keyFactory.generatePublic(keySpec);
        } catch (Exception var4) {
            log.error("base64编码=" + base64s + "转RSA公钥失败", var4);
        }

        return publicKey;
    }
}
