package indi.vicliu.juaner.upms.controller;

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.common.core.exception.ErrorType;
import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.common.core.util.UserContextHolder;
import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import indi.vicliu.juaner.upms.domain.service.UserService;
import indi.vicliu.juaner.upms.exception.UserException;
import indi.vicliu.juaner.upms.vo.AddUserInfoVO;
import indi.vicliu.juaner.upms.vo.BindWeChatVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

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
            TblUserInfo userInfo = this.userService.getByUsername(userName);
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

    @GetMapping("/user/getByUsername")
    public Result getByUsername(@RequestParam("username") String username){
        try {
            TblUserInfo userInfo = userService.getByUsername(username);
            userInfo.setPassword(null);
            return Result.success(userInfo);
        }catch (Exception e){
            log.error(" getByUsername fail ",e);
            if(e instanceof UserException){
                return Result.fail(e.getMessage());
            } else {
                return Result.fail(ErrorType.SYSTEM_ERROR,"查询用户信息时出错");
            }
        }
    }

    @GetMapping("/user/fullInfo/byPhone")
    public Result getFullUserInfoByPhone(@RequestParam String phone) {
        try{
            TblUserInfo userInfo = this.userService.findByPhone(phone);
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
    @PostMapping("/user/addUser")
    public Result addUserInfo(@RequestBody @Validated AddUserInfoVO userInfo){
        try {
            return Result.success(userService.addUserInfo(userInfo));
        } catch (Exception e) {
            log.info("创建用户异常：{}",e);
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
    @PutMapping("/user/updateUser")
    public Result updateUserInfo(@RequestBody AddUserInfoVO info){
        try{
            userService.updateUserInfo(info);
            return Result.success();
        } catch (Exception e){
            log.error("更新用户信息出错",e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 根据手机号码查询用户信息
     * @param phone
     * @return
     */
    @GetMapping("/user/findByUserPhone")
    public Result findByUserPhone(@RequestParam String phone){
        TblUserInfo userInfo = userService.findByUserPhone(phone);
        if(userInfo != null){
            return Result.success(userInfo);
        } else {
            return Result.success("手机号未绑定过任何用户");
        }
    }

    /**
     * 根据手机号码查询用户信息
     * @param userId
     * @return
     */
    @GetMapping("/user/getByUserId")
    public Result findByUserId(@RequestParam Long userId){
        TblUserInfo userInfo = userService.findByUserId(userId);
        if(userInfo != null){
            return Result.success(userInfo);
        } else {
            return Result.success("查询不到用户");
        }
    }

    /**
     * 创建用户不录入角色
     * @param userInfo
     * @return
     */
    @PostMapping("/user/createUserInfo")
    public Result createUserInfo(@RequestBody AddUserInfoVO userInfo){
        try{
            TblUserInfo userInfo1 = userService.createUserInfo(userInfo);
            return Result.success(userInfo1);
        } catch (Exception e){
            log.error("创建用户失败",e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 管理端删除用户
     * @param ids
     * @return
     */
    @GetMapping("/user/delUserInfo")
    public Result delUserInfo(@RequestParam("ids") String ids){
        try {
            userService.delUserInfo(ids);
            return Result.success("删除用户成功");
        } catch (Exception e) {
            log.error("删除用户方法removeUserInfo出错", e);
            return Result.fail("删除用户失败");
        }
    }

    @PutMapping("/user/lock")
    public Result lockUser(@RequestBody String userName){
        try{
            int count = userService.lockUserByName(userName);
            if(count > 0){
                return Result.success("用户被锁定");
            } else {
                return Result.success("用户已锁定");
            }
        } catch (Exception e){
            log.error("锁定用户出错",e);
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/user/unlock")
    public Result unlockUser(@RequestBody String userName){
        try{
            int count = userService.unlockUserByName(userName);
            if(count > 0){
                return Result.success("用户解锁成功");
            } else {
                return Result.success("用户已解锁");
            }
        } catch (Exception e){
            log.error("锁定用户出错",e);
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/user/bindPhone")
    public Result bindPhone(@RequestBody String phone) {
        String userName = UserContextHolder.getInstance().getUsername();
        TblUserInfo userInfo = null;
        try{
            userInfo = this.userService.getByUsername(userName);
        } catch (UserException e){
            log.error("绑定手机号出错",e);
            return Result.fail(e.getMessage());
        }

        userInfo.setPhone(phone);
        this.userService.updateUserInfo(userInfo);
        return Result.success();
    }

    @PostMapping("/user/bindWeChat")
    public Result bindUserFromWeChat(@RequestBody BindWeChatVo weChatVo){
        try{
            log.info("request data:{}", JSONObject.toJSONString(weChatVo));
            return Result.success(this.userService.queryByWeChatUnionId(weChatVo.getUnionid(),weChatVo.getOpenid()));
        } catch (Exception e){
            log.error("绑定微信账号出错",e);
            return Result.fail("绑定微信账号出错" + e.getMessage());
        }
    }
}
