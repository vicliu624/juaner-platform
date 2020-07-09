package indi.vicliu.juaner.admin.client.config;

import indi.vicliu.juaner.admin.client.endpoint.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.info.SimpleInfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
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

    @Bean
    public JuanerMetricsEndpoint juanerMetricsEndpoint(){
        return new JuanerMetricsEndpoint();
    }

    @Bean
    public GCLogEndpoint gcLogEndpoint(){
        return new GCLogEndpoint();
    }

    @Bean
    @ConditionalOnBean(CacheManager.class)
    public CacheManagerEndpoint cacheManagerEndpoint(){
        return new CacheManagerEndpoint();
    }

    @Bean
    public SimpleInfoContributor springBootVersionInfoContributor() {
        return new SimpleInfoContributor("spring-boot-version", SpringBootVersion.getVersion());
    }
    @Bean
    public MetaDataProvider metaDataProvider() {
        return new MetaDataProvider();
    }

}
