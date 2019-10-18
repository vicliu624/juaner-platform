package indi.vicliu.juaner.authorization.data.mapper;

import indi.vicliu.juaner.authorization.domain.entity.OauthClientToken;
import indi.vicliu.juaner.common.data.mapper.CommonRepository;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthClientTokenMapper extends CommonRepository<OauthClientToken> {
}