package indi.vicliu.juaner.gateway.data.mapper;

import indi.vicliu.juaner.common.data.mapper.CommonRepository;
import indi.vicliu.juaner.gateway.domain.entity.TblCryptoInfo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface TblCryptoInfoMapper extends CommonRepository<TblCryptoInfo> {
}