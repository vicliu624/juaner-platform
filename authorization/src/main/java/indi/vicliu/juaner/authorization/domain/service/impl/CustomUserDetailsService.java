package indi.vicliu.juaner.authorization.domain.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.authorization.Constant;
import indi.vicliu.juaner.authorization.security.core.userdetails.CustomUser;
import indi.vicliu.juaner.authorization.security.core.userdetails.CustomUserDetails;
import indi.vicliu.juaner.authorization.provider.UpmsProvider;
import indi.vicliu.juaner.authorization.utils.RedisStringUtil;
import indi.vicliu.juaner.authorization.vo.RoleInfo;
import indi.vicliu.juaner.authorization.vo.UserInfo;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: liuweikai
 * @Date: 2019/3/19 15:12
 * @Description:
 */
@Slf4j
@Service()
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private UpmsProvider upmsProvider;

    @Autowired
    private RedisStringUtil redisStringUtil;

    @SentinelResource(value = "loadUserByUsername" , blockHandler = "exceptionHandler" ,fallback = "fallbackHandler")
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("用户{}尝试登录",username);
        if(StringUtils.isEmpty(username)){
            log.error("用户名不能为空");
            throw new UsernameNotFoundException("用户名不能为空");
        }

        //如果redis内没有锁信息 就将用户更新到解锁状态再取用户信息
        String key = Constant.LOCK_KEY + username;
        if(StringUtils.isEmpty(redisStringUtil.getValue(key))){
            Result ret = upmsProvider.unlockUser(username);
            if(ret.isFail()){
                log.warn("锁住用户{}失败",username);
            }
        }

        Result ret = upmsProvider.getFullUserInfo(username);
        if(ret.isFail()){
            throw new UsernameNotFoundException(ret.getMessage());
        }

        UserInfo userInfo = JSONObject.parseObject(JSONObject.toJSONString(ret.getData()),UserInfo.class) ;

        if(!username.equals(userInfo.getUserName())){
            log.error("用户名不存在{}",username);
            throw new UsernameNotFoundException("用户名不存在");
        }

        return new CustomUser(
                username,
                userInfo.getId(),
                userInfo.getPassword(),
                userInfo.getEnabled(),
                userInfo.getAccountNonExpired(),
                userInfo.getCredentialsNonExpired(),
                userInfo.getAccountNonLocked(),
                this.getGrantedAuthorities(userInfo));
    }

    //熔断
    public void fallbackHandler(String userName) throws Exception {
        throw new Exception("系统繁忙,请稍后再试");
    }

    // 限流与阻塞处理
    public void exceptionHandler(String userName, BlockException ex) throws Exception  {
        throw new Exception("请求过频,请稍后再试");
    }

    private Set<GrantedAuthority> getGrantedAuthorities(UserInfo user) {
        Result ret = upmsProvider.getRolesByUserId(user.getId());
        if(ret.isFail()){
            GrantedAuthority guestAuthority = new SimpleGrantedAuthority("guest");
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(guestAuthority);
            return authorities;
        }

        List<RoleInfo> roles = JSONObject.parseArray(JSONObject.toJSONString(ret.getData()),RoleInfo.class) ; //= roleInfoMapper.selectRoleByUserId(user.getId());
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toSet());
    }
}
