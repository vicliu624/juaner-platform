package indi.vicliu.juaner.upms.client.fallback;

import indi.vicliu.juaner.upms.client.IdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IdProviderFallback implements IdProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdProviderFallback.class);

    @Override
    public long nextId() {
        LOGGER.error("genId的熔断被触发");
        return -1;
    }
}
