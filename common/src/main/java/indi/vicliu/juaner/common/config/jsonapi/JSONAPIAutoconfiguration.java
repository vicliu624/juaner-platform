/**
 * @Auther: vicliu
 * Date: 2021/2/10 上午11:49
 * @Description:
 */

package indi.vicliu.juaner.common.config.jsonapi;

import apijson.framework.APIJSONApplication;
import apijson.framework.APIJSONCreator;
import apijson.orm.SQLConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//设置成false 启动的时候不进入IOC容器
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ APIJSONApplication.class, WebMvcConfigurer.class })
@EnableConfigurationProperties(JSONAPIProperties.class)
@AutoConfigureAfter(RepositoryRestMvcAutoConfiguration.class)
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

        APIJSONApplication.DEFAULT_APIJSON_CREATOR.createSQLConfig()
    }
}
