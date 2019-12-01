package indi.vicliu.juaner.ndbcluster.analyze.data.mapper;

import indi.vicliu.juaner.common.data.mapper.CommonRepository;
import indi.vicliu.juaner.ndbcluster.analyze.domain.entity.Processlist;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcesslistMapper extends CommonRepository<Processlist> {
}