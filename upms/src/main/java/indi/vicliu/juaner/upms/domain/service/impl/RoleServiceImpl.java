package indi.vicliu.juaner.upms.domain.service.impl;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.client.IdProvider;
import indi.vicliu.juaner.upms.data.mapper.TblRoleInfoMapper;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.service.RoleService;
import indi.vicliu.juaner.upms.exception.RoleException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
    public Result addRole(TblRoleInfo role) {
        if(StringUtils.isBlank(role.getRoleDesc()) || StringUtils.isBlank(role.getRoleName())){
            return Result.fail("角色名角色描述不可为空");
        }
        Example example = new Example(TblRoleInfo.class);
        example.createCriteria().andEqualTo("roleName",role.getRoleName());
        example.setOrderByClause(" create_time desc limit 1");
        List<TblRoleInfo> userInfoList = this.roleInfoMapper.selectByExample(example);
        if(userInfoList.size()>0){
            return Result.fail("该角色已存在，请勿创建");
        }
        role.setCreateTime(new Date());
        role.setId(idProvider.nextId());
        int i = roleInfoMapper.insertSelective(role);
        if(i==0){
            return Result.fail("创建角色失败");
        }
        return Result.success("创建成功");
    }

    @Override
    public Result updateRole(TblRoleInfo role) {
        if(role.getId()==null){
            return Result.fail("角色编号不可为空");
        }
        roleInfoMapper.updateByPrimaryKeySelective(role);
        return Result.success("修改成功");
    }

    @Override
    public Result getRolesList(String roleName) {
        Example example = new Example(TblRoleInfo.class);
        example.createCriteria().andEqualTo("roleName",roleName);
        example.setOrderByClause(" create_time desc limit 1");
        List<TblRoleInfo> userInfoList = this.roleInfoMapper.selectByExample(example);
        if(userInfoList.size()==0){
            return Result.fail("没有该角色，请重新创建");
        }
        return Result.success(userInfoList.get(0));
    }
}
