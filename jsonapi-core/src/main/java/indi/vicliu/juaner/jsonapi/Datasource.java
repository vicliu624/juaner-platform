/**
 * @Auther: vicliu
 * Date: 2021/2/10 下午3:44
 * @Description:
 */

package indi.vicliu.juaner.jsonapi;

import lombok.Data;

import java.util.Map;

@Data
public class Datasource {
    private Map<String,String> tables;
    private String version;
    private String url;
    private String account;
    private String password;
    private String schema;
    private String type;
}
