package indi.vicliu.juaner.gateway.filter;

import indi.vicliu.juaner.common.core.exception.BaseException;
import indi.vicliu.juaner.common.core.exception.ErrorType;
import indi.vicliu.juaner.common.custom.http.CustomHttpHeaders;
import indi.vicliu.juaner.gateway.data.mapper.TblCryptoInfoMapper;
import indi.vicliu.juaner.gateway.domain.entity.TblCryptoInfo;
import indi.vicliu.juaner.gateway.utils.AESUtil;
import indi.vicliu.juaner.gateway.utils.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class ResponseBodyEncryptFilter implements GlobalFilter, Ordered {

    @Autowired
    private TblCryptoInfoMapper cryptoInfoMapper;



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String cryptoAlgorithm = exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.CRYPTO_ALGORITHM);
        if (StringUtils.isEmpty(cryptoAlgorithm)) {
            return chain.filter(exchange);
        }

        if (!cryptoAlgorithm.equalsIgnoreCase("RSA")) {
            return chain.filter(exchange);
        }

        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            if (exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.ALGORITHM_INDEX) == null) {
                throw new BaseException(ErrorType.DECRYPT_ERROR);
            }
            int algoIndex = Integer.parseInt(exchange.getRequest().getHeaders().getFirst("Algorithm-Index"));

            if (exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.CRYPTO_KEY) == null) {
                throw new BaseException(ErrorType.DECRYPT_ERROR);
            }
            String encryptKey = exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.CRYPTO_KEY);

            TblCryptoInfo primaryKey = new TblCryptoInfo();
            primaryKey.setRequester((long) algoIndex);
            primaryKey.setCryptoAlgorithm(cryptoAlgorithm);
            TblCryptoInfo cryptoInfo = cryptoInfoMapper.selectByPrimaryKey(primaryKey);
            if (cryptoInfo == null) {
                log.error("查询不到密钥索引{}", algoIndex);
                throw new BaseException(ErrorType.DECRYPT_ERROR);
            }

            byte[] key = RSAUtil.decrypt(encryptKey, cryptoInfo.getPrivateKey());
            log.debug("AES Key:{}", new String(key));

            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer join = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[join.readableByteCount()];
                            join.read(content);
                            // 释放掉内存
                            DataBufferUtils.release(join);
                            byte[] encodedBytes;
                            try {
                                encodedBytes = AESUtil.AESEncrypt(content, key, "ECB");
                            } catch (Exception e) {
                                log.error("加密出错", e);
                                throw new BaseException(ErrorType.DECRYPT_ERROR);
                            }
                            return bufferFactory.wrap(Base64.getEncoder().encodeToString(encodedBytes).getBytes(StandardCharsets.UTF_8));
                        }));
                    }
                    return super.writeWith(body);
                }
            };
            exchange.getResponse().getHeaders().set(CustomHttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
            exchange.getResponse().getHeaders().add(CustomHttpHeaders.ENCRYPTED,"true");
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        } catch (Exception e){
            log.error("加密应答数据异常", e);
            throw new BaseException(ErrorType.DECRYPT_ERROR);
        }
    }

    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }
}
