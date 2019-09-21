package indi.vicliu.juaner.authentication.domian.service;

import indi.vicliu.juaner.authentication.vo.PermissionInfo;

import java.util.List;
import java.util.Set;

public interface PermissionService {
    Set<PermissionInfo> findAll();
    Set<PermissionInfo> queryByRoleValue(List<String> roleValues);
    PermissionInfo findByURI(String uri,String method);
}