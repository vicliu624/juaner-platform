package indi.vicliu.juaner.upms.domain.service;

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
}
