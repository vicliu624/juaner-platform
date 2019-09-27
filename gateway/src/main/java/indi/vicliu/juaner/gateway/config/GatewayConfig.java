package indi.vicliu.juaner.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-21 17:10
 * @Description:
 */
@Configuration
public class GatewayConfig {

    public static final long DEFAULT_TIMEOUT = 30000;

    public static String RSA_PUBLIC_KEY;

    public static String NACOS_SERVER_ADDR;

    public static String NACOS_NAMESPACE;

    public static String NACOS_ROUTE_DATA_ID;

    public static String NACOS_ROUTE_GROUP;

    public static String NACOS_RATE_LIMIT_DATA_ID;

    public static String NACOS_RATE_LIMIT_GROUP;

    @Value("${jks.key}")
    public void setRsaPublicKey(String rsaPublicKey) {
        RSA_PUBLIC_KEY = rsaPublicKey;
    }

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    public void setNacosServerAddr(String nacosServerAddr){
        NACOS_SERVER_ADDR = nacosServerAddr;
    }

    @Value("${spring.cloud.nacos.discovery.namespace}")
    public void setNacosNamespace(String nacosNamespace){
        NACOS_NAMESPACE = nacosNamespace;
    }

    @Value("${nacos.gateway.route.config.data-id}")
    public void setNacosRouteDataId(String nacosRouteDataId){
        NACOS_ROUTE_DATA_ID = nacosRouteDataId;
    }

    @Value("${nacos.gateway.route.config.group}")
    public void setNacosRouteGroup(String nacosRouteGroup){
        NACOS_ROUTE_GROUP = nacosRouteGroup;
    }

    @Value("${nacos.gateway.limit.config.data-id}")
    public void setNacosRateLimitDataId(String nacosRateLimitDataId){
        NACOS_RATE_LIMIT_DATA_ID = nacosRateLimitDataId;
    }

    @Value("${nacos.gateway.limit.config.group}")
    public void setNacosRateLimitGroup(String nacosRateLimitGroup){
        NACOS_RATE_LIMIT_GROUP = nacosRateLimitGroup;
    }


}
