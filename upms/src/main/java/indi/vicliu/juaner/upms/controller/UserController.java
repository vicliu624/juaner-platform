package indi.vicliu.juaner.upms.controller;

import indi.vicliu.juaner.common.core.exception.ErrorType;
import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import indi.vicliu.juaner.upms.domain.service.UserService;
import indi.vicliu.juaner.upms.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 13:27
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/upms")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/fullInfo")
    public Result getFullUserInfoByUserName(@RequestParam String userName){
        try{
            TblUserInfo userInfo = this.userService.findByUserName(userName);
            return Result.success(userInfo);
        } catch (Exception e){
            log.error(" getFullUserInfoByUserName fail ",e);
            if(e instanceof UserException){
                return Result.fail(e.getMessage());
            } else {
                return Result.fail(ErrorType.SYSTEM_ERROR,"查询用户信息时出错");
            }
        }
    }
}
