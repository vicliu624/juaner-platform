package indi.vicliu.juaner.upms.domain.service;

import com.alibaba.fastjson.JSONObject;
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

    JSONObject findRoleByUser(Map<String, Object> jsonMap);

    void saveUserRole(Map<String, Object> jsonMap);

    List<TblRoleInfo> getRoleInfoList(Map<String,Object> jsonMap);

    JSONObject saveRoleInfo(Map<String,Object> jsonMap);

    void removeRoleInfo(String ids) throws RoleException;

    JSONObject findPermissionByRole( Map<String,Object> jsonMap);

    void saveRolePerm(@RequestBody Map<String,Object> jsonMap) throws RoleException;

    List<TblRoleInfo> allRoles();
}
