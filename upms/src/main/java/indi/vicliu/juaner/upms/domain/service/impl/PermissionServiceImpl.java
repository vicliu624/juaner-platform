package indi.vicliu.juaner.upms.domain.service.impl;

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.upms.data.mapper.TblPermissionInfoMapper;
import indi.vicliu.juaner.upms.data.mapper.TblRoleInfoMapper;
import indi.vicliu.juaner.upms.data.mapper.TblRolePermMapMapper;
import indi.vicliu.juaner.upms.domain.entity.TblPermissionInfo;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.entity.TblRolePermMap;
import indi.vicliu.juaner.upms.domain.service.PermissionService;
import indi.vicliu.juaner.upms.utils.RedisStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-19 12:52
 * @Description:
 */
@SuppressWarnings("ALL")
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private TblPermissionInfoMapper permissionInfoMapper;

    @Autowired
    private TblRoleInfoMapper tblRoleInfoMapper;

    @Autowired
    private RedisStringUtil redisStringUtil;
    @Autowired
    private TblRolePermMapMapper tblRolePermMapMapper;

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
            log.debug("queryByRoles:{}",data);
            if(Objects.nonNull(data) && Objects.nonNull(JSONObject.parseArray(data, TblPermissionInfo.class))){
                List<TblPermissionInfo> permissionInfos = JSONObject.parseArray(data, TblPermissionInfo.class);
                tblPermissionInfoList.addAll(permissionInfos);
            }else {
                log.info("缓存未命中:key{}",key);
                List<TblPermissionInfo> infos = permissionInfoMapper.queryByRoleCode(role);
                tblPermissionInfoList.addAll(infos);
                if(!infos.isEmpty()){
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
        log.debug("findByURI:{}",data);
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
            //redisStringUtil.setKeyExpire("permissionInfoAll",JSONObject.toJSONString(tblPermissionInfos),EXPIRE, TimeUnit.MINUTES);
             redisStringUtil.setKey("permissionInfoAll",JSONObject.toJSONString(tblPermissionInfos));
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
            String value = JSONObject.toJSONString(infos);
            log.info("更新redis:{} -> {}",key,value);
            redisStringUtil.setKey(key,value);
        }
    }

    /**
     * 修改菜单，数据更新redis
     * @param permissionInfo
     */
    @Override
    public void updateUrlPermissionCache(TblPermissionInfo permissionInfo) {
        String key="urlPermission_"+permissionInfo.getPermUrl()+permissionInfo.getMethod();
        log.debug("updateUrlPermissionCache:{}",key);
        if (Objects.nonNull(permissionInfo)) {
           // redisStringUtil.setKeyExpire(key,JSONObject.toJSONString(permissionInfo),EXPIRE, TimeUnit.MINUTES);
            redisStringUtil.setKey(key,JSONObject.toJSONString(permissionInfo));
            //如果修改了url则需要清除菜单缓存以及清除对应的角色权限缓存
            List<TblPermissionInfo> tblPermissionInfos = permissionInfoMapper.selectAll();
            if(!tblPermissionInfos.isEmpty()){
                redisStringUtil.setKey("permissionInfoAll",JSONObject.toJSONString(tblPermissionInfos));
            }
            List<TblRoleInfo> roleInfos = tblRoleInfoMapper.selectAll();
            List<String> collect = roleInfos.stream().map(r -> "rolesPermission_"+r.getRoleName()).collect(Collectors.toList());
            redisStringUtil.delKeyList(collect);
        }
    }
    @Override
    public List<TblPermissionInfo> list(Map<String,Object> jsonMap){
        String permName = jsonMap.get("permName")==null?null:jsonMap.get("permName").toString();
        String permValue = jsonMap.get("permValue")==null?null:jsonMap.get("permValue").toString();
        String method = jsonMap.get("method")==null?null:jsonMap.get("method").toString().trim();
        String permUrl = jsonMap.get("permUrl")==null?null:jsonMap.get("permUrl").toString();
        Integer permType = jsonMap.get("permType") == null ? null : (Integer) jsonMap.get("permType");
        Integer pageNum = jsonMap.get("pageNum") == null ? 1 : (Integer) jsonMap.get("pageNum");
        Integer pageSize = jsonMap.get("pageSize") == null ? 10 : (Integer) jsonMap.get("pageSize");
        Boolean isExp = jsonMap.get("isExp") == null ? false : (Boolean) jsonMap.get("isExp");
        Example example = new Example(TblPermissionInfo.class);
        example.createCriteria().andLike("permName",permName)
                .andLike("permUrl",permUrl)
                .andEqualTo("method",method);
        return permissionInfoMapper.selectByExample(example);
    }
    @Override
    public JSONObject savePermission(Map<String,Object> jsonMap){
        String permName = jsonMap.get("permName") == null ? null : jsonMap.get("permName").toString().trim();
        String permValue = jsonMap.get("permValue")==null?null:jsonMap.get("permValue").toString().trim();
        String method = jsonMap.get("method")==null?null:jsonMap.get("method").toString().trim();
        String permUrl = jsonMap.get("permUrl")==null?null:jsonMap.get("permUrl").toString().trim();
        String description = jsonMap.get("description")==null?null:jsonMap.get("description").toString().trim();
        Long id = jsonMap.get("id") == null ? null : Long.parseLong(jsonMap.get("id").toString());
        Integer permType = jsonMap.get("permType") == null ? null : Integer.parseInt(jsonMap.get("permType").toString());
        JSONObject json = new JSONObject();
        if(id != null){//编辑权限信息
            TblPermissionInfo ri = permissionInfoMapper.selectByPrimaryKey(id);
            Example example2 = new Example(TblPermissionInfo.class);
            example2.createCriteria().andEqualTo("permUrl",permUrl).andNotEqualTo("id",id)
                    .andEqualTo("method", method);
            List<TblPermissionInfo> list2 = permissionInfoMapper.selectByExample(example2);
            if(list2 != null && list2.size()>0){
                json.put("flag",0);
                json.put("msg","该权限对应URL已经存在"+method+"提交，请重新填写");
                return json;
            }
            ri.setPermUrl(permUrl);
            ri.setPermName(permName);
            ri.setUpdateTime(new Date());
            ri.setMethod(method);
            ri.setDescription(description);
            permissionInfoMapper.updateByPrimaryKey(ri);
            updateUrlPermissionCache(ri);
            json.put("flag",1);
            json.put("msg","权限修改成功");
            return json;
        }else{//新增权限信息
            TblPermissionInfo ri = new TblPermissionInfo();
            Example example2 = new Example(TblPermissionInfo.class);
            example2.createCriteria().andEqualTo("permUrl",permUrl).andEqualTo("method", method);
            List<TblPermissionInfo> list2 = permissionInfoMapper.selectByExample(example2);
            if(list2 != null && list2.size()>0){
                json.put("flag",0);
                json.put("msg","该权限对应URL已经存在"+method+"提交，请重新填写");
                return json;
            }
            ri.setPermUrl(permUrl);
            ri.setPermName(permName);
            ri.setMethod(method);
            ri.setDescription(description);
            ri.setCreateTime(new Date());
            ri.setUpdateTime(ri.getCreateTime());
            permissionInfoMapper.insertSelective(ri);
            updateUrlPermissionCache(ri);
            json.put("flag",1);
            json.put("msg","权限新增成功");
            return json;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removePermission( String ids){
        String[] idArr = ids.split(",");
        for(String id : idArr){
            if(id != null && ids.trim().length()>0){
                Example rpexample = new Example(TblRolePermMap.class);
                rpexample.createCriteria().andEqualTo("permId",Long.parseLong(id.trim()));
                Integer rp = tblRolePermMapMapper.deleteByExample(rpexample);
                Integer ri = permissionInfoMapper.deleteByPrimaryKey(Long.parseLong(id.trim()));
                log.info("删除权限id:{},角色相关表删除个数TblRolePermMap:{},TblRoleInfo:{}",id,rp,ri);
            }
        }
    }

    @Override
    public List<Long> getUserPermission(Long id) {
        return permissionInfoMapper.getUserPermission(id);
    }
}
