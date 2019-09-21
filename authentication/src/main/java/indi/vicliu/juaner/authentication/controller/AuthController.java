package indi.vicliu.juaner.authentication.controller;

import indi.vicliu.juaner.authentication.domian.service.AuthenticationService;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/permission")
    public Result decide(@RequestParam String url, @RequestParam String method, HttpServletRequest request){
        boolean decide = authenticationService.decide(new HttpServletRequestAuthWrapper(request, url, method.toUpperCase()));
        return Result.success(decide);
    }
}
