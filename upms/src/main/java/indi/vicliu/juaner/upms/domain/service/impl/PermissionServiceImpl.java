package indi.vicliu.juaner.upms.domain.service.impl;

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.upms.data.mapper.TblPermissionInfoMapper;
import indi.vicliu.juaner.upms.domain.entity.TblPermissionInfo;
import indi.vicliu.juaner.upms.domain.service.PermissionService;
import indi.vicliu.juaner.upms.utils.RedisStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-19 12:52
 * @Description:
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private TblPermissionInfoMapper permissionInfoMapper;

    @Autowired
    private RedisStringUtil redisStringUtil;

    private static final int EXPIRE=30;

    @Override
    public List<TblPermissionInfo> findAll() {
        String data = redisStringUtil.getValue("permissionInfoAll");
        if(Objects.nonNull(data)){
            List<TblPermissionInfo> permissionInfos = JSONObject.parseArray(data, TblPermissionInfo.class);
            if(!permissionInfos.isEmpty()){
                return permissionInfos;
            }
        }
        List<TblPermissionInfo> tblPermissionInfos = permissionInfoMapper.selectAll();
        if(tblPermissionInfos.isEmpty()){
            redisStringUtil.setKeyExpire("permissionInfoAll",JSONObject.toJSONString(tblPermissionInfos),EXPIRE, TimeUnit.MINUTES);
        }
        return tblPermissionInfos;
    }

    @Override
    public List<TblPermissionInfo> queryByRoles(String[] roles) {
        String key = "rolesPermission"+Arrays.stream(roles).reduce("", String::concat);
        String data=redisStringUtil.getValue(key);
        if(Objects.nonNull(data)){
            List<TblPermissionInfo> permissionInfos = JSONObject.parseArray(data, TblPermissionInfo.class);
            return permissionInfos;
        }

        List<TblPermissionInfo> tblPermissionInfoList = permissionInfoMapper.queryByRoleCodes(roles);
        if(tblPermissionInfoList.isEmpty()){
            redisStringUtil.setKeyExpire(key,JSONObject.toJSONString(tblPermissionInfoList),EXPIRE, TimeUnit.MINUTES);
        }
        return tblPermissionInfoList;

    }

    @Override
    public TblPermissionInfo findByURI(String uri, String method) {
        String key="urlPermission"+uri+method;
        String data=redisStringUtil.getValue(key);
        if(Objects.nonNull(data)){
            TblPermissionInfo tblPermissionInfo = JSONObject.parseObject(data, TblPermissionInfo.class);
            return tblPermissionInfo;
        }

        Example example = new Example(TblPermissionInfo.class);
        example.createCriteria().andEqualTo("permUrl",uri).andEqualTo("method",method);
        TblPermissionInfo permissionInfo = permissionInfoMapper.selectOneByExample(example);
        if (Objects.nonNull(permissionInfo)) {
            redisStringUtil.setKeyExpire(key,JSONObject.toJSONString(permissionInfo),EXPIRE, TimeUnit.MINUTES);
        }
        return permissionInfo;


    }

}
