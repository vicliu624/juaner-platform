package indi.vicliu.juaner.id;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Slf4j
@EnableDiscoveryClient
@EnableSwagger2
@SpringBootApplication
public class IdApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(IdApplication.class, args);
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean containsOption = args.containsOption("node.id");
        if (!containsOption) {
            log.warn("未指定node.id");
            throw new IllegalArgumentException("未指定node.id");
        }
    }

}
