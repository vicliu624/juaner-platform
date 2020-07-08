package indi.vicliu.juaner.admin.client.config;

import org.springframework.context.ConfigurableApplicationContext;

public interface ApplicationContextCustomizer<T extends ConfigurableApplicationContext> {
    void customize(T t);
}
