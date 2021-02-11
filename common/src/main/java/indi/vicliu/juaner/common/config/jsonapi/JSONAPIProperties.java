/**
 * @Auther: vicliu
 * Date: 2021/2/10 下午3:03
 * @Description: put到JSONAPI中的TABLE_KEY_MAP内,表名映射,对安全要求很高的表可以这么做
 */

package indi.vicliu.juaner.common.config.jsonapi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jsonapi")
public class JSONAPIProperties {

    private Datasource datasource;
}
