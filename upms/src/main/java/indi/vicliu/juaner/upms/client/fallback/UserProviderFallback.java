package indi.vicliu.juaner.upms.client.fallback;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.client.UserProvider;
import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserProviderFallback implements UserProvider {

    @Override
    public Result createUserInfo(TblUserInfo info) {
        log.error("createUserInfo的熔断被触发");
        return Result.fail();
    }
}
