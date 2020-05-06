package indi.vicliu.juaner.authorization.annotation;

import indi.vicliu.juaner.authorization.annotation.handler.ControllerExceptionHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther: liuweikai
 * @Date: 2020-04-21 16:05
 * @Description:
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ControllerExceptionHandler.class)
public @interface EnableArgExceptionHandler {
}
