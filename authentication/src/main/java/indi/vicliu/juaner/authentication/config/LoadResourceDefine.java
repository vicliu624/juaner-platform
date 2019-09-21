package indi.vicliu.juaner.authentication.config;

import indi.vicliu.juaner.authentication.domian.service.PermissionService;
import indi.vicliu.juaner.authentication.vo.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LoadResourceDefine {
    @Autowired
    private HandlerMappingIntrospector mvcHandlerMappingIntrospector;

    @Autowired
    private PermissionService permissionService;

    @Bean
    public Map<RequestMatcher, ConfigAttribute> resourceConfigAttributes() {
        Set<PermissionInfo> resources = permissionService.findAll();
        resources.stream().forEach( item -> log.debug(" put url:{},method:{}",item.getPermUrl(),item.getMethod()));
        Map<RequestMatcher, ConfigAttribute> map = resources.stream()
                .collect(Collectors.toMap(
                        resource -> {
                            MvcRequestMatcher mvcRequestMatcher = new MvcRequestMatcher(mvcHandlerMappingIntrospector, resource.getPermUrl());
                            mvcRequestMatcher.setMethod(HttpMethod.resolve(resource.getMethod()));
                            return mvcRequestMatcher;
                        },
                        resource -> new SecurityConfig(resource.getPermUrl())
                        )
                );
        return map;
    }
}
