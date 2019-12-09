package indi.vicliu.juaner.authorization.provider.fallback;

import indi.vicliu.juaner.authorization.provider.SmsProvider;
import indi.vicliu.juaner.common.core.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsProviderFallback implements SmsProvider {
	
	@Override
	public Result verifyByUsername(String username, String code) {
		log.error("verify的熔断被触发");
        return Result.fail("确认短信验证码时出错");
	}
}
