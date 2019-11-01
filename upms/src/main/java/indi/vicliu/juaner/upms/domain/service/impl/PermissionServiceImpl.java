package indi.vicliu.juaner.upms.domain.service.impl;

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.upms.data.mapper.TblPermissionInfoMapper;
import indi.vicliu.juaner.upms.data.mapper.TblRoleInfoMapper;
import indi.vicliu.juaner.upms.domain.entity.TblPermissionInfo;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.service.PermissionService;
import indi.vicliu.juaner.upms.utils.RedisStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
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
    private TblRoleInfoMapper tblRoleInfoMapper;

    @Autowired
    private RedisStringUtil redisStringUtil;

    private static final int EXPIRE=10;

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
        if(!tblPermissionInfos.isEmpty()){
            // TODO 线上发布redis不设置缓存时间
           // redisStringUtil.setKeyExpire("permissionInfoAll",JSONObject.toJSONString(tblPermissionInfos),EXPIRE, TimeUnit.MINUTES);
           redisStringUtil.setKey("permissionInfoAll",JSONObject.toJSONString(tblPermissionInfos));
        }
        return tblPermissionInfos;
    }

    @Override
    public List<TblPermissionInfo> queryByRoles(String[] roles) {
       // String key = "rolesPermission"+Arrays.stream(roles).reduce("", String::concat);
        //方便进行redis更新数据
        List<TblPermissionInfo> tblPermissionInfoList=new ArrayList<>();
        for (String role:roles){
            String key = "rolesPermission_"+role;
            String data=redisStringUtil.getValue(key);
            if(Objects.nonNull(data) && Objects.nonNull(JSONObject.parseArray(data, TblPermissionInfo.class))){
                List<TblPermissionInfo> permissionInfos = JSONObject.parseArray(data, TblPermissionInfo.class);
                tblPermissionInfoList.addAll(permissionInfos);
            }else {
                List<TblPermissionInfo> infos = permissionInfoMapper.queryByRoleCode(role);
                tblPermissionInfoList.addAll(infos);
                if(!infos.isEmpty()){
                    // TODO 线上发布redis不设置缓存时间
                    //redisStringUtil.setKeyExpire(key,JSONObject.toJSONString(infos),EXPIRE, TimeUnit.MINUTES);
                    redisStringUtil.setKey(key,JSONObject.toJSONString(infos));
                }
            }
        }

        return tblPermissionInfoList;

    }

    @Override
    public TblPermissionInfo findByURI(String uri, String method) {
        String key="urlPermission_"+uri+method;
        String data=redisStringUtil.getValue(key);
        if(Objects.nonNull(data)){
            TblPermissionInfo tblPermissionInfo = JSONObject.parseObject(data, TblPermissionInfo.class);
            if(Objects.nonNull(tblPermissionInfo)){
                return tblPermissionInfo;
            }
        }

        Example example = new Example(TblPermissionInfo.class);
        example.createCriteria().andEqualTo("permUrl",uri).andEqualTo("method",method);
        TblPermissionInfo permissionInfo = permissionInfoMapper.selectOneByExample(example);
        if (Objects.nonNull(permissionInfo)) {
            // TODO 线上发布redis不设置缓存时间
            //redisStringUtil.setKeyExpire(key,JSONObject.toJSONString(permissionInfo),EXPIRE, TimeUnit.MINUTES);
            redisStringUtil.setKey(key,JSONObject.toJSONString(permissionInfo));
        }
        return permissionInfo;


    }

    /**
     * 新增菜单，数据更新redis
     */
    @Override
    public void updatePermissionAllCache() {
        List<TblPermissionInfo> tblPermissionInfos = permissionInfoMapper.selectAll();
        if(!tblPermissionInfos.isEmpty()){
            // TODO 线上发布redis不设置缓存时间
            redisStringUtil.setKeyExpire("permissionInfoAll",JSONObject.toJSONString(tblPermissionInfos),EXPIRE, TimeUnit.MINUTES);
            // redisStringUtil.setKey("permissionInfoAll",JSONObject.toJSONString(tblPermissionInfos));
        }
    }

    /**
     * 角色权限发生变化，数据更新redis
     * @param role
     */
    @Override
    public void updateRolePermissionCache(String role) {
        String key = "rolesPermission_"+role;
        List<TblPermissionInfo> infos = permissionInfoMapper.queryByRoleCode(role);
        if(!infos.isEmpty()){
            // TODO 线上发布redis不设置缓存时间
            redisStringUtil.setKeyExpire(key,JSONObject.toJSONString(infos),EXPIRE, TimeUnit.MINUTES);
            //redisStringUtil.setKey(key,JSONObject.toJSONString(infos));
        }
    }

    /**
     * 修改菜单，数据更新redis
     * @param permissionInfo
     */
    @Override
    public void updateUrlPermissionCache(TblPermissionInfo permissionInfo) {
        String key="urlPermission_"+permissionInfo.getPermUrl()+permissionInfo.getMethod();
        if (Objects.nonNull(permissionInfo)) {
            // TODO 线上发布redis不设置缓存时间
            redisStringUtil.setKeyExpire(key,JSONObject.toJSONString(permissionInfo),EXPIRE, TimeUnit.MINUTES);
           // redisStringUtil.setKeyExpire(key,JSONObject.toJSONString(permissionInfo));
            //如果修改了url则需要清除菜单缓存以及清除对应的角色权限缓存
            redisStringUtil.delKey("permissionInfoAll");
            List<TblRoleInfo> roleInfos = tblRoleInfoMapper.selectAll();
            List<String> collect = roleInfos.stream().map(r -> "rolesPermission_"+r.getRoleName()).collect(Collectors.toList());
            redisStringUtil.delKeyList(collect);
        }
    }

}
