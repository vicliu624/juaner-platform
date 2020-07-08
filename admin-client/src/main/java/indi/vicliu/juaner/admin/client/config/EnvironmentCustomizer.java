package indi.vicliu.juaner.admin.client.config;

import org.springframework.core.env.Environment;

public interface EnvironmentCustomizer<T extends Environment> {
    void customize(T t);
}
