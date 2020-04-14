package indi.vicliu.juaner.upms.domain.service;

import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import indi.vicliu.juaner.upms.exception.UserException;
import indi.vicliu.juaner.upms.vo.AddUserInfoVO;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 13:15
 * @Description:
 */
public interface UserService {
    TblUserInfo findByPhone(String phone) throws UserException;

    TblUserInfo addUserInfo(AddUserInfoVO userInfo) throws UserException;

    TblUserInfo updateUserInfo(AddUserInfoVO user) throws UserException;

    int updateUserInfo(TblUserInfo user);

    TblUserInfo findByUserPhone(String phone);

    TblUserInfo findByUserId(Long userId);

    TblUserInfo getByUsername( String username) throws UserException;

    TblUserInfo updateUserInfoCache(String userName) throws UserException;

    TblUserInfo createUserInfo(AddUserInfoVO userInfo) throws UserException;
    void delUserInfo(String ids);

    int lockUserByName(String userName) throws UserException;

    int unlockUserByName(String userName) throws UserException;
}
