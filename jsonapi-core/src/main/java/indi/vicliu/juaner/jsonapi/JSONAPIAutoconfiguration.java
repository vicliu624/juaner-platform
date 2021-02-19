/**
 * @Auther: vicliu
 * Date: 2021/2/10 上午11:49
 * @Description:
 */

package indi.vicliu.juaner.jsonapi;

import apijson.framework.APIJSONApplication;
import apijson.framework.APIJSONCreator;
import apijson.orm.SQLConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//设置成false 启动的时候不进入IOC容器
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ JSONAPIProperties.class })
@ConditionalOnProperty(name = "apijson.enabled", matchIfMissing = true)
@AutoConfigureAfter(JSONAPIBootstrapConfiguration.class)
public class JSONAPIAutoconfiguration {
    private final JSONAPIProperties properties;

    public JSONAPIAutoconfiguration(JSONAPIProperties properties) {
        this.properties = properties;

        APIJSONApplication.DEFAULT_APIJSON_CREATOR = new APIJSONCreator(){
            @Override
            public SQLConfig createSQLConfig() {
                return new JuanerSQLConfig(properties);
            }

        };

    }
}
