package indi.vicliu.juaner.gateway.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisStringUtil {

    @Autowired
    private StringRedisTemplate template;


    public String getValue(String key){
        ValueOperations<String, String> ops = template.opsForValue();
        return ops.get(key);
    }
}
