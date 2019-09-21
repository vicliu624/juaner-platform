package indi.vicliu.juaner.upms.domain.service.impl;

import indi.vicliu.juaner.upms.data.mapper.TblPermissionInfoMapper;
import indi.vicliu.juaner.upms.domain.entity.TblPermissionInfo;
import indi.vicliu.juaner.upms.domain.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-19 12:52
 * @Description:
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private TblPermissionInfoMapper permissionInfoMapper;

    @Override
    public List<TblPermissionInfo> findAll() {
        return permissionInfoMapper.selectAll();
    }

    @Override
    public List<TblPermissionInfo> queryByRoles(String[] roles) {
        return permissionInfoMapper.queryByRoleCodes(roles);
    }

    @Override
    public TblPermissionInfo findByURI(String uri, String method) {
        Example example = new Example(TblPermissionInfo.class);
        example.createCriteria().andEqualTo("permUrl",uri).andEqualTo("method",method);
        return permissionInfoMapper.selectOneByExample(example);
    }

}
