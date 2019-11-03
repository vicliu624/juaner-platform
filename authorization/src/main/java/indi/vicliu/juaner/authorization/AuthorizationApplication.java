package indi.vicliu.juaner.authorization;

import indi.vicliu.juaner.common.annotation.EnableSentinelAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@EnableSentinelAspect
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("indi.vicliu.juaner.authorization.data.mapper")
@SpringBootApplication
public class AuthorizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationApplication.class, args);
	}

}
