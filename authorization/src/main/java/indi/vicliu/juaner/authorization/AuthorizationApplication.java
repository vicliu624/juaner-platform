package indi.vicliu.juaner.authorization;

import indi.vicliu.juaner.authorization.annotation.EnableArgExceptionHandler;
import indi.vicliu.juaner.common.annotation.EnableSentinelAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

@EnableSentinelAspect
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("indi.vicliu.juaner.authorization.data.mapper")
@SpringBootApplication
@EnableArgExceptionHandler
@Slf4j
public class AuthorizationApplication {

	public static void main(String[] args) {
		log.info("当前服务器时区:{}",ZoneId.systemDefault().getId());
		log.info("启动时间:{}", ZonedDateTime.now().toInstant().plusMillis(TimeUnit.HOURS.toMillis(8)));
		SpringApplication.run(AuthorizationApplication.class, args);
	}

}
