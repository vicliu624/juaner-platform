package indi.vicliu.juaner.upms.domain.service.impl;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.client.IdProvider;
import indi.vicliu.juaner.upms.data.mapper.TblRoleInfoMapper;
import indi.vicliu.juaner.upms.data.mapper.TblUserInfoMapper;
import indi.vicliu.juaner.upms.data.mapper.TblUserRoleMapMapper;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import indi.vicliu.juaner.upms.domain.entity.TblUserRoleMap;
import indi.vicliu.juaner.upms.domain.service.UserService;
import indi.vicliu.juaner.upms.exception.UserException;
import indi.vicliu.juaner.upms.vo.AddUserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 13:15
 * @Description:
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TblUserInfoMapper userInfoMapper;

    @Autowired
    private TblRoleInfoMapper tblRoleInfoMapper;

    @Autowired
    private TblUserRoleMapMapper tblUserRoleMapMapper;

    @Autowired
    private IdProvider idProvider;

    @Override
    public TblUserInfo findByUserName(String userName) throws UserException {
        Example example = new Example(TblUserInfo.class);
        example.createCriteria().andEqualTo("userName",userName);
        example.setOrderByClause(" create_time desc limit 1");
        List<TblUserInfo> userInfoList = this.userInfoMapper.selectByExample(example);
        if(userInfoList.size() == 0){
            throw new UserException("找不到该用户");
        }
        return userInfoList.get(0);
    }
    @Override
    public Result addUserInfo(AddUserInfoVO userInfo) {

        Example example = new Example(TblUserInfo.class);
        example.createCriteria().andEqualTo("phone",userInfo.getPhone());
        example.setOrderByClause(" create_time desc limit 1");
        List<TblUserInfo> userInfoList = this.userInfoMapper.selectByExample(example);
        if(userInfoList.size()>0){
            return Result.fail("用户已存在");
        }
        //保存用户信息
        TblUserInfo info=new TblUserInfo();
        BeanUtils.copyProperties(userInfo,info);
        info.setCreateTime(new Date());
        info.setId(idProvider.nextId());
        //校验角色信息
        TblRoleInfo roleInfo = tblRoleInfoMapper.selectByPrimaryKey(userInfo.getRoleId());
        if(roleInfo==null){
            return Result.fail("用户角色不存在");
        }
        info.setNickName(roleInfo.getRoleName());
        int count = userInfoMapper.insert(info);
        if(count==0){
            return Result.fail("创建用户失败！！");
        }

        //保存用户角色关联信息
        TblUserRoleMap userRole=new TblUserRoleMap();
        userRole.setCreateTime(new Date());
        userRole.setRoleId(userInfo.getRoleId());
        userRole.setUserId(info.getId());
        int insert = tblUserRoleMapMapper.insert(userRole);
        if(insert==0){
            return Result.fail("创建用户角色失败！！");
        }
        return Result.success(info);
    }

    @Override
    public Result updateUserInfo(TblUserInfo user) {

        if(user.getId()==null){
            return Result.fail("用户id不可以为空");
        }
        TblUserInfo userInfo = userInfoMapper.selectByPrimaryKey(user.getId());
        if(userInfo==null){
            return Result.fail("用户不存在");
        }
        userInfo.setUpdateTime(new Date());
        userInfoMapper.updateByPrimaryKeySelective(user);
        return Result.success("修改成功");
    }
}
