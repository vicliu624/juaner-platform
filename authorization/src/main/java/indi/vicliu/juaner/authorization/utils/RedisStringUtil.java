package indi.vicliu.juaner.authorization.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisStringUtil {

    @Autowired
    private StringRedisTemplate template;

    public void setKey(String key,String value){
        ValueOperations<String, String> ops = template.opsForValue();
        ops.set(key,value);
    }

    public void setKeyExpire(String key, String value, int time, TimeUnit unit) {
        ValueOperations<String, String> ops = template.opsForValue();
        ops.set(key, value,time,unit);
    }

    public String getValue(String key){
        ValueOperations<String, String> ops = template.opsForValue();
        return ops.get(key);
    }

    public void delKey(String key){
        template.delete(key);
    }
}
