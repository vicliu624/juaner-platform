package indi.vicliu.juaner.authentication.domian.service.impl;

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.authentication.domian.service.AuthenticationService;
import indi.vicliu.juaner.authentication.domian.service.PermissionService;
import indi.vicliu.juaner.authentication.vo.PermissionInfo;
import indi.vicliu.juaner.common.core.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    /**
     * 未在资源库中的URL默认标识
     */
    public static final String NONEXISTENT_URL = "NONEXISTENT_URL";


    private static final String AUTHORITIES = "authorities";

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
        String authorization = authRequest.getHeader("Authorization");
        log.info("decide authRequest uri:{},method:{},authorization:{}",authRequest.getServletPath(),authRequest.getMethod(),authorization);
        String token = getJwt(authorization).getClaims();
        log.info(token);

        JSONObject tokenInfo = JSONObject.parseObject(token);
        if(!tokenInfo.containsKey(AUTHORITIES)){
            log.warn("token中没有authorities");
            return false;
        }

        String authorities = tokenInfo.getString(AUTHORITIES);
        log.info(authorities);
        List<String> authorityRoleCodes = JSONObject.parseArray(authorities,String.class);
        //获取此url，method访问对应的权限资源信息

        ConfigAttribute urlConfigAttribute = findConfigAttributesByUrl(authRequest);
        if (NONEXISTENT_URL.equals(urlConfigAttribute.getAttribute()))
            log.info("url:{} 未在资源池中找到，拒绝访问",urlConfigAttribute.getAttribute());

        //获取此访问用户所有角色拥有的权限资源
        Set<PermissionInfo> userResources = findResourcesByAuthorityRoles(authorityRoleCodes);
        //用户拥有权限资源 与 url要求的资源进行对比
        boolean match = isMatch(urlConfigAttribute, userResources);
        //boolean match = isMatch(authRequest, userResources);
        if(!match){
            log.warn("urlConfigAttribute [{}] match false",authRequest.getServletPath());
        }
        return match;
    }

    private ConfigAttribute findConfigAttributesByUrl(HttpServletRequest authRequest) {
        this.resourceConfigAttributes.forEach((requestMatcher, configAttribute) -> log.debug("----{}",requestMatcher.toString()));
        ConfigAttribute attribute = this.resourceConfigAttributes.keySet().stream()
                .filter(requestMatcher -> {
                    boolean isMatch = requestMatcher.matches(authRequest);
                    log.debug(" authRequest isMatch:{},uri:{},method:{},requestMatcher:{} requestMatcher to String:{}",isMatch,authRequest.getServletPath(),authRequest.getMethod(),requestMatcher.toString());
                    return isMatch;
                })
                .map(requestMatcher -> this.resourceConfigAttributes.get(requestMatcher))
                .peek(urlConfigAttribute -> log.info("url在资源池中配置：{}", urlConfigAttribute.getAttribute()))
                .findFirst()
                .orElse(new SecurityConfig(NONEXISTENT_URL));



        if(NONEXISTENT_URL.equals(attribute.getAttribute())){
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
                        .peek(urlConfigAttribute -> log.info("重新遍历->url在资源池中配置：{}", urlConfigAttribute.getAttribute()))
                        .findFirst()
                        .orElse(new SecurityConfig(NONEXISTENT_URL));
            } else {
                log.info("{}:{}在资源表中未找到",authRequest.getServletPath(),authRequest.getMethod());
            }
        }

        return attribute;
    }

    private Set<PermissionInfo> findResourcesByAuthorityRoles(List<String> authorityRoleCodes) {
        //用户被授予的角色
        log.info("用户的授权角色集合信息为:{}", authorityRoleCodes);
        return permissionService.queryByRoleValue(authorityRoleCodes);
    }

    private boolean isMatch(ConfigAttribute urlConfigAttribute, Set<PermissionInfo> userResources) {
        return userResources.stream().anyMatch(resource -> resource.getPermUrl().equals(urlConfigAttribute.getAttribute()));
    }

    private Jwt getJwt(String authentication) {
        return JwtHelper.decode(StringUtils.substring(authentication, CommonConstant.BEARER_BEGIN_INDEX));
    }

}
