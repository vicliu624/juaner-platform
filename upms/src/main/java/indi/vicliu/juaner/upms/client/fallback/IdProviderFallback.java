package indi.vicliu.juaner.upms.client.fallback;

import indi.vicliu.juaner.upms.client.IdProvider;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IdProviderFallback implements IdProvider {

    @Override
    public long nextId() {
        log.error("genId的熔断被触发");
        return -1;
    }
}
