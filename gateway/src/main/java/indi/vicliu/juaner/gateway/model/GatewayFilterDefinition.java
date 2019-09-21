package indi.vicliu.juaner.gateway.model;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Auther: liuweikai
 * @Date: 2019-03-27 10:26
 * @Description:
 */
@Data
public class GatewayFilterDefinition {
    /**
     * Filter Name
     */
    private String name;

    /**
     * 对应的路由规则
     */
    private Map<String, String> args = new LinkedHashMap<>();
}
