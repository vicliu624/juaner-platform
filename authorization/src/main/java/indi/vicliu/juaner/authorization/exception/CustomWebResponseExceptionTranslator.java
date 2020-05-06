package indi.vicliu.juaner.authorization.exception;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.io.IOException;

/**
 * @Auther: liuweikai
 * @Date: 2019/3/15 20:58
 * @Description:
 */
@Slf4j
@Component
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {

    private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {

        // Try to extract a SpringSecurityException from the stacktrace
        Throwable[] causeChain = throwableAnalyzer.determineCauseChain(e);

        // 异常栈获取 OAuth2Exception 异常
        Exception ase = (OAuth2Exception) throwableAnalyzer.getFirstThrowableOfType(
                OAuth2Exception.class, causeChain);
        if (ase != null) {
            log.info("--------OAuth2Exception----------");
            return handleOAuth2Exception(new CustomOAuthException(e.getMessage(), e));
        }

        ase = (AuthenticationException) throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class,
                causeChain);
        if (ase != null) {
            log.info("AuthenticationException");
            return handleOAuth2Exception(new UnauthorizedException(e.getMessage(), e));
        }

        ase = (AccessDeniedException) throwableAnalyzer
                .getFirstThrowableOfType(AccessDeniedException.class, causeChain);
        if (ase != null) {
            log.info("AccessDeniedException");
            return handleOAuth2Exception(new ForbiddenException(ase.getMessage(), ase));
        }

        ase = (HttpRequestMethodNotSupportedException) throwableAnalyzer
                .getFirstThrowableOfType(HttpRequestMethodNotSupportedException.class, causeChain);
        if (ase != null) {
            log.info("HttpRequestMethodNotSupportedException");
            return handleOAuth2Exception(new MethodNotAllowed(ase.getMessage(), ase));
        }
        log.info("ServerErrorException");
        // 不包含上述异常则服务器内部错误
        return handleOAuth2Exception(new ServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e));
    }

    private ResponseEntity<OAuth2Exception> handleOAuth2Exception(OAuth2Exception e) throws IOException {

        int status = e.getHttpErrorCode();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        if (status == HttpStatus.UNAUTHORIZED.value() || (e instanceof InsufficientScopeException)) {
            headers.set("WWW-Authenticate", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, e.getSummary()));
        }

        CustomOAuth2Exception exception = new CustomOAuth2Exception(e.getMessage(),e);

        log.error("捕获到异常:" + exception.getMessage(),exception);
        ResponseEntity entity = new ResponseEntity<>(exception, headers,
                HttpStatus.valueOf(status));
        log.debug("返回:{}", JSONObject.toJSONString(entity));
        return entity;

    }

    private static class UnauthorizedException extends OAuth2Exception {
        UnauthorizedException(String msg, Throwable t) {
            super(msg, t);
        }
        public String getOAuth2ErrorCode() {
            return "unauthorized";
        }
        public int getHttpErrorCode() {
            return 401;
        }
    }

    private static class ForbiddenException extends OAuth2Exception {
        ForbiddenException(String msg, Throwable t) {
            super(msg, t);
        }
        public String getOAuth2ErrorCode() {
            return "access_denied";
        }
        public int getHttpErrorCode() {
            return 403;
        }
    }

    private static class ServerErrorException extends OAuth2Exception {
        ServerErrorException(String msg, Throwable t) {
            super(msg, t);
        }
        public String getOAuth2ErrorCode() {
            return "server_error";
        }
        public int getHttpErrorCode() {
            return 500;
        }
    }

    private static class MethodNotAllowed extends OAuth2Exception {
        MethodNotAllowed(String msg, Throwable t) {
            super(msg, t);
        }
        public String getOAuth2ErrorCode() {
            return "method_not_allowed";
        }
        public int getHttpErrorCode() {
            return 405;
        }
    }

    private static class CustomOAuthException extends OAuth2Exception {
        CustomOAuthException(String msg, Throwable t) {
            super(msg, t);
        }
        public String getOAuth2ErrorCode() {
            return "authorization_error";
        }
        public int getHttpErrorCode() {
            return 200;
        }
    }
}
