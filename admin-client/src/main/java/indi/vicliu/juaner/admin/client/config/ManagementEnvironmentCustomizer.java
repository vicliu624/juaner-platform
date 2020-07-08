package indi.vicliu.juaner.admin.client.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

/**
 * 内嵌 Web 容器模式运行时，指定一些 Management 的属性配置
 */
@Slf4j
public class ManagementEnvironmentCustomizer implements EnvironmentCustomizer<ConfigurableEnvironment> {


    private static final int SPRINGBOOT_MANAGEMENT_PORT_VALUE = 8081;

    private static final String SPRINGBOOT_MANAGEMENT_PORT_KEY = "management.server.port";

    private static final String DEFAULT_PROPERTY = "META-INF/moss-client/bootstrap.properties";

    @Override
    public void customize(ConfigurableEnvironment env) {
        try {
            Properties props;
            ClassPathResource resource = new ClassPathResource(DEFAULT_PROPERTY);
            props = PropertiesLoaderUtils.loadProperties(resource);
            props.put(SPRINGBOOT_MANAGEMENT_PORT_KEY, getManagementPort(env));
            env.getPropertySources().addLast(new PropertiesPropertySource("managementProperties", props));
        } catch (IOException e) {
            log.error("Failed to load {}",DEFAULT_PROPERTY);
        }
    }

    private int getManagementPort(ConfigurableEnvironment env) {
        if (!"prod".equalsIgnoreCase(env.getProperty("spring.profiles.active"))) {
            try {
                //不是生产环境，使用Socket去连接如果能连接上表示端口被占用
                InetAddress Address = InetAddress.getByName("127.0.0.1");
                Socket socket = new Socket(Address, SPRINGBOOT_MANAGEMENT_PORT_VALUE);
                log.info("{}:port is used,return:0",SPRINGBOOT_MANAGEMENT_PORT_VALUE);
                return 0;
            } catch (IOException e) {
                log.info("{}:port is not used",SPRINGBOOT_MANAGEMENT_PORT_VALUE);
            }
        }
        return SPRINGBOOT_MANAGEMENT_PORT_VALUE ;
    }
}

