package indi.vicliu.juaner.common.annotation;

import indi.vicliu.juaner.common.config.SentinelAspectConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther: liuweikai
 * @Date: 2019-11-03 11:13
 * @Description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SentinelAspectConfiguration.class)
public @interface EnableSentinelAspect {
}
