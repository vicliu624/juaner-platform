package indi.vicliu.juaner.upms.controller;

import indi.vicliu.juaner.common.core.exception.ErrorType;
import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import indi.vicliu.juaner.upms.domain.service.UserService;
import indi.vicliu.juaner.upms.exception.UserException;
import indi.vicliu.juaner.upms.vo.AddUserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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

    /**
     * 创建用户
     * @param userInfo
     * @return
     */
    @RequestMapping("/user/addUser")
    public Result addUserInfo(@RequestBody @Validated AddUserInfoVO userInfo){
        try {
            return userService.addUserInfo(userInfo);
        } catch (Exception e) {
            if(e instanceof UserException){
                return Result.fail(e.getMessage());
            }else {
                return Result.fail(ErrorType.SYSTEM_ERROR,"创建用户角色失败");
            }

        }
    }

    /**
     * 修改用户信息
     * @param info
     * @return
     */
    @RequestMapping("/user/updateUser")
    public Result updateUserInfo(@RequestBody TblUserInfo info){
        return userService.updateUserInfo(info);
    }

    /**
     * 根据手机号码查询用户信息
     * @param phone
     * @return
     */
    @GetMapping("/user/findByUserPhone")
    public Result findByUserPhone(@RequestParam String phone){
        return userService.findByUserPhone(phone);
    }

    /**
     * 根据手机号码查询用户信息
     * @param userId
     * @return
     */
    @GetMapping("/user/getByUserId")
    public Result findByUserPhone(@RequestParam Long userId){
        return userService.findByUserId(userId);
    }
}
