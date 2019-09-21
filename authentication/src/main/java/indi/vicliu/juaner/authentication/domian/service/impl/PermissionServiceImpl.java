package indi.vicliu.juaner.authentication.domian.service.impl;

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.authentication.domian.service.PermissionService;
import indi.vicliu.juaner.authentication.provider.PermissionProvider;
import indi.vicliu.juaner.authentication.vo.PermissionInfo;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionProvider permissionProvider;

    @Override
    public Set<PermissionInfo> findAll() {
        Result ret = permissionProvider.getAllPermissions();
        return convertPermissionInfos(ret);
    }

    @Override
    public Set<PermissionInfo> queryByRoleValue(List<String> roleValues) {
        Result ret = permissionProvider.getPermissionsByRoles(roleValues);
        return convertPermissionInfos(ret);
    }

    @Override
    public PermissionInfo findByURI(String uri, String method) {
        Result ret = permissionProvider.getPermissionsByUri(uri,method);
        if(ret.isFail()){
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(ret.getData()),PermissionInfo.class);
    }

    private Set<PermissionInfo> convertPermissionInfos(Result ret) {
        if(ret.isFail()){
            log.error("取权限列表失败");
            return new HashSet<>();
        }
        String permissionsStr = JSONObject.toJSONString(ret.getData());
        List<PermissionInfo> permissionInfos = JSONObject.parseArray(permissionsStr,PermissionInfo.class);
        return new HashSet<>(permissionInfos);
    }
}
