package indi.vicliu.juaner.gateway.client.fallback;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.gateway.client.AuthProvider;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Auther: liuweikai
 * @Date: 2019/3/20 17:29
 * @Description:
 */
@Slf4j
@Component
public class AuthProviderFallback implements AuthProvider {

    /**
     * 降级统一返回无权限
     *
     * @param authentication
     * @param url
     * @param method
     * @return <pre>
     * Result:
     * {
     *   code:"-1"
     *   mesg:"系统异常"
     * }
     * </pre>
     */
    @Override
    public Result auth(String authentication, String url, String method) {
        log.error("AuthProvider的熔断被触发");
        return Result.fail("鉴权时出错");
    }
}
