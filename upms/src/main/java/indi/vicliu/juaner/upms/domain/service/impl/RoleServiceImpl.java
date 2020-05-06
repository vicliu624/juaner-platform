package indi.vicliu.juaner.upms.domain.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.upms.client.IdProvider;
import indi.vicliu.juaner.upms.data.mapper.TblPermissionInfoMapper;
import indi.vicliu.juaner.upms.data.mapper.TblRoleInfoMapper;
import indi.vicliu.juaner.upms.data.mapper.TblRolePermMapMapper;
import indi.vicliu.juaner.upms.data.mapper.TblUserRoleMapMapper;
import indi.vicliu.juaner.upms.domain.entity.TblPermissionInfo;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.entity.TblRolePermMap;
import indi.vicliu.juaner.upms.domain.entity.TblUserRoleMap;
import indi.vicliu.juaner.upms.domain.service.RoleService;
import indi.vicliu.juaner.upms.exception.RoleException;
import indi.vicliu.juaner.upms.utils.RedisStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import tk.mybatis.mapper.entity.Example;

import javax.xml.bind.ValidationException;
import java.util.*;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 15:09
 * @Description:
 */
@SuppressWarnings("ALL")
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private TblRoleInfoMapper roleInfoMapper;

    @Autowired
    private TblUserRoleMapMapper tblUserRoleMapMapper;

    @Autowired
    private IdProvider idProvider;

    @Autowired
    private TblPermissionInfoMapper permissionInfoMapper;

    @Autowired
    private TblRolePermMapMapper tblRolePermMapMapper;
    @Autowired
    private RedisStringUtil redisStringUtil;

    @Autowired
    private TblPermissionInfoMapper tblPermissionInfoMapper;

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
    @Override
    public JSONObject findRoleByUser(Map<String, Object> jsonMap) {
        log.info("查找角色的权限以及所有权限集合参数：{}", jsonMap);
        Long id = jsonMap.get("id") == null ? null : Long.parseLong(jsonMap.get("id").toString());
        Example rpexample = new Example(TblUserRoleMap.class);
        rpexample.createCriteria().andEqualTo("userId", id);
        List<TblUserRoleMap> list = null;
        if (id != null) {
            list = tblUserRoleMapMapper.selectByExample(rpexample);
        }
        List<TblRoleInfo> pilist = roleInfoMapper.selectAll();
        Map<Long, String> pimap = new HashMap<Long, String>();
        JSONArray dataSource = new JSONArray();
        JSONArray targetNodes = new JSONArray();
        if (pilist != null && pilist.size() > 0) {
            for (TblRoleInfo pi : pilist) {
                pimap.put(pi.getId(), pi.getId() + "-" + pi.getRoleName());
                JSONObject jo = new JSONObject();
                jo.put("key", pi.getId().toString());
                jo.put("title", pi.getId() + "-" + pi.getRoleName());
                dataSource.add(jo);
            }
        }
        if (list != null && list.size() > 0) {
            for (TblUserRoleMap rp : list) {
                JSONObject jo = new JSONObject();
                jo.put("key", rp.getRoleId().toString());
                jo.put("title", pimap.get(rp.getRoleId()));
                targetNodes.add(jo);
            }
        }
        JSONObject json = new JSONObject();
        json.put("dataSource", dataSource);
        json.put("targetNodes", targetNodes);
        return json;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveUserRole(Map<String, Object> jsonMap) {
        log.info("分配用户的角色参数 {}", jsonMap);
        Long id = jsonMap.get("id") == null ? null : Long.parseLong(jsonMap.get("id").toString());
        String permIds = jsonMap.get("permIds") == null ? null : jsonMap.get("permIds").toString();
        if (id != null) {
            Example rpexample = new Example(TblUserRoleMap.class);
            rpexample.createCriteria().andEqualTo("userId", id);
            tblUserRoleMapMapper.deleteByExample(rpexample);
            if (permIds != null && permIds.trim().length() > 0) {
                String[] permIdArr = permIds.split(",");
                for (String permId : permIdArr) {
                    if (permId != null && permId.trim().length() > 0) {
                        TblUserRoleMap rpm = new TblUserRoleMap();
                        rpm.setUserId(id);
                        rpm.setRoleId(Long.parseLong(permId.trim()));
                        tblUserRoleMapMapper.insertSelective(rpm);
                    }
                }

            }
        }
    }
    @Override
    public List<TblRoleInfo> getRoleInfoList(Map<String,Object> jsonMap){
        String roleName = jsonMap.get("roleName")==null?null:jsonMap.get("roleName").toString();
        String roleDesc = jsonMap.get("roleDesc")==null?null:jsonMap.get("roleDesc").toString();
        Example example = new Example(TblRoleInfo.class);
        example.createCriteria().andLike("roleName",roleName).andLike("roleDesc",roleDesc);
        List<TblRoleInfo> list = roleInfoMapper.selectByExample(example);
        return  list;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject saveRoleInfo(Map<String,Object> jsonMap){
        String roleName = jsonMap.get("roleName") == null ? null : jsonMap.get("roleName").toString();
        String roleDesc = jsonMap.get("roleDesc")==null?null:jsonMap.get("roleDesc").toString();
       // String roleValue = jsonMap.get("roleValue")==null?null:jsonMap.get("roleValue").toString();
        Long id = jsonMap.get("id") == null ? null : Long.parseLong(jsonMap.get("id").toString());
        JSONObject json = new JSONObject();
        if(id != null){//编辑角色信息
            TblRoleInfo ri = roleInfoMapper.selectByPrimaryKey(id);
            Example example = new Example(TblRoleInfo.class);
            example.createCriteria().andEqualTo("roleName",roleName).andNotEqualTo("id",id);
            List<TblRoleInfo> list = roleInfoMapper.selectByExample(example);
            if(list != null && list.size()>0){
                json.put("flag",0);
                json.put("msg","该角色名称已经存在，请重新填写");
                return json;
            }
            ri.setRoleDesc(roleDesc);
            ri.setRoleName(roleName);
            ri.setUpdateTime(new Date());
            roleInfoMapper.updateByPrimaryKeySelective(ri);
            json.put("flag",1);
            json.put("msg","角色修改成功");
            return json;
        }else{//新增角色信息
            TblRoleInfo ri = new TblRoleInfo();
            Example example = new Example(TblRoleInfo.class);
            example.createCriteria().andEqualTo("roleName",roleName);
            List<TblRoleInfo> list = roleInfoMapper.selectByExample(example);
            if(list != null && list.size()>0){
                json.put("flag",0);
                json.put("msg","该角色名称已经存在，请重新填写");
                return json;
            }
            ri.setRoleDesc(roleDesc);
            ri.setRoleName(roleName);
            ri.setCreateTime(new Date());
            ri.setUpdateTime(ri.getCreateTime());
            roleInfoMapper.insertSelective(ri);
            json.put("flag",1);
            json.put("msg","角色新增成功");
            return json;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRoleInfo(String ids) throws RoleException {
        String[] idArr = ids.split(",");
        for(String id : idArr){
            if(id != null && ids.trim().length()>0){
                TblRoleInfo roleInfo = roleInfoMapper.selectByPrimaryKey(Long.parseLong(id.trim()));
                if(roleInfo==null){
                    throw new RoleException("角色不存在");
                }
                Example rpexample = new Example(TblRolePermMap.class);
                rpexample.createCriteria().andEqualTo("roleId",Long.parseLong(id.trim()));
                Integer rp = tblRolePermMapMapper.deleteByExample(rpexample);
                Example urexample = new Example(TblUserRoleMap.class);
                urexample.createCriteria().andEqualTo("roleId",Long.parseLong(id.trim()));
                Integer ur = tblUserRoleMapMapper.deleteByExample(urexample);
                Integer ri = roleInfoMapper.deleteByPrimaryKey(Long.parseLong(id.trim()));
                log.info("删除角色id:"+id+",角色相关表删除个数TblRolePermMap:"+rp+",TblUserRoleMap:"+ur+",TblRoleInfo:"+ri);
                //清除缓存改角色的权限
                redisStringUtil.delKey("rolesPermission_"+roleInfo.getRoleName());
            }
        }
    }

    @Override
    public JSONObject findPermissionByRole(Map<String,Object> jsonMap){
        log.info("查找角色的权限以及所有权限集合参数：{}",jsonMap);
        Long id = jsonMap.get("id") == null ? null : Long.parseLong(jsonMap.get("id").toString());
        Example rpexample = new Example(TblRolePermMap.class);
        rpexample.createCriteria().andEqualTo("roleId",id);
        List<TblRolePermMap> list = null;
        if(id!=null){
            list = tblRolePermMapMapper.selectByExample(rpexample);
        }
        Example piexample = new Example(TblPermissionInfo.class);
//        piexample.createCriteria().andEqualTo("permType",1);
        List<TblPermissionInfo> pilist = tblPermissionInfoMapper.selectByExample(piexample);
        Map<Long, String> pimap = new HashMap<Long, String>();
        JSONArray dataSource = new JSONArray();
        JSONArray targetNodes = new JSONArray();
        if(pilist != null && pilist.size()>0){
            for(TblPermissionInfo pi : pilist){
                pimap.put(pi.getId(), pi.getId()+"-"+pi.getPermName());
                JSONObject jo = new JSONObject();
                jo.put("key",pi.getId().toString());
                jo.put("title",pi.getId()+"-"+pi.getPermName());
                dataSource.add(jo);
            }
        }
        if(list != null && list.size()>0){
            for(TblRolePermMap rp : list){
                JSONObject jo = new JSONObject();
                jo.put("key",rp.getPermId().toString());
                jo.put("title",pimap.get(rp.getPermId()));
                targetNodes.add(jo);
            }
        }
        JSONObject json = new JSONObject();
        json.put("dataSource",dataSource);
        json.put("targetNodes",targetNodes);
        return json;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveRolePerm(@RequestBody Map<String,Object> jsonMap) throws RoleException {
        log.info("分配角色的权限参数 {}",jsonMap);
        Long id = jsonMap.get("id") == null ? null : Long.parseLong(jsonMap.get("id").toString());
        String permIds = jsonMap.get("permIds") == null ? null : jsonMap.get("permIds").toString();
        if(id != null){
            TblRoleInfo info = roleInfoMapper.selectByPrimaryKey(id);
            if(info==null){
                throw new RoleException("角色不存在");
            }
            String key = "rolesPermission_"+info.getRoleName();
            Example rpexample = new Example(TblRolePermMap.class);
            rpexample.createCriteria().andEqualTo("roleId",id);
            tblRolePermMapMapper.deleteByExample(rpexample);
            // List<TblRolePermMap> list = new ArrayList<TblRolePermMap>();
            if(permIds != null && permIds.trim().length()>0){
                String[] permIdArr = permIds.split(",");
                for(String permId : permIdArr){
                    if(permId != null && permId.trim().length()>0){
                        TblRolePermMap rpm = new TblRolePermMap();
                        rpm.setRoleId(id);
                        rpm.setPermId(Long.parseLong(permId.trim()));
                        //     list.add(rpm);
                        tblRolePermMapMapper.insertSelective(rpm);
                    }
                }
                List<TblPermissionInfo> infos = permissionInfoMapper.queryByRoleCode(info.getRoleName());
                if(!infos.isEmpty()){
                    redisStringUtil.setKey(key,JSONObject.toJSONString(infos));
                }
            }
        }
    }
}
