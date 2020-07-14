package indi.vicliu.juaner.admin.controller;

import de.codecentric.boot.admin.server.domain.entities.Application;
import de.codecentric.boot.admin.server.services.ApplicationRegistry;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/management/admin")
public class SpringBootAdminController {

    @Autowired
    private ApplicationRegistry registry;

    @GetMapping(value = "/applications",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result applications(){
        List<Application> apps = new ArrayList<>();
        Flux<Application> appsFlux = registry.getApplications();
        appsFlux.subscribe(apps::add);
        log.info("size:{}",apps.size());
        return Result.success(apps);
    }

    @PostMapping(value = "/application")
    public Result application(@RequestBody Map<String,String> application){
        List<Application> apps = new ArrayList<>();
        if(application == null || StringUtils.isEmpty(application.get("name"))){
            return Result.fail("name == null");
        }
        String name = application.get("name");
        Mono<Application> appMono = registry.getApplication(name);
        appMono.subscribe(apps::add);
        log.info("size:{}",apps.size());
        return apps.isEmpty() ? Result.fail("未查询到这样的application") : Result.success(apps.get(0));
    }

    @DeleteMapping(path = "/application")
    public Result deleteApplication(@RequestBody Map<String,String> application){
        if(application == null || StringUtils.isEmpty(application.get("name"))){
            return Result.fail("name == null");
        }
        String name = application.get("name");
        log.debug("Unregister application with name '{}'", name);
        try{
            registry.deregister(name).collectList().map((deregistered) -> !deregistered.isEmpty()
                    ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build());
            return Result.success("删除成功");
        } catch (Exception e){
            log.error("删除application失败",e);
            return Result.fail("删除失败"+ e.getMessage());
        }
    }
}
