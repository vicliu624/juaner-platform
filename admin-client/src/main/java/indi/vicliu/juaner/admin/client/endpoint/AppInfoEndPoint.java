package indi.vicliu.juaner.admin.client.endpoint;

import indi.vicliu.juaner.admin.client.utils.Analyzer;
import indi.vicliu.juaner.admin.client.model.JarDependencies;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestControllerEndpoint(id = "appinfo")
@Slf4j
public class AppInfoEndPoint {
    @Autowired
    private Environment env;

    private Map<String, Object> cache = new ConcurrentHashMap<>();

    @RequestMapping(method = {RequestMethod.GET})
    public Object get() {
        return cache;
    }

    private String nullToEmpty(Object o){
        if(null==o){
            return "";
        }
        return o.toString();
    }

    @PostConstruct
    public void init() {
        CompletableFuture.runAsync(() -> {
            String appName = env.getProperty("spring.application.name", "");
            try {
                JarDependencies dependencies = Analyzer.getAllPomInfo();
                cache.put("appName", nullToEmpty(appName));
                cache.put("springBootVersion",nullToEmpty(dependencies.getSpringBootVersion()));
                cache.put("springCloudVersion",nullToEmpty(dependencies.getSpringCloudVersion()));
                List<HashMap<String, String>> list = new ArrayList<>();
                dependencies.getPomInfos().forEach(p -> {
                    if (p.getGroupId().equals("org.springframework.cloud")
                            && p.getArtifactId().startsWith("spring-cloud")) {
                        HashMap<String, String> kv = new HashMap<>();
                        kv.put(p.getArtifactId(), p.getVersion());
                        list.add(kv);
                    }
                });
                cache.put("using", list);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
