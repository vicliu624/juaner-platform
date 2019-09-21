package indi.vicliu.juaner.authentication.domian.service;


import javax.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    /**
     * 校验权限
     *
     * @param authRequest
     * @return 是否有权限
     */
    boolean decide(HttpServletRequest authRequest);
}
