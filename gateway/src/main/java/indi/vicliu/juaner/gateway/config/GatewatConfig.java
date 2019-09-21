package indi.vicliu.juaner.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-21 17:10
 * @Description:
 */
@RefreshScope
@Configuration
public class GatewatConfig {

    public static String RSA_PUBLIC_KEY;

    @Value("${jks.key}")
    public void setRsaPublicKey(String rsaPublicKey) {
        RSA_PUBLIC_KEY = rsaPublicKey;
    }
}
