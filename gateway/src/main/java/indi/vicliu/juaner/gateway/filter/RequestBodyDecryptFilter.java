package indi.vicliu.juaner.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.common.core.exception.BaseException;   
import indi.vicliu.juaner.common.core.exception.ErrorType;
import indi.vicliu.juaner.common.custom.http.CustomHttpHeaders;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @Auther: liuweikai
 * @Date: 2021-01-04 15:56
 * @Description: 请求体解密过滤器
 */
@Slf4j
@Component
public class RequestBodyDecryptFilter implements GlobalFilter, Ordered {

    @Autowired
    private TblCryptoInfoMapper cryptoInfoMapper;

    private static final String PARAM_DEFINE = "=";

    private static final String PARAM_TOKENIZER = "&";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
        log.info("RequestBodyDecryptFilter 当前访问路径为 {}",requestUrl.toString());
        if (requestUrl.toString().indexOf("ws/endpoint") > -1) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        if (request.getMethod() != HttpMethod.POST && request.getMethod() != HttpMethod.PUT && request.getMethod() != HttpMethod.PATCH) {
            // 如果不是post（新增）、put（全量修改）、patch（部分字段修改）操作，则直接放行
            return chain.filter(exchange);
        }

        String cryptoAlgorithm = exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.CRYPTO_ALGORITHM);
        if(StringUtils.isEmpty(cryptoAlgorithm)){
            return chain.filter(exchange);
        }

        try {
            if (cryptoAlgorithm.equalsIgnoreCase("RSA")){
                String contentType = exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.CONTENT_TYPE);
                if(exchange.getRequest().getHeaders().getFirst("Algorithm-Index") == null){
                    throw new BaseException(ErrorType.DECRYPT_ERROR);
                }
                String index = exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.ALGORITHM_INDEX);
                if(index == null){
                    throw new BaseException(ErrorType.DECRYPT_ERROR);
                }
                int algoIndex = Integer.parseInt(index);
                String encryptKey = exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.CRYPTO_KEY);
                if(encryptKey == null){
                    throw new BaseException(ErrorType.DECRYPT_ERROR);
                }
                TblCryptoInfo primaryKey = new TblCryptoInfo();
                primaryKey.setRequester((long)algoIndex);
                primaryKey.setCryptoAlgorithm(cryptoAlgorithm);
                TblCryptoInfo cryptoInfo = cryptoInfoMapper.selectByPrimaryKey(primaryKey);
                if(cryptoInfo == null) {
                    log.error("查询不到密钥索引{}",algoIndex);
                    throw new BaseException(ErrorType.DECRYPT_ERROR);
                }

                byte[] key = RSAUtil.decrypt(encryptKey,cryptoInfo.getPrivateKey());
                log.debug("AES Key:{}",new String(key));
                byte[] decryptBytes;
                if(contentType.contains(MediaType.APPLICATION_JSON_VALUE)){
                    Object cachedRequestBodyObject = exchange.getAttributeOrDefault(Constant.REQUEST_BODY_OBJECT, null);
                    byte[] body = (byte[]) cachedRequestBodyObject;
                    String rootData = new String(body);
                    JSONObject jsonObject = JSONObject.parseObject(rootData);
                    String encryptData = (String) jsonObject.get("v");
                    decryptBytes = AESUtil.AESDecrypt(encryptData, key, "ECB");
                    if(decryptBytes == null){
                        throw new BaseException(ErrorType.DECRYPT_ERROR);
                    }
                } else if (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                    Object cachedRequestBodyObject = exchange.getAttributeOrDefault(Constant.REQUEST_BODY_OBJECT, null);
                    byte[] body = (byte[]) cachedRequestBodyObject;
                    String rootData = new String(body);
                    log.info("data:{}",rootData);
                    Map<String, String> params = toMap(rootData);
                    Map<String,String> newParams = new HashMap<>();
                    log.info("params:{}",params);
                    params.forEach((k, v) -> {
                        try {
                            byte[] srcKey = AESUtil.AESDecrypt(URLDecoder.decode(k,StandardCharsets.UTF_8.name()), key, "ECB");
                            if(srcKey == null){
                                throw new BaseException(ErrorType.DECRYPT_ERROR);
                            }
                            byte[] srcValue = AESUtil.AESDecrypt(URLDecoder.decode(v,StandardCharsets.UTF_8.name()), key, "ECB");
                            if(srcValue == null){
                                throw new BaseException(ErrorType.DECRYPT_ERROR);
                            }
                            newParams.put(new String(srcKey), new String(srcValue));
                        } catch (Exception e) {
                            log.error("客户端数据解析异常", e);
                            throw new BaseException(ErrorType.DECRYPT_ERROR);
                        }           
                    });
                    log.info("newParams:{}",newParams);
                    decryptBytes = toUrlString(newParams).getBytes(StandardCharsets.UTF_8);
                } else if (contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                    //暂不支持
                    Object cachedRequestBodyObject = exchange.getAttributeOrDefault(Constant.REQUEST_BODY_OBJECT, null);
                    byte[] body = (byte[]) cachedRequestBodyObject;
                    String rootData = new String(body);
                    log.debug("data:{}",rootData);
                    throw new BaseException(ErrorType.DECRYPT_ERROR);
                } else {
                    log.error("不支持MediaType{}进行加密请求",contentType);
                    throw new BaseException(ErrorType.DECRYPT_ERROR);
                }
                DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
                Flux<DataBuffer> bodyFlux = Flux.just(dataBufferFactory.wrap(decryptBytes));
                ServerHttpRequest newRequest = request.mutate().uri(uri).build();
                newRequest = new ServerHttpRequestDecorator(newRequest) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        return bodyFlux;
                    }
                };
                CustomHttpHeaders headers = new CustomHttpHeaders();
                headers.putAll(exchange.getRequest().getHeaders());
                // 由于修改了传递参数，需要重新设置CONTENT_LENGTH，长度是字节长度，不是字符串长度
                int length = decryptBytes.length;
                headers.remove(CustomHttpHeaders.CONTENT_LENGTH);
                headers.setContentLength(length);
                // headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
                newRequest = new ServerHttpRequestDecorator(newRequest) {
                    @Override
                    public CustomHttpHeaders getHeaders() {
                        return headers;
                    }
                };
                // 把解密后的数据重置到exchange自定义属性中,在之后的日志GlobalLogFilter从此处获取请求参数打印日志
                exchange.getAttributes().put(Constant.REQUEST_BODY_OBJECT, decryptBytes);
                return chain.filter(exchange.mutate().request(newRequest).build());
            } else {
                //暂时只支持AES
                throw new BaseException(ErrorType.DECRYPT_ERROR);
            }
        } catch (Exception e){
            log.error("客户端数据解析异常", e);
            throw new BaseException(ErrorType.DECRYPT_ERROR);
        }
    }

    @Override
    public int getOrder() {
        return 10003;
    }

    private Map<String, String> toMap(String url) {
        final Map<String, String> paramsMap = new LinkedHashMap<String, String>();
        Stream.of(url.split(PARAM_TOKENIZER)).forEach( str -> paramsMap.put(str.split(PARAM_DEFINE)[0], str.split(PARAM_DEFINE)[1]));
        return paramsMap;
    }

    private String toUrlString(Map<String, String> params) {
        StringBuffer ret = new StringBuffer();
        params.forEach((k,v) -> {
            try {
                ret.append(URLEncoder.encode(k,StandardCharsets.UTF_8.name()));
                ret.append("=");
                ret.append(URLEncoder.encode(v,StandardCharsets.UTF_8.name()));
                ret.append("&");
            } catch (UnsupportedEncodingException e) {
                throw new BaseException(ErrorType.DECRYPT_ERROR);
            }
        });
        return ret.toString().substring(0,ret.length() - 1 - 1);
    }
}
