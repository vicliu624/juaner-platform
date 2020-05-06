package indi.vicliu.juaner.authorization.provider.fallback;

import indi.vicliu.juaner.authorization.provider.UpmsProvider;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 13:51
 * @Description:
 */
@Component
@Slf4j
public class UpmsProviderFallback implements UpmsProvider {

    @Override
    public Result getFullUserInfo(String userName) {
        log.error("UpmsProviderFallback getFullUserInfo 熔断被触发");
        return Result.fail("未能获取到用户信息,请稍后再试.");
    }

    @Override
    public Result getRolesByUserId(Long userId) {
        log.error("UpmsProviderFallback getRolesByUserId 熔断被触发");
        return Result.fail("未能获取到用户的角色信息,请稍后再试.");
    }

    @Override
    public Result lockUser(String userName) {
        log.error("UpmsProviderFallback lockUser 熔断被触发");
        return Result.fail("锁定用户失败,请稍后再试.");
    }

    @Override
    public Result unlockUser(String userName) {
        log.error("UpmsProviderFallback unlockUser 熔断被触发");
        return Result.fail("用户已解锁.");
    }

}
