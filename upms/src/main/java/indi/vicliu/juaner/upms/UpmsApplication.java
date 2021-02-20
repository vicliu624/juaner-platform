package indi.vicliu.juaner.upms;

import indi.vicliu.juaner.common.annotation.*;
import indi.vicliu.juaner.jsonapi.EnableAPIJSON;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@EnableAPIJSON
@EnableSentinelAspect
@EnableFeignClients
@EnableJuanerScheduling
@EnableDiscoveryClient
@EnableLoginArgResolver
@MapperScan("indi.vicliu.juaner.upms.data.mapper")
@SpringBootApplication
public class UpmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UpmsApplication.class, args);
	}

}
