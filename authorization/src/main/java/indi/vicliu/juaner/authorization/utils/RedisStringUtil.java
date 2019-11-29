package indi.vicliu.juaner.authorization.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisStringUtil {

    @Autowired
    private StringRedisTemplate template;

    public void setKey(String key,String value){
        ValueOperations<String, String> ops = template.opsForValue();
        ops.set(key,value);
    }
}
