package indi.vicliu.juaner.upms.data.mapper;

import indi.vicliu.juaner.common.data.mapper.CommonRepository;
import indi.vicliu.juaner.upms.domain.entity.TblPermissionInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TblPermissionInfoMapper extends CommonRepository<TblPermissionInfo> {

    @Select("<script> " +
            "SELECT p.* FROM tbl_permission_info p,tbl_role_perm_map rp,tbl_role_info r WHERE p.id = rp.perm_id AND r.id = rp.role_id " +
            " and r.role_name in <foreach item='item' index='index' collection='roleValues' open='(' separator=',' close=')'> #{item} </foreach> " +
            "</script>")
    List<TblPermissionInfo> queryByRoleCodes(@Param("roleValues") String[] roleValues);

    @Select("<script> " +
            "SELECT p.* FROM tbl_permission_info p,tbl_role_perm_map rp,tbl_role_info r WHERE p.id = rp.perm_id AND r.id = rp.role_id " +
            " and r.role_name =#{roleValues}" +
            "</script>")
    List<TblPermissionInfo> queryByRoleCode(@Param("roleValues") String roleValues);
}