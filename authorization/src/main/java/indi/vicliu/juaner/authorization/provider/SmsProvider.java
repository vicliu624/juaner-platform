package indi.vicliu.juaner.authorization.provider;

import indi.vicliu.juaner.authorization.provider.fallback.SmsProviderFallback;
import indi.vicliu.juaner.common.core.message.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "ynyt-sms-service", fallback = SmsProviderFallback.class)
public interface SmsProvider {
	
    @GetMapping(value = "/sms/verify/{phone}/{code}")
    Result verify(@PathVariable("phone") String phone, @PathVariable("code") String code);
}
