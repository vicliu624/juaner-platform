package indi.vicliu.juaner.upms.domain.service.impl;

import indi.vicliu.juaner.upms.data.mapper.TblRoleInfoMapper;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.service.RoleService;
import indi.vicliu.juaner.upms.exception.RoleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 15:09
 * @Description:
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private TblRoleInfoMapper roleInfoMapper;

    @Override
    public List<TblRoleInfo> queryRolesByUserId(Long userId) throws RoleException {
        if(userId == null){
            throw new RoleException("用户编号不能为空");
        }
        return roleInfoMapper.queryRolesByUserId(userId);
    }
}
