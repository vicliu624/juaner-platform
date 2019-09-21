package indi.vicliu.juaner.common.annotation;

import indi.vicliu.juaner.common.config.WebServerMvcConfigurerAdapter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-15 14:09
 * @Description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(WebServerMvcConfigurerAdapter.class)
public @interface EnableLoginArgResolver {
}
