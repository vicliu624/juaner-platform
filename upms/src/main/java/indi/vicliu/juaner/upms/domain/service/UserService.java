package indi.vicliu.juaner.upms.domain.service;

import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import indi.vicliu.juaner.upms.exception.UserException;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 13:15
 * @Description:
 */
public interface UserService {
    TblUserInfo findByUserName(String userName) throws UserException;
    TblUserInfo findByPhone(String phone) throws UserException;
}
