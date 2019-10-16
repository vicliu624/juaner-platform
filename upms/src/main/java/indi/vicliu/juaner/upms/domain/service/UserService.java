package indi.vicliu.juaner.upms.domain.service;

import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import indi.vicliu.juaner.upms.exception.UserException;
import indi.vicliu.juaner.upms.vo.AddUserInfoVO;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 13:15
 * @Description:
 */
public interface UserService {
    TblUserInfo findByUserName(String userName) throws UserException;

    Result addUserInfo(AddUserInfoVO userInfo) throws UserException;

    Result updateUserInfo(TblUserInfo user);

    Result findByUserPhone(String phone);

    Result findByUserId(Long userId);
}
