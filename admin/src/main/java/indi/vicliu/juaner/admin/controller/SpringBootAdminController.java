package indi.vicliu.juaner.admin.controller;

import de.codecentric.boot.admin.server.domain.entities.Application;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.values.Endpoint;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.eventstore.InstanceEventStore;
import de.codecentric.boot.admin.server.services.ApplicationRegistry;
import de.codecentric.boot.admin.server.services.InstanceRegistry;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/management/admin")
public class SpringBootAdminController {

    @Autowired
    private ApplicationRegistry applicationRegistry;

    @Autowired
    private InstanceRegistry instanceRegistry;

    @Autowired
    private InstanceEventStore eventStore;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "/applications",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result applications(){
        List<Application> apps = new ArrayList<>();
        Flux<Application> appsFlux = applicationRegistry.getApplications();
        appsFlux.subscribe(apps::add);
        log.info("size:{}",apps.size());
        return Result.success(apps);
    }

    @GetMapping(value = "/application")
    public Result application(@RequestParam("name") String name){
        List<Application> apps = new ArrayList<>();
        Mono<Application> appMono = applicationRegistry.getApplication(name);
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
            applicationRegistry.deregister(name).collectList().map((deregistered) -> !deregistered.isEmpty()
                    ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build());
            return Result.success("删除成功");
        } catch (Exception e){
            log.error("删除application失败",e);
            return Result.fail("删除失败"+ e.getMessage());
        }
    }

    @GetMapping(path = "/instances")
    public Result instances(){
        List<Instance> instances = new ArrayList<>();
        Flux<Instance> instanceFlux = instanceRegistry.getInstances().filter(Instance::isRegistered);
        instanceFlux.subscribe(instances::add);
        log.info("size:{}",instances.size());
        return Result.success(instances);
    }

    @GetMapping(path = "/instanceByName")
    public Result instanceByName(@RequestParam("name") String name) {
        List<Instance> instances = new ArrayList<>();
        Flux<Instance> instanceFlux = instanceRegistry.getInstances(name).filter(Instance::isRegistered);
        instanceFlux.subscribe(instances::add);
        log.info("size:{}",instances.size());
        return Result.success(instances);
    }

    @GetMapping(path = "/instanceById")
    public Result instanceById(@RequestParam("id") String id) {
        List<Instance> instances = new ArrayList<>();
        Mono<Instance> instanceFlux = instanceRegistry.getInstance(InstanceId.of(id)).filter(Instance::isRegistered);
        instanceFlux.subscribe(instances::add);
        log.info("size:{}",instances.size());
        return instances.isEmpty() ? Result.fail("未查询到这样的instance") : Result.success(instances.get(0));
    }

    @DeleteMapping(path = "/instance/")
    public Result deleteInstance(@RequestParam("id") String id){
        try{
            instanceRegistry.deregister(InstanceId.of(id)).map((v) -> ResponseEntity.noContent().<Void>build())
                    .defaultIfEmpty(ResponseEntity.notFound().build());
            return Result.success("删除成功");
        } catch (Exception e){
            log.error("删除instance失败",e);
            return Result.fail("删除失败"+ e.getMessage());
        }
    }

    @GetMapping(path = "/instances/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result instancesEvents(){
        List<InstanceEvent> events = new ArrayList<>();
        Flux<InstanceEvent> eventFlux = eventStore.findAll();
        eventFlux.subscribe(events::add);
        log.info("size:{}",events.size());
        return Result.success(events);
    }

    @PostMapping(path = "/instances", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result register(@RequestBody Registration registration, UriComponentsBuilder builder){
        try{
            Registration withSource = Registration.copyOf(registration).source("http-api").build();
            Mono<ResponseEntity<Map<String, InstanceId>>> mono = instanceRegistry.register(withSource).map((id) -> {
                URI location = builder.replacePath("/instances/{id}").buildAndExpand(id).toUri();
                return ResponseEntity.created(location).body(Collections.singletonMap("id", id));
            });
            return Result.success("注册实例成功");
        } catch (Exception e){
            log.error("注册实例失败,",e);
            return Result.fail("注册实例失败:" + e.getMessage());
        }
    }

    @GetMapping(path = "/queryActuatorByInstanceId")
    public Result getInstanceActuator(@RequestParam("id") String id){
        List<Instance> instances = new ArrayList<>();
        Mono<Instance> instanceFlux = instanceRegistry.getInstance(InstanceId.of(id)).filter(Instance::isRegistered);
        instanceFlux.subscribe(instances::add);
        log.info("size:{}",instances.size());
        if(instances.isEmpty()){
            return Result.fail("查询不到节点");
        }
        List<String> ids = instances.get(0).getEndpoints().stream().map(Endpoint::getId).collect(Collectors.toList());
        return Result.success(ids);
    }

    @GetMapping(path = "/executeActuator")
    public Result execActuator(@RequestParam("instanceId") String id,@RequestParam("actuator") String actuator){
        List<Instance> instances = new ArrayList<>();
        Mono<Instance> instanceFlux = instanceRegistry.getInstance(InstanceId.of(id)).filter(Instance::isRegistered);
        instanceFlux.subscribe(instances::add);
        log.info("size:{}",instances.size());
        if(instances.isEmpty()){
            return Result.fail("查询不到节点");
        }
        Optional<Endpoint> endpointOptional = instances.get(0).getEndpoints().stream().filter(x -> x.getId().equalsIgnoreCase(actuator)).findFirst();
        if(endpointOptional.isPresent()){
            Endpoint endpoint = endpointOptional.get();
            String url = endpoint.getUrl();
            if(!url.contains("logfile")){
                ResponseEntity<String> responseEntity = restTemplate.getForEntity(url,String.class);
                if(responseEntity.getStatusCode() != HttpStatus.OK){
                    return Result.fail("请求该实例失败 http status:"+responseEntity.getStatusCode().value());
                }
                return Result.success(responseEntity.getBody());
            } else {
                return Result.fail("该接口不支持日志下载");
            }
        } else {
            return Result.fail("查询不到actuator");
        }
    }
}
