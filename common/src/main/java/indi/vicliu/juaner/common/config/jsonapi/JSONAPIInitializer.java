/**
 * @Auther: vicliu
 * Date: 2021/2/10 下午3:21
 * @Description:
 */

package indi.vicliu.juaner.common.config.jsonapi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class JSONAPIInitializer {
    private static final Log logger = LogFactory.getLog(JSONAPIInitializer.class);

    private final DataSourceProperties properties;

    public JSONAPIInitializer(DataSourceProperties properties) {
        this.properties = properties;
    }
}
