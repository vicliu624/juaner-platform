package indi.vicliu.juaner.authorization.security.core.userdetails;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @Auther: liuweikai
 * @Date: 2019-12-18 15:45
 * @Description:
 */
public interface CustomUserDetails extends UserDetails {

    long getUserId();
}
