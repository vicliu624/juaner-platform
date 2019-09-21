package indi.vicliu.juaner.authentication.domian.service.impl;

import indi.vicliu.juaner.authentication.domian.service.AuthenticationService;
import indi.vicliu.juaner.authentication.domian.service.PermissionService;
import indi.vicliu.juaner.authentication.vo.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    /**
     * 未在资源库中的URL默认标识
     */
    public static final String NONEXISTENT_URL = "NONEXISTENT_URL";

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private HandlerMappingIntrospector mvcHandlerMappingIntrospector;

    /**
     * 系统中所有权限集合
     */
    Map<RequestMatcher, ConfigAttribute> resourceConfigAttributes;

    @Autowired
    public AuthenticationServiceImpl(Map<RequestMatcher, ConfigAttribute> resourceConfigAttributes) {
        this.resourceConfigAttributes = resourceConfigAttributes;
    }


    @Override
    public boolean decide(HttpServletRequest authRequest) {
        log.info("decide authRequest uri:{},method:{}",authRequest.getServletPath(),authRequest.getMethod());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //获取此url，method访问对应的权限资源信息

        ConfigAttribute urlConfigAttribute = findConfigAttributesByUrl(authRequest);
        if (NONEXISTENT_URL.equals(urlConfigAttribute.getAttribute()))
            log.debug("url:{} 未在资源池中找到，拒绝访问",urlConfigAttribute.getAttribute());

        //获取此访问用户所有角色拥有的权限资源
        Set<PermissionInfo> userResources = findResourcesByAuthorityRoles(authentication.getAuthorities());
        //用户拥有权限资源 与 url要求的资源进行对比
        boolean match = isMatch(urlConfigAttribute, userResources);
        //boolean match = isMatch(authRequest, userResources);
        if(!match){
            log.warn("urlConfigAttribute [{}] match false",authRequest.getServletPath());
        }
        return match;
    }

    private ConfigAttribute findConfigAttributesByUrl(HttpServletRequest authRequest) {
        //this.resourceConfigAttributes.forEach((requestMatcher, configAttribute) -> log.info("----{},{}",requestMatcher.toString(),configAttribute.getAttribute()));
        ConfigAttribute attribute = this.resourceConfigAttributes.keySet().stream()
                .filter(requestMatcher -> {
                    boolean isMatch = requestMatcher.matches(authRequest);
                    log.info("isMatch:{},uri:{},method:{}",isMatch,authRequest.getServletPath(),authRequest.getMethod());
                    return isMatch;
                })
                .map(requestMatcher -> this.resourceConfigAttributes.get(requestMatcher))
                .peek(urlConfigAttribute -> log.debug("url在资源池中配置：{}", urlConfigAttribute.getAttribute()))
                .findFirst()
                .orElse(new SecurityConfig(NONEXISTENT_URL));

        if(NONEXISTENT_URL.equals(attribute.getAttribute())) {
            log.info("query uri :{}:{}",authRequest.getServletPath(),authRequest.getMethod());
            PermissionInfo permissionInfo = permissionService.findByURI(authRequest.getServletPath(),authRequest.getMethod());
            if(permissionInfo != null){
                log.info("新增资源{}:{}",permissionInfo.getPermUrl(),permissionInfo.getMethod());
                MvcRequestMatcher mvcRequestMatcher = new MvcRequestMatcher(mvcHandlerMappingIntrospector, permissionInfo.getPermUrl());
                mvcRequestMatcher.setMethod(HttpMethod.resolve(permissionInfo.getMethod()));
                this.resourceConfigAttributes.put(mvcRequestMatcher,new SecurityConfig(permissionInfo.getPermUrl()));

                attribute = this.resourceConfigAttributes.keySet().stream()
                        .filter(requestMatcher -> requestMatcher.matches(authRequest))
                        .map(requestMatcher -> this.resourceConfigAttributes.get(requestMatcher))
                        .peek(urlConfigAttribute -> log.debug("重新遍历->url在资源池中配置：{}", urlConfigAttribute.getAttribute()))
                        .findFirst()
                        .orElse(new SecurityConfig(NONEXISTENT_URL));
            } else {
                log.warn("{}:{}在资源表中未找到",authRequest.getMethod(),authRequest.getServletPath());
            }
        }

        return attribute;
    }

    private Set<PermissionInfo> findResourcesByAuthorityRoles(Collection<? extends GrantedAuthority> authorityRoles) {
        //用户被授予的角色
        log.debug("用户的授权角色集合信息为:{}", authorityRoles);
        List<String> authorityRoleCodes = authorityRoles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Set<PermissionInfo> resources = permissionService.queryByRoleValue(authorityRoleCodes);
        return resources;
    }

    public boolean isMatch(ConfigAttribute urlConfigAttribute, Set<PermissionInfo> userResources) {
        return userResources.stream().anyMatch(resource -> resource.getPermUrl().equals(urlConfigAttribute.getAttribute()));
    }

}
