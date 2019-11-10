package indi.vicliu.juaner.upms.domain.service.impl;

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.client.IdProvider;
import indi.vicliu.juaner.upms.client.UserProvider;
import indi.vicliu.juaner.upms.data.mapper.TblRoleInfoMapper;
import indi.vicliu.juaner.upms.data.mapper.TblUserInfoMapper;
import indi.vicliu.juaner.upms.data.mapper.TblUserRoleMapMapper;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import indi.vicliu.juaner.upms.domain.entity.TblUserRoleMap;
import indi.vicliu.juaner.upms.domain.service.UserService;
import indi.vicliu.juaner.upms.exception.UserException;
import indi.vicliu.juaner.upms.utils.RedisStringUtil;
import indi.vicliu.juaner.upms.vo.AddUserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 13:15
 * @Description:
 */
@Slf4j
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

    @Autowired
    private RedisStringUtil redisStringUtil;

    @Autowired
    private UserProvider userProvider;

    @Override
    public TblUserInfo findByUserName(String userName) throws UserException {
        String key="user_"+userName;
        String value = redisStringUtil.getValue(key);
        if(Objects.nonNull(value)){
            TblUserInfo userInfo = JSONObject.parseObject(value, TblUserInfo.class);
            if(Objects.nonNull(userInfo)){
                return userInfo;
            }
        }
        Example example = new Example(TblUserInfo.class);
        example.createCriteria().andEqualTo("userName",userName);
        example.setOrderByClause(" create_time desc limit 1");
        List<TblUserInfo> userInfoList = this.userInfoMapper.selectByExample(example);
        if(userInfoList.size() == 0){
            throw new UserException("找不到该用户");
        }
        return userInfoList.get(0);
    }
    @Transactional
    @Override
    public Result addUserInfo(AddUserInfoVO userInfo) throws UserException {

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
        int count = userInfoMapper.insertSelective(info);
        if(count==0){
            return Result.fail("创建用户失败！！");
        }

        //保存用户角色关联信息
        TblUserRoleMap userRole=new TblUserRoleMap();
        userRole.setCreateTime(new Date());
        userRole.setRoleId(userInfo.getRoleId());
        userRole.setUserId(info.getId());
        int insert = tblUserRoleMapMapper.insertSelective(userRole);
        if(insert==0){
            throw new UserException("创建用户角色失败");
        }
        /*try {
            Result result = userProvider.createUserInfo(info);
            if(result.isFail()){
                log.info("同步用户到userservice服务失败：{}",JSONObject.toJSONString(info));
            }
        }catch (Exception e){
            log.info("同步用户到userservice服务失败,异常信息：{}，同步数据：{}",e,JSONObject.toJSONString(info));
        }*/
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
        try {
            updateUserInfoCache(userInfo.getUserName());
        } catch (UserException ex) {
            log.info("更新用户缓存失败：{}",JSONObject.toJSONString(userInfo));
        }finally {

        }
        return Result.success("修改成功");
    }

    @Override
    public Result findByUserPhone(String phone) {
        Example example = new Example(TblUserInfo.class);
        example.createCriteria().andEqualTo("phone",phone);
        example.setOrderByClause(" create_time desc limit 1");
        List<TblUserInfo> userInfoList = this.userInfoMapper.selectByExample(example);
        if(userInfoList.size()==0){
            return Result.success();
        }
        return Result.success(userInfoList.get(0));
    }

    @Override
    public Result findByUserId(Long userId) {
        TblUserInfo userInfo = this.userInfoMapper.selectByPrimaryKey(userId);
        return Result.success(userInfo);
    }

    @Override
    public TblUserInfo findByPhone(String phone) throws UserException {
        Example example = new Example(TblUserInfo.class);
        example.createCriteria().andEqualTo("phone",phone);
        example.setOrderByClause(" create_time desc limit 1");
        List<TblUserInfo> userInfoList = this.userInfoMapper.selectByExample(example);
        if(userInfoList.size() == 0){
            throw new UserException("找不到该用户");
        }
        return userInfoList.get(0);
    }



    @Override
    public Result getByUsername(String username) throws UserException {
        String key="user_"+username;
        String value = redisStringUtil.getValue(key);
        if(Objects.nonNull(value)){
            TblUserInfo userInfo = JSONObject.parseObject(value, TblUserInfo.class);
            if(Objects.nonNull(userInfo)){
                return Result.success(userInfo);
            }
        }
        TblUserInfo userCache = getUserCache(username, key);
        return Result.success(userCache);
    }

    @Override
    public void updateUserInfoCache(String userName) throws UserException {
        String key="user_"+userName;
        getUserCache(userName, key);
    }

    private TblUserInfo getUserCache(String userName, String key) throws UserException {
        Example example = new Example(TblUserInfo.class);
        example.createCriteria().andEqualTo("userName",userName);
        example.setOrderByClause(" create_time desc limit 1");
        TblUserInfo userInfo = userInfoMapper.selectOneByExample(example);
        if(Objects.isNull(userInfo)){
            throw new UserException("找不到该用户");
        }
        redisStringUtil.setKey(key, JSONObject.toJSONString(userInfo));
        return userInfo;
    }


}
