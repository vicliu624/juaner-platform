package indi.vicliu.juaner.authorization.provider;

import indi.vicliu.juaner.authorization.provider.fallback.SmsProviderFallback;
import indi.vicliu.juaner.common.core.message.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 该client请求一个非本平台模块的接口 应根据实际需求进行替换
 * 对端去记录username和手机号的对应关系
 */
@Component
@FeignClient(name = "ynyt-sms-service", fallback = SmsProviderFallback.class)
public interface SmsProvider {
	
    @GetMapping(value = "/sms/verify/username/{username}/{code}")
    Result verifyByUsername(@PathVariable("username") String phone, @PathVariable("code") String code);

    @GetMapping(value = "/sms/verifyV2/username/{username}/{code}")
    Result verifyV2ByUsername(@PathVariable("username") String phone, @PathVariable("code") String code);
}
