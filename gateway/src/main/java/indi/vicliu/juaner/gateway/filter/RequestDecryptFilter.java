package indi.vicliu.juaner.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.common.core.exception.BaseException;
import indi.vicliu.juaner.common.core.exception.ErrorType;
import indi.vicliu.juaner.gateway.data.mapper.TblCryptoInfoMapper;
import indi.vicliu.juaner.gateway.domain.entity.TblCryptoInfo;
import indi.vicliu.juaner.gateway.utils.AESUtil;
import indi.vicliu.juaner.gateway.utils.Constant;
import indi.vicliu.juaner.gateway.utils.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class RequestDecryptFilter implements GlobalFilter, Ordered {

    @Autowired
    private TblCryptoInfoMapper cryptoInfoMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        if (request.getMethod() != HttpMethod.POST && request.getMethod() != HttpMethod.PUT && request.getMethod() != HttpMethod.PATCH) {
            // 如果不是post（新增）、put（全量修改）、patch（部分字段修改）操作，则直接放行
            return chain.filter(exchange);
        }

        // 设置是否加密标识
        String cryptoAlgorithm = exchange.getRequest().getHeaders().getFirst("Crypto-Algorithm");
        if(!StringUtils.isEmpty(cryptoAlgorithm) && cryptoAlgorithm.equalsIgnoreCase("RSA")){
            // 尝试从 exchange 的自定义属性中取出缓存到的 body
            Object cachedRequestBodyObject = exchange.getAttributeOrDefault(Constant.REQUEST_BODY_OBJECT, null);
            byte[] decrypBytes;
            try {
                byte[] body = (byte[]) cachedRequestBodyObject;
                String rootData = new String(body); // 客户端传过来的数据
                JSONObject jsonObject = JSONObject.parseObject(rootData);
                int algoIndex = (Integer)jsonObject.get("i");
                TblCryptoInfo primaryKey = new TblCryptoInfo();
                primaryKey.setRequester((long)algoIndex);
                primaryKey.setCryptoAlgorithm(cryptoAlgorithm);
                TblCryptoInfo cryptoInfo = cryptoInfoMapper.selectByPrimaryKey(primaryKey);
                if(cryptoInfo == null){
                    log.error("查询不到密钥索引{}",algoIndex);
                    throw new BaseException(ErrorType.DECRYPT_ERROR);
                }
                String encryptKey = (String) jsonObject.get("k");
                String encryptData = (String) jsonObject.get("v");
                //解密出用于aes解密的密钥
                byte[] key = RSAUtil.decrypt(encryptKey,cryptoInfo.getPrivateKey());
                log.debug("AES Key:{}",new String(key));
                decrypBytes = AESUtil.AESDecrypt(encryptData, key, "ECB");
                if(decrypBytes == null){
                    throw new BaseException(ErrorType.DECRYPT_ERROR);
                }
            } catch (Exception e){
                log.error("客户端数据解析异常", e);
                throw new BaseException(ErrorType.DECRYPT_ERROR);
            }

            // 根据解密后的参数重新构建请求
            DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
            Flux<DataBuffer> bodyFlux = Flux.just(dataBufferFactory.wrap(decrypBytes));
            ServerHttpRequest newRequest = request.mutate().uri(uri).build();
            newRequest = new ServerHttpRequestDecorator(newRequest) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return bodyFlux;
                }
            };

            // 构建新的请求头
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            // 由于修改了传递参数，需要重新设置CONTENT_LENGTH，长度是字节长度，不是字符串长度
            int length = decrypBytes.length;
            headers.remove(HttpHeaders.CONTENT_LENGTH);
            headers.setContentLength(length);
            // headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
            newRequest = new ServerHttpRequestDecorator(newRequest) {
                @Override
                public HttpHeaders getHeaders() {
                    return headers;
                }
            };

            // 把解密后的数据重置到exchange自定义属性中,在之后的日志GlobalLogFilter从此处获取请求参数打印日志
            exchange.getAttributes().put(Constant.REQUEST_BODY_OBJECT, decrypBytes);
            return chain.filter(exchange.mutate().request(newRequest).build());
        } else {
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return 10003;
    }
}
