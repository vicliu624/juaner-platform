package indi.vicliu.juaner.upms.task;

import indi.vicliu.juaner.upms.domain.service.PermissionService;
import indi.vicliu.juaner.upms.domain.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SyncRedisTask {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Scheduled(fixedDelay = 1000 * 10)
    public void roleAndPermission() {
        try{
            roleService.allRoles().forEach( role -> {
                permissionService.updateRolePermissionCache(role.getRoleName());
            });
        } catch (Exception e){
            log.error("定时器出错",e);
        }
    }
}
