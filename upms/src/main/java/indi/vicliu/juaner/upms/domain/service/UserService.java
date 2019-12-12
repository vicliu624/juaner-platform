package indi.vicliu.juaner.upms.domain.service;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import indi.vicliu.juaner.upms.exception.UserException;
import indi.vicliu.juaner.upms.vo.AddUserInfoVO;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 13:15
 * @Description:
 */
public interface UserService {
    TblUserInfo findByUserName(String userName) throws UserException;
    TblUserInfo findByPhone(String phone) throws UserException;

    Result addUserInfo(AddUserInfoVO userInfo) throws UserException;

    Result updateUserInfo(AddUserInfoVO user);

    Result findByUserPhone(String phone);

    Result findByUserId(Long userId);

    Result getByUsername( String username) throws UserException;

    void updateUserInfoCache(String userName) throws UserException;

    Result createUserInfo(AddUserInfoVO userInfo);
    Result delUserInfo(String ids);

    int lockUserByName(String userName) throws UserException;
}
