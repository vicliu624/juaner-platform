package indi.vicliu.juaner.ndbcluster.analyze.controller;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.ndbcluster.analyze.domain.entity.Processlist;
import indi.vicliu.juaner.ndbcluster.analyze.domain.service.PerformanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: liuweikai
 * @Date: 2019-12-01 15:49
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/open/database/ndbcluster")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    @GetMapping("/slowSql")
    public Result showSlowSql(@RequestParam long time){
        try{
            List<Processlist> processlist = performanceService.showSlowSql(time);
            return Result.success(processlist);
        } catch (Exception e){
            return Result.fail("查询proccesslist出错");
        }
    }
}
