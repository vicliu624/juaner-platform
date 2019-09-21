package indi.vicliu.juaner.authentication.provider;

import indi.vicliu.juaner.authentication.provider.fallback.PermissionProviderFallback;
import indi.vicliu.juaner.common.core.message.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-19 12:48
 * @Description:
 */
@Component
@FeignClient(name = "juaner-upms", fallback = PermissionProviderFallback.class)
public interface PermissionProvider {

    @GetMapping(value = "/permission/all")
    Result getAllPermissions();

    @GetMapping(value = "/permission/roles")
    Result getPermissionsByRoles(@RequestParam(value = "roles") List<String> roles);

    @GetMapping(value = "/permission/uri")
    Result getPermissionsByUri(@RequestParam(value = "uri") String uri,@RequestParam(value = "method") String method);
}
