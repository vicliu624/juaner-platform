package indi.vicliu.juaner.upms.domain.service.impl;

import indi.vicliu.juaner.upms.client.IdProvider;
import indi.vicliu.juaner.upms.data.mapper.TblRoleInfoMapper;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.service.RoleService;
import indi.vicliu.juaner.upms.exception.RoleException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.xml.bind.ValidationException;
import java.util.Date;
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

    @Autowired
    private IdProvider idProvider;

    @Override
    public List<TblRoleInfo> queryRolesByUserId(Long userId) throws RoleException {
        if(userId == null){
            throw new RoleException("用户编号不能为空");
        }
        return roleInfoMapper.queryRolesByUserId(userId);
    }
    @Override
    public int addRole(TblRoleInfo role) throws Exception {
        if(StringUtils.isBlank(role.getRoleDesc()) || StringUtils.isBlank(role.getRoleName())){
            throw new ValidationException("角色名角色描述不可为空");
        }
        Example example = new Example(TblRoleInfo.class);
        example.createCriteria().andEqualTo("roleName",role.getRoleName());
        example.setOrderByClause(" create_time desc limit 1");
        List<TblRoleInfo> userInfoList = this.roleInfoMapper.selectByExample(example);
        if(!userInfoList.isEmpty()){
            throw new ValidationException("该角色已存在，请勿重复创建");
        }
        role.setCreateTime(new Date());
        role.setId(idProvider.nextId());
        return roleInfoMapper.insertSelective(role);
    }

    @Override
    public int updateRole(TblRoleInfo role) throws Exception {
        if(role.getId()==null){
            throw new ValidationException("角色编号不可为空");
        }
        return roleInfoMapper.updateByPrimaryKeySelective(role);
    }

    @Override
    public TblRoleInfo getRoleByName(String roleName) throws Exception {
        Example example = new Example(TblRoleInfo.class);
        example.createCriteria().andEqualTo("roleName",roleName);
        example.setOrderByClause(" create_time desc limit 1");
        List<TblRoleInfo> roleInfoList = this.roleInfoMapper.selectByExample(example);
        if(roleInfoList.isEmpty()){
            return null;
        }
        return roleInfoList.get(0);
    }
}
