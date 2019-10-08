package indi.vicliu.juaner.upms.domain.service;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.exception.RoleException;

import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 15:09
 * @Description:
 */
public interface RoleService {
    List<TblRoleInfo> queryRolesByUserId(Long userId) throws RoleException;

    Result addRole(TblRoleInfo role);

    Result updateRole(TblRoleInfo role);
}
