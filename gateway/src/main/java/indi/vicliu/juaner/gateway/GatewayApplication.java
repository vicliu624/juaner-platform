package indi.vicliu.juaner.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-19 22:24
 * @Description:
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
@MapperScan("indi.vicliu.juaner.gateway.data.mapper")
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
