/**
 * @Auther: vicliu
 * Date: 2021/2/18 下午6:57
 * @Description:
 */

package indi.vicliu.juaner.jsonapi;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(JSONAPIRegistrar.class)
public @interface EnableJSONAPI {
    String[] value() default {};
    String[] basePackages() default {};
    Class<?>[] basePackageClasses() default {};
    Class<?>[] defaultConfiguration() default {};
}
