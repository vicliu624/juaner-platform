package indi.vicliu.juaner.upms.domain.service.impl;

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.client.IdProvider;
import indi.vicliu.juaner.upms.data.mapper.TblRoleInfoMapper;
import indi.vicliu.juaner.upms.data.mapper.TblUserIdMapMapper;
import indi.vicliu.juaner.upms.data.mapper.TblUserInfoMapper;
import indi.vicliu.juaner.upms.data.mapper.TblUserRoleMapMapper;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.entity.TblUserIdMap;
import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import indi.vicliu.juaner.upms.domain.entity.TblUserRoleMap;
import indi.vicliu.juaner.upms.domain.service.UserService;
import indi.vicliu.juaner.upms.exception.UserException;
import indi.vicliu.juaner.upms.utils.RedisStringUtil;
import indi.vicliu.juaner.upms.vo.AddUserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@SuppressWarnings("ALL")
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
    private TblUserIdMapMapper userIdMapMapper;

    @Autowired
    private IdProvider idProvider;

    @Autowired
    private RedisStringUtil redisStringUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TblUserInfo addUserInfo(AddUserInfoVO userInfo) throws UserException {
        //保存用户信息
        TblUserInfo info=new TblUserInfo();
        BeanUtils.copyProperties(userInfo,info);
        info.setCreateTime(new Date());
        if(userInfo.getId()==null){
            info.setId(idProvider.nextId());
        }
        //校验角色信息
        TblRoleInfo roleInfo = tblRoleInfoMapper.selectByPrimaryKey(userInfo.getRoleId());
        if(roleInfo==null){
            throw new UserException("用户角色不存在");
        }
        info.setNickName(roleInfo.getRoleName());
        int count = userInfoMapper.insertSelective(info);
        if(count==0){
            throw new UserException("创建用户失败!");
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
        return info;
    }

    @Override
    public TblUserInfo updateUserInfo(AddUserInfoVO user) throws UserException {
        if(user.getId()==null){
            throw new UserException("用户id不可以为空");
        }
        TblUserInfo userInfo = userInfoMapper.selectByPrimaryKey(user.getId());
        if(userInfo==null){
            throw new UserException("用户不存在");
        }
        TblUserInfo info=new TblUserInfo();
        BeanUtils.copyProperties(user,info);
        userInfo.setUpdateTime(new Date());
        userInfoMapper.updateByPrimaryKeySelective(info);
        return updateUserInfoCache(userInfo.getUserName());
    }

    @Override
    public int updateUserInfo(TblUserInfo user) {
        return this.userInfoMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public TblUserInfo findByUserPhone(String phone) {
        Example example = new Example(TblUserInfo.class);
        example.createCriteria().andEqualTo("phone",phone);
        example.setOrderByClause(" create_time desc limit 1");
        List<TblUserInfo> userInfoList = this.userInfoMapper.selectByExample(example);
        if(userInfoList.size()==0){
            return null;
        }
        return userInfoList.get(0);
    }

    @Override
    public TblUserInfo findByUserId(Long userId) {
        return this.userInfoMapper.selectByPrimaryKey(userId);
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
    public TblUserInfo getByUsername(String username) throws UserException {
        String key="upms_user_"+username;
        String value = redisStringUtil.getValue(key);
        if(Objects.nonNull(value)){
            TblUserInfo userInfo = JSONObject.parseObject(value, TblUserInfo.class);
            if(Objects.nonNull(userInfo)){
                return userInfo;
            }
        }
        return getUserFromDB(username, key);
    }

    @Override
    public TblUserInfo updateUserInfoCache(String userName) throws UserException {
        String key="upms_user_"+userName;
        return getUserFromDB(userName, key);
    }

    private TblUserInfo getUserFromDB(String userName, String key) throws UserException {
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TblUserInfo createUserInfo(AddUserInfoVO userInfo) throws UserException {
        Example example = new Example(TblUserInfo.class);
        example.createCriteria().andEqualTo("userName",userInfo.getUserName());
        example.setOrderByClause(" create_time desc limit 1");
        List<TblUserInfo> userInfoList = this.userInfoMapper.selectByExample(example);
        if(userInfoList.size()>0){
            throw new UserException("用户已存在");
        }
        //保存用户信息
        TblUserInfo info=new TblUserInfo();

        BeanUtils.copyProperties(userInfo,info);
        if(userInfo.getId()==null){
            info.setId(idProvider.nextId());
        }
        info.setCreateTime(new Date());
        int count = userInfoMapper.insertSelective(info);
        return info;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delUserInfo(String ids) {
        String[] idArr = ids.split(",");
        for (String id : idArr) {
            if (id != null && ids.trim().length() > 0) {
                Example urexample = new Example(TblUserRoleMap.class);
                urexample.createCriteria().andEqualTo("userId", Long.parseLong(id.trim()));
                //删除用户角色
                Integer ur = tblUserRoleMapMapper.deleteByExample(urexample);
                //删除用户
                Integer ri = userInfoMapper.deleteByPrimaryKey(Long.parseLong(id.trim()));
                log.info("删除用户id:" + id + ",删除用户权限关系个数TblUserRoleMap:" + ur);
            }
        }
    }

    @Override
    public int lockUserByName(String userName) throws UserException {
        TblUserInfo userInfo = getByUsername(userName);
        userInfo.setAccountNonLocked(Boolean.FALSE);
        Example example = new Example(TblUserInfo.class);
        example.createCriteria().andEqualTo("userName",userName);
        int updateCount = this.userInfoMapper.updateByExample(userInfo,example);
        String key="upms_user_" + userName;
        redisStringUtil.delKey(key);
        return updateCount;
    }

    @Override
    public int unlockUserByName(String userName) throws UserException {
        TblUserInfo userInfo = getByUsername(userName);
        userInfo.setAccountNonLocked(Boolean.TRUE);
        Example example = new Example(TblUserInfo.class);
        example.createCriteria().andEqualTo("userName",userName);
        int updateCount = this.userInfoMapper.updateByExample(userInfo,example);
        String key1 = "lock_count_user_" + userName;
        redisStringUtil.delKey(key1);
        String key2 ="upms_user_" + userName;
        redisStringUtil.delKey(key2);
        return updateCount;
    }

    @Override
    public TblUserInfo queryByWeChatUnionId(String unionId,String openId) {
        if(StringUtils.isEmpty(unionId)){
            Example example1 = new Example(TblUserInfo.class);
            example1.createCriteria().andEqualTo("wxUnionId",openId);
            List<TblUserInfo> userInfos1 = this.userInfoMapper.selectByExample(example1);
            if (!userInfos1.isEmpty()) {
                TblUserIdMap userIdMap = this.userIdMapMapper.selectByPrimaryKey(userInfos1.get(0).getId());
                if(userIdMap == null){
                    userIdMap = new TblUserIdMap();
                    userIdMap.setUserId(userInfos1.get(0).getId());
                    userIdMap.setWechatOpenId(openId);
                    this.userIdMapMapper.insertSelective(userIdMap);
                } else {
                    userIdMap.setWechatOpenId(openId);
                    this.userIdMapMapper.updateByPrimaryKeySelective(userIdMap);
                }
                return userInfos1.get(0);
            } else {
                TblUserInfo userInfo = new TblUserInfo();
                userInfo.setId(idProvider.nextId());
                userInfo.setUserName("User" + userInfo.getId());
                userInfo.setAccountNonExpired(true);
                userInfo.setAccountNonLocked(true);
                userInfo.setCreateBy(0L);
                userInfo.setCreateTime(new Date());
                userInfo.setCredentialsNonExpired(true);
                userInfo.setEnabled(true);
                userInfo.setNickName(userInfo.getUserName());
                userInfo.setWxUnionId(openId);
                userInfo.setPassword("");
                this.userInfoMapper.insertSelective(userInfo);

                TblUserRoleMap userRole=new TblUserRoleMap();
                userRole.setCreateTime(new Date());
                userRole.setRoleId(0L);
                userRole.setUserId(userInfo.getId());
                tblUserRoleMapMapper.insertSelective(userRole);

                TblUserIdMap userIdMap = this.userIdMapMapper.selectByPrimaryKey(userInfo.getId());
                if(userIdMap == null){
                    userIdMap = new TblUserIdMap();
                    userIdMap.setUserId(userInfo.getId());
                    userIdMap.setWechatOpenId(openId);
                    this.userIdMapMapper.insertSelective(userIdMap);
                } else {
                    userIdMap.setWechatOpenId(openId);
                    this.userIdMapMapper.updateByPrimaryKeySelective(userIdMap);
                }
                return userInfo;
            }
        } else {
            Example example = new Example(TblUserInfo.class);
            example.createCriteria().andEqualTo("wxUnionId",unionId);
            List<TblUserInfo> userInfos = this.userInfoMapper.selectByExample(example);
            if(!userInfos.isEmpty()){
                TblUserIdMap userIdMap = this.userIdMapMapper.selectByPrimaryKey(userInfos.get(0).getId());
                if(userIdMap == null){
                    userIdMap = new TblUserIdMap();
                    userIdMap.setUserId(userInfos.get(0).getId());
                    userIdMap.setWechatUnionId(unionId);
                    userIdMap.setWechatOpenId(openId);
                    this.userIdMapMapper.insertSelective(userIdMap);
                } else {
                    userIdMap.setWechatUnionId(unionId);
                    userIdMap.setWechatOpenId(openId);
                    this.userIdMapMapper.updateByPrimaryKeySelective(userIdMap);
                }
                return userInfos.get(0);
            } else {
                Example example1 = new Example(TblUserInfo.class);
                example1.createCriteria().andEqualTo("wxUnionId",openId);
                List<TblUserInfo> userInfos1 = this.userInfoMapper.selectByExample(example1);
                if (!userInfos1.isEmpty()) {
                    TblUserInfo userInfo = userInfos1.get(0);
                    userInfo.setWxUnionId(unionId);
                    this.userInfoMapper.updateByPrimaryKeySelective(userInfo);
                    TblUserIdMap userIdMap = this.userIdMapMapper.selectByPrimaryKey(userInfos1.get(0).getId());
                    if(userIdMap == null){
                        userIdMap = new TblUserIdMap();
                        userIdMap.setUserId(userInfos1.get(0).getId());
                        userIdMap.setWechatUnionId(unionId);
                        userIdMap.setWechatOpenId(openId);
                        this.userIdMapMapper.insertSelective(userIdMap);
                    } else {
                        userIdMap.setWechatUnionId(unionId);
                        userIdMap.setWechatOpenId(openId);
                        this.userIdMapMapper.updateByPrimaryKeySelective(userIdMap);
                    }
                    return userInfo;
                } else {
                    TblUserInfo userInfo = new TblUserInfo();
                    userInfo.setId(idProvider.nextId());
                    userInfo.setUserName("User" + userInfo.getId());
                    userInfo.setAccountNonExpired(true);
                    userInfo.setAccountNonLocked(true);
                    userInfo.setCreateBy(0L);
                    userInfo.setCreateTime(new Date());
                    userInfo.setCredentialsNonExpired(true);
                    userInfo.setEnabled(true);
                    userInfo.setNickName(userInfo.getUserName());
                    userInfo.setWxUnionId(unionId);
                    userInfo.setPassword("");
                    this.userInfoMapper.insertSelective(userInfo);

                    TblUserRoleMap userRole=new TblUserRoleMap();
                    userRole.setCreateTime(new Date());
                    userRole.setRoleId(0L);
                    userRole.setUserId(userInfo.getId());
                    tblUserRoleMapMapper.insertSelective(userRole);

                    TblUserIdMap userIdMap = this.userIdMapMapper.selectByPrimaryKey(userInfo.getId());
                    if(userIdMap == null){
                        userIdMap = new TblUserIdMap();
                        userIdMap.setUserId(userInfo.getId());
                        userIdMap.setWechatUnionId(unionId);
                        userIdMap.setWechatOpenId(openId);
                        this.userIdMapMapper.insertSelective(userIdMap);
                    } else {
                        userIdMap.setWechatUnionId(unionId);
                        userIdMap.setWechatOpenId(openId);
                        this.userIdMapMapper.updateByPrimaryKeySelective(userIdMap);
                    }

                    return userInfo;
                }
            }
        }
    }

    @Override
    public Result unRegister(Long userId) {
        TblUserInfo userInfo =  userInfoMapper.selectByPrimaryKey(userId);
        if (userInfo!=null){
            userInfo.setEnabled(false);
            userInfoMapper.updateByPrimaryKeySelective(userInfo);
            return Result.success("注销成功");
        }else{
            return Result.fail("未查询到该账户");
        }
    }
}
