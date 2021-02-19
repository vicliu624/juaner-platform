/**
 * @Auther: vicliu
 * Date: 2021/2/10 下午4:02
 * @Description:
 */

package indi.vicliu.juaner.jsonapi;

import apijson.framework.APIJSONSQLConfig;
import com.alibaba.fastjson.annotation.JSONField;

public class JuanerSQLConfig extends APIJSONSQLConfig {

    private JSONAPIProperties properties;

    public JuanerSQLConfig(JSONAPIProperties properties){
        this.properties = properties;
    }

    @Override
    public String getDBVersion() {
        return this.properties.getDatasource().getVersion();  // "8.0.11";  // TODO 改成你自己的 MySQL 或 PostgreSQL 数据库版本号  // MYSQL 8 和 7 使用的 JDBC 配置不一样
    }

    @JSONField(serialize = false)  // 不在日志打印 账号/密码 等敏感信息
    @Override
    public String getDBUri() {
        return this.properties.getDatasource().getUrl(); // TODO 改成你自己的，TiDB 可以当成 MySQL 使用，默认端口为 4000
    }

    @JSONField(serialize = false)  // 不在日志打印 账号/密码 等敏感信息
    @Override
    public String getDBAccount() {
        return this.properties.getDatasource().getAccount();  // TODO 改成你自己的
    }

    @JSONField(serialize = false)  // 不在日志打印 账号/密码 等敏感信息
    @Override
    public String getDBPassword() {
        return this.properties.getDatasource().getPassword();  // TODO 改成你自己的，TiDB 可以当成 MySQL 使用， 默认密码为空字符串 ""
    }
}
