package indi.vicliu.juaner.upms.domain.service;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.domain.entity.TblPermissionInfo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-19 12:52
 * @Description:
 */
public interface PermissionService {
    List<TblPermissionInfo> findAll();
    List<TblPermissionInfo> queryByRoles(String[] roles);
    TblPermissionInfo findByURI(String uri,String method);

    void updatePermissionAllCache();
    void updateRolePermissionCache(String role);
    void updateUrlPermissionCache(TblPermissionInfo permissionInfo);

    Result list(Map<String,Object> jsonMap);

    Result savePermission(Map<String,Object> jsonMap);

    Result removePermission( Map<String,Object> jsonMap);

    Result getUserPermission(Long id);
}
