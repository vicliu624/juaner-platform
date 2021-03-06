package indi.vicliu.juaner.gateway.filter;

import indi.vicliu.juaner.common.custom.http.CustomHttpHeaders;
import indi.vicliu.juaner.gateway.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @Auther: liuweikai
 * @Date: 2021-01-04 14:56
 * @Description: 请求体缓存过滤器
 */
@Slf4j
@Component
public class CacheRequestBodyFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
        log.info("CacheRequestBodyFilter 当前访问路径为 {}",requestUrl.toString());
        if (requestUrl.toString().indexOf("ws/endpoint") > -1) {
            return chain.filter(exchange);
        }

        String cryptoAlgorithm = exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.CRYPTO_ALGORITHM);
        if(StringUtils.isEmpty(cryptoAlgorithm)){
            return chain.filter(exchange);
        }

        String contentType = exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.CONTENT_TYPE);
        if(contentType.contains(MediaType.APPLICATION_JSON_VALUE)
                || contentType.contains(MediaType.APPLICATION_JSON_UTF8_VALUE)
                || contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                || contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
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
        } else {
            log.info("暂不处理:{}", contentType);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 10002;
    }
}
