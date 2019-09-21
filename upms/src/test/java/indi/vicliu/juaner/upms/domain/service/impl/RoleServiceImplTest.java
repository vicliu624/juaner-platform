package indi.vicliu.juaner.upms.domain.service.impl;

import indi.vicliu.juaner.upms.domain.entity.TblRoleInfo;
import indi.vicliu.juaner.upms.domain.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 15:19
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
public class RoleServiceImplTest {

    @Autowired
    private RoleService roleService;

    @Test
    public void queryRolesByUserId() {
        try{
            List<TblRoleInfo> roleInfoList = roleService.queryRolesByUserId(1L);
            log.info("查询到记录 {}",roleInfoList.toString());
        } catch (Exception e){
            log.error("测试queryRolesByUserId时出错",e);
        }

    }
}