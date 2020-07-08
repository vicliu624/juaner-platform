package indi.vicliu.juaner.admin.client.endpoint;

import indi.vicliu.juaner.admin.client.utils.Analyzer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestControllerEndpoint(id = "jardeps")
public class JarDependenciesEndpoint {
    private Environment env;

    private Object cachedObject;

    public JarDependenciesEndpoint(Environment env) {
        this.env = env;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public Object invoke() {
        try {
            if (cachedObject == null) {
                cachedObject = Analyzer.getAllPomInfo();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return cachedObject;
    }

    @PostConstruct
    public void init() {
        CompletableFuture.runAsync(() -> {
            try {
                cachedObject = Analyzer.getAllPomInfo();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
