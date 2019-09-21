package indi.vicliu.juaner.upms.data.mapper;

import indi.vicliu.juaner.common.data.mapper.CommonRepository;
import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface TblUserInfoMapper extends CommonRepository<TblUserInfo> {
}