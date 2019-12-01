package indi.vicliu.juaner.ndbcluster.analyze;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Auther: liuweikai
 * @Date: 2019-12-01 15:04
 * @Description:
 */
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("indi.vicliu.juaner.ndbcluster.analyze.data.mapper")
@SpringBootApplication
public class NdbClusterAnalyzeApplication {
    public static void main(String[] args) {
        SpringApplication.run(NdbClusterAnalyzeApplication.class, args);
    }
}
