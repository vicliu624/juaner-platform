package indi.vicliu.juaner.upms.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.entity.TblRolePermMap;
import indi.vicliu.juaner.upms.domain.entity.TblUserRoleMap;
import indi.vicliu.juaner.upms.domain.service.RoleService;
import indi.vicliu.juaner.upms.exception.RoleException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

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
    public Result getFullUserInfoByUserName(@RequestParam Long userId) {
        try {
            List<TblRoleInfo> roleInfoList = roleService.queryRolesByUserId(userId);
            return Result.success(roleInfoList);
        } catch (Exception e) {
            log.error(" getFullUserInfoByUserName fail ", e);
            if (e instanceof RoleException) {
                return Result.fail(e.getMessage());
            } else {
                return Result.fail("查询用的角色时出错");
            }
        }
    }

    /**
     * 根据角色名获取角色信息
     *
     * @param roleName
     * @return
     */
    @GetMapping("/getRoleByName")
    public Result getRoleByName(@RequestParam String roleName) {
        try {
            return Result.success(roleService.getRoleByName(roleName));
        } catch (Exception e) {
            return Result.fail("查询出错:" + e.getMessage());
        }
    }

    /**
     * 添加角色
     *
     * @param role
     * @return
     */
    @PostMapping("/addRole")
    public Result addRole(@RequestBody TblRoleInfo role) {
        try {
            roleService.addRole(role);
            return Result.success();
        } catch (Exception e) {
            return Result.fail("创建失败:" + e.getMessage());
        }
    }

    /**
     * 修改角色信息
     *
     * @param role
     * @return
     */
    @PutMapping("/updateRole")
    public Result updateRole(@RequestBody TblRoleInfo role) {
        try {
            roleService.updateRole(role);
            return Result.success();
        } catch (Exception e) {
            return Result.fail("更新失败:" + e.getMessage());
        }
    }

    /**
     * 管理端获取用户角色
     *
     * @param jsonMap
     * @return
     */
    @PostMapping("/findRoleByUser")
    public Result findRoleByUser(@RequestBody Map<String, Object> jsonMap) {
        try {
            return Result.success(roleService.findRoleByUser(jsonMap));
        } catch (Exception e) {
            return Result.fail("查询角色时出错");
        }
    }

    /**
     * 管理端保存用户信息的角色
     *
     * @param jsonMap
     * @return
     */
    @PostMapping("/saveUserRole")
    public Result saveUserRole(@RequestBody Map<String, Object> jsonMap) {
        try {
            roleService.saveUserRole(jsonMap);
            return Result.success("用户角色分配成功");
        } catch (Exception e) {
            log.error("分配用户的角色方法saveUserRole出错", e);
            return Result.fail("分配用户角色失败");
        }
    }

    /**
     * 获取角色列表
     *
     * @param jsonMap
     * @return
     */
    @PostMapping("/getRoleInfoList")
    public Result getRoleInfoList(@RequestBody Map<String, Object> jsonMap) {
        log.info("sysManage getRoleInfoList:{}", jsonMap.toString());
        Integer pageNum = jsonMap.get("pageNum") == null ? 1 : (Integer) jsonMap.get("pageNum");
        Integer pageSize = jsonMap.get("pageSize") == null ? 10 : (Integer) jsonMap.get("pageSize");
        Boolean isExp = jsonMap.get("isExp") == null ? false : (Boolean) jsonMap.get("isExp");
        if (!isExp) {
            PageHelper.startPage(pageNum, pageSize);
        }
        try {
            List<TblRoleInfo> list = roleService.getRoleInfoList(jsonMap);
            PageInfo<TblRoleInfo> pageInfo = new PageInfo<>(list);
            return Result.success(pageInfo);
        } catch (Exception e) {
            log.error("调用获取角色列表的方法getRoleInfoList出错", e);
            return Result.fail("查询角色失败");
        }
    }

    /**
     * 保存角色信息
     *
     * @param jsonMap
     * @return
     */
    @PostMapping("/saveRoleInfo")
    public Result saveRoleInfo(@RequestBody Map<String, Object> jsonMap) {
        try {
            JSONObject jsonObject = roleService.saveRoleInfo(jsonMap);
            return Result.success(jsonObject);
        } catch (Exception e) {
            log.info("保存角色信息异常", e);
            return Result.fail("保存角色信息失败");
        }
    }

    /**
     * 删除角色信息
     *
     * @param jsonMap
     * @return
     */
    @PostMapping("/removeRoleInfo")
    public Result removeRoleInfo(@RequestBody Map<String, Object> jsonMap) {
        String ids = jsonMap.get("ids") == null ? null : jsonMap.get("ids").toString();
        if (StringUtils.isEmpty(ids)) {
            return Result.fail("删除权限失败");
        }
        try {
            roleService.removeRoleInfo(ids);
            return Result.success("删除角色成功");
        } catch (Exception e) {
            log.error("删除角色方法removeRoleInfo出错", e);
            return Result.fail("删除角色失败");
        }
    }

    /**
     * 查找角色的权限以及所有权限集合
     *
     * @param jsonMap
     * @return
     */
    @PostMapping("/findPermissionByRole")
    public Result findPermissionByRole(@RequestBody Map<String, Object> jsonMap) {
        try {
            return Result.success(roleService.findPermissionByRole(jsonMap));
        } catch (Exception e) {
            log.error("查u想你权限出错", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 分配角色的权限
     *
     * @param jsonMap
     * @return
     */
    @PostMapping("/saveRolePerm")
    public Result saveRolePerm(@RequestBody Map<String, Object> jsonMap) {
        try {
            roleService.saveRolePerm(jsonMap);
            return Result.success("角色权限分配成功");
        } catch (Exception e) {
            log.error("分配角色的权限方法saveRolePerm出错", e);
            return Result.fail("分配角色权限失败");
        }
    }
}
