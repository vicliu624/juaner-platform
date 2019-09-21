package indi.vicliu.juaner.upms.data.mapper;

import indi.vicliu.juaner.common.data.mapper.CommonRepository;
import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TblRoleInfoMapper extends CommonRepository<TblRoleInfo> {

    @Select("SELECT r.* FROM tbl_role_info r, tbl_user_role_map ur WHERE r.id = ur.role_id AND ur.user_id = #{userId}")
    List<TblRoleInfo> queryRolesByUserId(Long userId);
}