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

    @Scheduled(fixedDelay = 1000 * 60 * 2)
    public void roleAndPermission() {
        try{
            roleService.allRoles().forEach( role -> {
                permissionService.updateRolePermissionCache(role.getRoleName());
            });
        } catch (Exception e){
            log.error("定时器出错",e);
        }
    }


    @Scheduled(fixedDelay = 1000 * 2)
    public void a() {
        try{
            System.out.println("----a exec---");
            Thread.sleep(1000 * 60 * 10);
        } catch (Exception e){
            log.error("定时器出错",e);
        }
    }

    @Scheduled(fixedDelay = 1000 * 2)
    public void b() {
        try{
            System.out.println("----b exec---");
        } catch (Exception e){
            log.error("定时器出错",e);
        }
    }

    @Scheduled(fixedRate = 1000 * 2)
    public void c() {
        try{
            System.out.println("----c exec---");
        } catch (Exception e){
            log.error("定时器出错",e);
        }
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void d() {
        try{
            System.out.println("----d exec---");
        } catch (Exception e){
            log.error("定时器出错",e);
        }
    }
}
