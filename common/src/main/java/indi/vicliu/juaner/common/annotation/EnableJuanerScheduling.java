/**
 * @Auther: vicliu
 * Date: 2021/2/18 下午5:02
 * @Description:
 */

package indi.vicliu.juaner.common.annotation;

import indi.vicliu.juaner.common.scheduling.annotation.SchedulingConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SchedulingConfiguration.class)
@Documented
public @interface EnableJuanerScheduling {
}
