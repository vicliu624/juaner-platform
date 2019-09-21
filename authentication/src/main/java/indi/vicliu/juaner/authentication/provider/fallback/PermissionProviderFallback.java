package indi.vicliu.juaner.authentication.provider.fallback;

import indi.vicliu.juaner.authentication.provider.PermissionProvider;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-19 12:49
 * @Description:
 */
@Component
@Slf4j
public class PermissionProviderFallback implements PermissionProvider {

    @Override
    public Result getAllPermissions() {
        log.error("PermissionProviderFallback getAllPermissions 熔断被触发");
        return Result.fail("未查询到权限列表,请稍后再试.");
    }

    @Override
    public Result getPermissionsByRoles(List<String> roles) {
        log.error("PermissionProviderFallback getPermissionsByRoles 熔断被触发");
        return Result.fail("查询不到该角色可用的权限,请稍后再试.");
    }

    @Override
    public Result getPermissionsByUri(String uri, String method) {
        log.error("PermissionProviderFallback getPermissionsByUri 熔断被触发");
        return Result.fail("查询不到该角色可用的权限,请稍后再试.");
    }
}
