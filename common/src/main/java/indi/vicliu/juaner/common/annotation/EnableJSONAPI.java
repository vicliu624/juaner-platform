package indi.vicliu.juaner.common.annotation;

import indi.vicliu.juaner.common.config.jsonapi.JSONAPIAutoconfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther: liuweikai
 * @Date: 2021-02-10 11:44
 * @Description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(JSONAPIAutoconfiguration.class)
public @interface EnableJSONAPI {
}
