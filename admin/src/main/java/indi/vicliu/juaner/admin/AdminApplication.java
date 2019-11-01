package indi.vicliu.juaner.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Auther: liuweikai
 * @Date: 2019-10-31 19:42
 * @Description:
 */
@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
