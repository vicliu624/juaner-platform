package indi.vicliu.juaner.upms.controller;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.domain.service.PermissionService;
import indi.vicliu.juaner.upms.utils.RedisStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-19 12:57
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;



    @GetMapping("/all")
    public Result getAll(){
        try{
            return Result.success(permissionService.findAll());
        } catch (Exception e){
            log.error(" getAll fail ",e);
            return Result.fail("查询权限时出错");
        }
    }

    @GetMapping("/roles")
    public Result queryByRoles(@RequestParam List<String> roles){
        try{
            if(roles == null || roles.size() == 0){
                return Result.fail("无角色可查");
            }

            String[] strings = new String[roles.size()];
            return Result.success(permissionService.queryByRoles(roles.toArray(strings)));
        } catch (Exception e){
            log.error(" queryByRoles fail ",e);
            return Result.fail("查询权限时出错");
        }
    }

    @GetMapping("/uri")
    public Result queryByUri(@RequestParam String uri,@RequestParam String method){
        try{
            return Result.success(permissionService.findByURI(uri,method));
        } catch (Exception e){
            log.error(" queryByUri fail ",e);
            return Result.fail("查询权限时出错");
        }
    }
}
