package indi.vicliu.juaner.gateway.model;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Auther: liuweikai
 * @Date: 2019-03-27 10:27
 * @Description:
 */
@Data
public class GatewayPredicateDefinition {
    /**
     * 断言对应的Name
     */
    private String name;

    /**
     * 配置的断言规则
     */
    private Map<String, String> args = new LinkedHashMap<>();
}
