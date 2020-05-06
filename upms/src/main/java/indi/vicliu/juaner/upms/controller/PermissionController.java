package indi.vicliu.juaner.upms.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.domain.entity.TblPermissionInfo;
import indi.vicliu.juaner.upms.domain.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    /**
     * 获取权限列表
     * @param jsonMap
     * @return
     */
    @PostMapping("/list")
    public Result list(@RequestBody Map<String,Object> jsonMap){
        Integer pageNum = jsonMap.get("pageNum") == null ? 1 : (Integer) jsonMap.get("pageNum");
        Integer pageSize = jsonMap.get("pageSize") == null ? 10 : (Integer) jsonMap.get("pageSize");
        Boolean isExp = jsonMap.get("isExp") == null ? false : (Boolean) jsonMap.get("isExp");
        try{
            List<TblPermissionInfo> list = this.permissionService.list(jsonMap);
            if(!isExp) {
                PageHelper.startPage(pageNum, pageSize);
                PageInfo<TblPermissionInfo> pageInfo = new PageInfo<TblPermissionInfo>(list);
                return Result.success(pageInfo);
            }else{
                return Result.success(list);
            }
        }catch (Exception e){
            log.error("调用获取权限列表的方法list出错",e);
            return Result.fail("查询权限失败");
        }
    }

    /**
     * 保存权限信息
     * @param jsonMap
     * @return
     */
    @PostMapping("/savePermission")
    public Result savePermission(@RequestBody Map<String,Object> jsonMap){
        try {
            return Result.success(permissionService.savePermission(jsonMap));
        }catch (Exception e){
            log.info("保存权限信息异常",e);
            return Result.fail("保存权限信息失败");
        }
    }

    /**
     * 删除权限信息
     * @param jsonMap
     * @return
     */
    @PostMapping("/removePermission")
    public Result removePermission(@RequestBody Map<String,Object> jsonMap){
        String ids = jsonMap.get("ids") == null ? null : jsonMap.get("ids").toString();
        if(StringUtils.isEmpty(ids)){
            return Result.fail("删除权限失败");
        }
        try{
            permissionService.removePermission(ids);
            return Result.success("删权限成功");
        }catch (Exception e){
            log.error("删除权限方法removePermission出错",e);
            return Result.fail("删除权限失败");
        }
    }

    /**
     * 获取用户的权限列表
     * @param id
     * @return
     */
    @GetMapping("/getUserPermission")
    public Result getUserPermission(@RequestParam("id") Long id) {
        try{
            return Result.success(permissionService.getUserPermission(id));
        } catch (Exception e){
            log.error("查询权限出错",e);
            return Result.fail("查询权限失败");
        }

    }
}
