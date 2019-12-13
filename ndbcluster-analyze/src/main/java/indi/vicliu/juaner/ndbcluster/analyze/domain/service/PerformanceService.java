package indi.vicliu.juaner.ndbcluster.analyze.domain.service;

import indi.vicliu.juaner.ndbcluster.analyze.domain.entity.Processlist;

import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2019-12-01 15:49
 * @Description:
 */
public interface PerformanceService {
    List<Processlist> showSlowSql(long time);
}
