package indi.vicliu.juaner.upms.domain.service;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.exception.RoleException;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 15:09
 * @Description:
 */
public interface RoleService {
    List<TblRoleInfo> queryRolesByUserId(Long userId) throws RoleException;
    int addRole(TblRoleInfo role) throws Exception;

    int updateRole(TblRoleInfo role) throws Exception;

    TblRoleInfo getRoleByName(String roleName) throws Exception;

    Result findRoleByUser(Map<String, Object> jsonMap);

    Result saveUserRole(Map<String, Object> jsonMap);

    Result getRoleInfoList(Map<String,Object> jsonMap);

    Result saveRoleInfo(Map<String,Object> jsonMap);

    Result removeRoleInfo( Map<String,Object> jsonMap);

    Result findPermissionByRole( Map<String,Object> jsonMap);

    Result saveRolePerm(@RequestBody Map<String,Object> jsonMap);
}
