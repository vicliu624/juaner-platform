package indi.vicliu.juaner.upms.domain.service.impl;

import indi.vicliu.juaner.upms.domain.entity.TblUserInfo;
import indi.vicliu.juaner.upms.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 14:52
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    public void findByUserName() {
        try{
            TblUserInfo userInfo = userService.getByUsername("superadmin");
            log.info("取到用户-> {}",userInfo.toString());
        } catch (Exception e){
            log.error("测试findByUserName出错",e);
        }
    }
}