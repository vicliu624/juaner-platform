package indi.vicliu.juaner.authorization.provider;

import indi.vicliu.juaner.authorization.provider.fallback.UpmsProviderFallback;
import indi.vicliu.juaner.common.core.message.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 13:50
 * @Description:
 */
@Component
@FeignClient(name = "juaner-upms", fallback = UpmsProviderFallback.class)
public interface UpmsProvider {

    @GetMapping(value = "/upms/user/fullInfo")
    Result getFullUserInfo(@RequestParam("userName") String userName);

    @GetMapping(value = "/upms/roles")
    Result getRolesByUserId(@RequestParam("userId") Long userId);

    @PutMapping("/upms/user/lock")
    Result lockUser(@RequestBody String userName);

    @PutMapping("/upms/user/unlock")
    Result unlockUser(@RequestBody String userName);

    @PostMapping("/user/bindWeChat")
    public Result bindUserFromWeChat(@RequestBody String unionId);
}
