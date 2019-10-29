package indi.vicliu.juaner.upms.domain.service;

import indi.vicliu.juaner.upms.domain.entity.TblPermissionInfo;

import java.util.List;

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
}
