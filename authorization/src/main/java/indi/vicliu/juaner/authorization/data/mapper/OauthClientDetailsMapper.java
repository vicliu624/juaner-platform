package indi.vicliu.juaner.authorization.data.mapper;

import indi.vicliu.juaner.authorization.domain.entity.OauthClientDetails;
import indi.vicliu.juaner.common.data.mapper.CommonRepository;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthClientDetailsMapper extends CommonRepository<OauthClientDetails> {
}