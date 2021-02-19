/**
 * @Auther: vicliu
 * Date: 2021/2/10 下午3:03
 * @Description: put到JSONAPI中的TABLE_KEY_MAP内,表名映射,对安全要求很高的表可以这么做
 */

package indi.vicliu.juaner.jsonapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Data
@ConfigurationProperties(prefix = "jsonapi")
public class JSONAPIProperties {

    private Datasource datasource;

    @Autowired
    @JsonIgnore
    private Environment environment;

    @PostConstruct
    public void init() {
        this.overrideFromEnv();
    }

    private void overrideFromEnv() {
        if (datasource == null) {
            datasource = new Datasource();
            datasource.setType(environment
                    .resolvePlaceholders("${jsonapi.datasource.type:}"));
            datasource.setVersion(environment
                    .resolvePlaceholders("${jsonapi.datasource.version:}"));
            datasource.setSchema(environment
                    .resolvePlaceholders("${jsonapi.datasource.schema:}"));
            datasource.setUrl(environment
                    .resolvePlaceholders("${jsonapi.datasource.url:}"));
            datasource.setAccount(environment
                    .resolvePlaceholders("${jsonapi.datasource.account:}"));
            datasource.setPassword(environment
                    .resolvePlaceholders("${jsonapi.datasource.password:}"));
        }
    }
}
