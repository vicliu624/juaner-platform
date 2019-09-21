package indi.vicliu.juaner.gateway.client;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.gateway.client.fallback.AuthProviderFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Auther: liuweikai
 * @Date: 2019/3/20 17:28
 * @Description:
 */
@Component
@FeignClient(name = "juaner-authentication", fallback = AuthProviderFallback.class)
public interface AuthProvider {

    @PostMapping(value = "/auth/permission")
    Result auth(@RequestHeader(HttpHeaders.AUTHORIZATION) String authentication, @RequestParam("url") String url,
                @RequestParam("method") String method);
}
