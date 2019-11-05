package indi.vicliu.juaner.upms.client;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.client.fallback.UserProviderFallback;
import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@FeignClient(name = "ynyt-user-service",fallback = UserProviderFallback.class)
public interface UserProvider {

    @PostMapping("/user/create")
    Result createUserInfo(TblUserInfo info);
}
