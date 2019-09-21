package indi.vicliu.juaner.upms.controller;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.service.RoleService;
import indi.vicliu.juaner.upms.exception.RoleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 15:13
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/upms")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/roles")
    public Result getFullUserInfoByUserName(@RequestParam Long userId){
        try{
            List<TblRoleInfo> roleInfoList = roleService.queryRolesByUserId(userId);
            return Result.success(roleInfoList);
        } catch (Exception e){
            log.error(" getFullUserInfoByUserName fail ",e);
            if(e instanceof RoleException){
                return Result.fail(e.getMessage());
            } else {
                return Result.fail("查询用的角色时出错");
            }
        }
    }
}
