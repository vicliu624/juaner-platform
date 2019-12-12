package indi.vicliu.juaner.authorization.aspect;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.authorization.utils.RedisStringUtil;
import indi.vicliu.juaner.common.core.CommonConstant;
import indi.vicliu.juaner.common.core.exception.ErrorType;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

/**
 * @Auther: liuweikai
 * @Date: 2019/3/19 15:12
 * @Description: 改掉了默认的oauth返回格式
 */
@Component
@Aspect
@Slf4j
public class AuthTokenAspect {

    @Autowired
    private RedisStringUtil redisStringUtil;

    /*
    @Pointcut("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
    public void postAccessTokenPointcut() {
    }
     */

    /*
    @AfterThrowing(pointcut = "postAccessTokenPointcut()", throwing = "e")
    public Result afterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
        log.error("afterThrowing",e);
        if(e instanceof InsufficientAuthenticationException){
            return Result.fail("没有clientId身份验证");
        } else if(e instanceof InvalidClientException){
            return Result.fail("不可用的clientId");
        } else if(e instanceof InvalidRequestException){
            return Result.fail("缺少grant type");
        } else if(e instanceof InvalidGrantException){
            return Result.fail("不支持隐式授权");
        } else if(e instanceof UnsupportedGrantTypeException){
            return Result.fail("不支持这样的grant type");
        } else {
            return Result.fail("授权出错");
        }
    }
     */

    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
    //@Around("postAccessTokenPointcut()")
    public Object handleControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        Result result = null;
        Object proceed = pjp.proceed();
        if (proceed != null) {
            ResponseEntity<OAuth2AccessToken> responseEntity = (ResponseEntity<OAuth2AccessToken>)proceed;
            if (responseEntity.getStatusCode().is2xxSuccessful()){
                OAuth2AccessToken body = responseEntity.getBody();
                String strAccessToken = JSONObject.toJSONString(body);
                log.info("strAccessToken:{}",strAccessToken);
                if(body == null){
                    result = Result.fail("token出错");
                    return ResponseEntity.status(200).body(result);
                }
                redisStringUtil.setKey(CommonConstant.USER_TOKEN_KEY + body.getAdditionalInformation().get("user"),(String)body.getAdditionalInformation().get("jti"));
                result = Result.success(body);
            } else {
                log.error("error:{}", responseEntity.getStatusCode().toString());
                result = Result.fail(ErrorType.OAUTH_ERROR);
            }
        }
        return ResponseEntity.status(200).body(result);
    }
}
