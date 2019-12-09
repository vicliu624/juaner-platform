package indi.vicliu.juaner.upms.controller;

import com.sun.org.apache.regexp.internal.RE;
import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.service.RoleService;
import indi.vicliu.juaner.upms.exception.RoleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    /**
     * 根据角色名获取角色信息
     * @param roleName
     * @return
     */
    @GetMapping("/getRoleByName")
    public Result getRoleByName(@RequestParam String roleName){
        try{
            return Result.success(roleService.getRoleByName(roleName));
        } catch (Exception e){
            return Result.fail("查询出错:" + e.getMessage());
        }
    }

    /**
     * 添加角色
     * @param role
     * @return
     */
    @PostMapping("/addRole")
    public Result addRole(@RequestBody TblRoleInfo role){
        try{
            roleService.addRole(role);
            return Result.success();
        } catch (Exception e){
            return Result.fail("创建失败:" + e.getMessage());
        }
    }

    /**
     * 修改角色信息
     * @param role
     * @return
     */
    @PutMapping("/updateRole")
    public Result updateRole(@RequestBody TblRoleInfo role){
        try{
            roleService.updateRole(role);
            return Result.success();
        } catch (Exception e){
            return Result.fail("更新失败:" + e.getMessage());
        }
    }

    /**
     * 管理端获取用户角色
     * @param jsonMap
     * @return
     */
    @PostMapping("/findRoleByUser")
    public Result findRoleByUser(@RequestBody Map<String, Object> jsonMap){
        return roleService.findRoleByUser(jsonMap);
    }

    /**
     * 管理端保存用户信息的角色
     * @param jsonMap
     * @return
     */
    @PostMapping("/saveUserRole")
    public Result saveUserRole(@RequestBody Map<String, Object> jsonMap) {
        return roleService.saveUserRole(jsonMap);
    }

    /**
     *获取角色列表
     * @param jsonMap
     * @return
     */
    @PostMapping("/getRoleInfoList")
    public Result getRoleInfoList(@RequestBody Map<String,Object> jsonMap){
        return roleService.getRoleInfoList(jsonMap);
    }

    /**
     *保存角色信息
     * @param jsonMap
     * @return
     */
    @PostMapping("/saveRoleInfo")
    public Result saveRoleInfo(@RequestBody Map<String,Object> jsonMap){
        return roleService.saveRoleInfo(jsonMap);
    }

    /**
     *删除角色信息
     * @param jsonMap
     * @return
     */
    @PostMapping("/removeRoleInfo")
    public Result removeRoleInfo(@RequestBody Map<String,Object> jsonMap){
        return roleService.removeRoleInfo(jsonMap);
    }

    /**
     *查找角色的权限以及所有权限集合
     * @param jsonMap
     * @return
     */
    @PostMapping("/findPermissionByRole")
    public Result findPermissionByRole(@RequestBody Map<String,Object> jsonMap){
        return roleService.findPermissionByRole(jsonMap);
    }

    /**
     *分配角色的权限
     * @param jsonMap
     * @return
     */
    @PostMapping("/saveRolePerm")
    public Result saveRolePerm(@RequestBody Map<String,Object> jsonMap){
        return roleService.saveRolePerm(jsonMap);
    }
}
