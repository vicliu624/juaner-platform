package indi.vicliu.juaner.gateway.filter;

import indi.vicliu.juaner.gateway.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Slf4j
@Component
public class CacheRequestBodyFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
        log.info("AccessGatewayFilter 当前访问路径为 {}",requestUrl.toString());
        if (requestUrl.toString().indexOf("ws/endpoint") > -1) {
            return chain.filter(exchange);
        }

        // 将 request body 中的内容 copy 一份，记录到 exchange 的一个自定义属性中
        Object cachedRequestBodyObject = exchange.getAttributeOrDefault(Constant.REQUEST_BODY_OBJECT, null);
        // 如果已经缓存过，略过
        if (cachedRequestBodyObject != null) {
            return chain.filter(exchange);
        }
        // 如果没有缓存过，获取字节数组存入 exchange 的自定义属性中
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                }).defaultIfEmpty(new byte[0])
                .doOnNext(bytes -> exchange.getAttributes().put(Constant.REQUEST_BODY_OBJECT, bytes))
                .then(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return 10002;
    }
}
