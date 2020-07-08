package indi.vicliu.juaner.admin.client.config;

import indi.vicliu.juaner.admin.client.endpoint.AppInfoEndPoint;
import indi.vicliu.juaner.admin.client.endpoint.JarDependenciesEndpoint;
import indi.vicliu.juaner.admin.client.endpoint.LogFileEndPoint;
import indi.vicliu.juaner.admin.client.endpoint.LogFileRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties(LogFileRegistry.class)
public class AdminEndPointConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public JarDependenciesEndpoint jarDependenciesEndpoint() {
        return new JarDependenciesEndpoint(env);
    }

    @Bean
    public LogFileEndPoint logFileEndPoint(LogFileRegistry logFileRegistry) {
        return new LogFileEndPoint(env, logFileRegistry);
    }

    @Bean
    public AppInfoEndPoint appInfoEndPoint() {
        return new AppInfoEndPoint();
    }

}
