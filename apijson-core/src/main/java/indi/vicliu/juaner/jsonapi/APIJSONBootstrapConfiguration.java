/**
 * @Auther: vicliu
 * Date: 2021/2/19 下午4:13
 * @Description:
 */

package indi.vicliu.juaner.jsonapi;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "apijson.enabled", matchIfMissing = true)
public class APIJSONBootstrapConfiguration {

}
