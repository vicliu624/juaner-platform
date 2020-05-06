package indi.vicliu.juaner.ndbcluster.analyze.domain.service.impl;

import indi.vicliu.juaner.ndbcluster.analyze.data.mapper.ProcesslistMapper;
import indi.vicliu.juaner.ndbcluster.analyze.domain.entity.Processlist;
import indi.vicliu.juaner.ndbcluster.analyze.domain.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2019-12-01 15:50
 * @Description:
 */
@Service
public class PerformanceServiceImpl implements PerformanceService {


    @Autowired
    private ProcesslistMapper processlistMapper;

    @Override
    public List<Processlist> showSlowSql(long time) {
        Example example = new Example(Processlist.class);
        example.createCriteria().andEqualTo("command","Query")
                .andGreaterThanOrEqualTo("time",time);
        return processlistMapper.selectByExample(example);
    }
}
