package indi.vicliu.juaner.authorization.aspect;

import indi.vicliu.juaner.common.core.exception.ErrorType;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
    public Object handleControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        Result result = null;
        Object proceed = pjp.proceed();
        if (proceed != null) {
            ResponseEntity<OAuth2AccessToken> responseEntity = (ResponseEntity<OAuth2AccessToken>)proceed;
            OAuth2AccessToken body = responseEntity.getBody();
            if (responseEntity.getStatusCode().is2xxSuccessful()){
                result = Result.success(body);
            } else {
                log.error("error:{}", responseEntity.getStatusCode().toString());
                result = Result.fail(ErrorType.OAUTH_ERROR);
            }
        }
        return ResponseEntity.status(200).body(result);
    }
}
