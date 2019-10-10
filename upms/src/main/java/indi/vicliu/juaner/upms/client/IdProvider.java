package indi.vicliu.juaner.upms.client;

import indi.vicliu.juaner.upms.client.fallback.IdProviderFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@FeignClient(name = "juaner-id", fallback = IdProviderFallback.class)
public interface IdProvider {

    @GetMapping(value = "/id/next")
    long nextId();
}
