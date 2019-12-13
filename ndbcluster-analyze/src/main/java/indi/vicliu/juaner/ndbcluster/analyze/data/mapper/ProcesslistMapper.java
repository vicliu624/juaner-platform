package indi.vicliu.juaner.ndbcluster.analyze.data.mapper;

import indi.vicliu.juaner.common.data.mapper.CommonRepository;
import indi.vicliu.juaner.ndbcluster.analyze.domain.entity.Processlist;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ProcesslistMapper extends CommonRepository<Processlist> {

    @Select(" select state,sum(duration) as total_r, " +
            " round( " +
            " 100 * sum(duration) / " +
            " (select sum(duration) " +
            " from information_schema.profiling " +
            " where query_id = #{queryId} " +
            " ),2) as Pct_r, " +
            " count(*) as Calls, " +
            " sum(duration)/count(*) as R-Call " +
            " from information_schema.profiling " +
            " where query_id = #{queryId} " +
            " group by state " +
            " order by total_r desc ")
    Map<String,String> showSqlProfilingById(long id);
}