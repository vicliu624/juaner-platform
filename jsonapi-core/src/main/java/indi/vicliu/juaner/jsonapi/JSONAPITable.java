/**
 * @Auther: vicliu
 * Date: 2021/2/15 下午2:12
 * @Description:
 */

package indi.vicliu.juaner.jsonapi;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface JSONAPITable {
    /**
     * 数据库内正式的表名称
     * @return
     */
    String realTableName() default "";
}
