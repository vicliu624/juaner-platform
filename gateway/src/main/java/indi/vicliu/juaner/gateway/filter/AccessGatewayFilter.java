package indi.vicliu.juaner.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.common.core.CommonConstant;
import indi.vicliu.juaner.gateway.client.service.AuthService;
import indi.vicliu.juaner.gateway.utils.RedisStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.jwt.Jwt;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.isAlreadyRouted;

/**
 * @Auther: liuweikai
 * @Date: 2019-03-22 12:56
 * @Description: 认证过滤器
 */
@Slf4j
@Configuration
public class AccessGatewayFilter implements GlobalFilter {

    /**
     * 由authentication-client模块提供签权的feign客户端
     */
    @Autowired
    private AuthService authService;

    @Autowired
    private RedisStringUtil redisStringUtil;


    @Autowired
    WebSockerFilter webSockerFilter;

    private static String WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
    /**
     * 1.首先网关检查token是否有效，无效直接返回401，不调用签权服务
     * 2.调用签权服务器看是否对该请求有权限，有权限进入下一个filter，没有权限返回401
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authentication = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String method = request.getMethodValue();
        String url = request.getPath().value();
        log.debug("url:{},method:{},headers:{}", url, method, request.getHeaders());
        URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
        log.info("当前访问路径为 {}",requestUrl.toString());
        String scheme = requestUrl.getScheme();
        if (isAlreadyRouted(exchange)
                || ("ws".equals(scheme) && "wss".equals(scheme))) {
            return webSockerFilter.filter(exchange,chain);
        }
        /*if (StringUtils.isNotEmpty(request.getHeaders().getFirst(WEBSOCKET_PROTOCOL))) {
            log.info("WebSocket In AccessGatewayFilter ");
            return webSockerFilter.filter(exchange,chain);
        }*/
        //不需要网关签权的url
        if (authService.ignoreAuthentication(url)) {
            return chain.filter(exchange);
        }

        if(StringUtils.isEmpty(authentication)){
            return unauthorized(exchange);
        }

        Jwt jwt = authService.getJwt(authentication);

        //校验jwt
        //token是否有效
        if (authService.invalidJwtAccessToken(jwt)) {
            return unauthorized(exchange);
        }

        //token是否是当前生效的token
        String claims = jwt.getClaims();
        log.debug("get jwt by authentication:{} claims:{}",authentication,claims);

        JSONObject jsonObject = JSONObject.parseObject(claims);
        String userId = jsonObject.get("user_id").toString();
        String jti = (String) jsonObject.get("jti");
        String redisJti = redisStringUtil.getValue(CommonConstant.USER_TOKEN_KEY + userId);
        if(redisJti != null && !jti.equals(redisJti)){
            log.debug(" key:[{}],value:[{}],jti:[{}] ",CommonConstant.USER_TOKEN_KEY + userId,redisJti,jti);
            return conflict(exchange);
        }

        //调用签权服务看用户是否有权限，若有权限进入下一个filter
        if (authService.hasPermission(authentication, url, method)) {
            ServerHttpRequest.Builder builder = request.mutate();
            //TODO 转发的请求都加上服务间认证token
            builder.header(CommonConstant.X_CLIENT_TOKEN_USER, claims);
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }
        return unauthorized(exchange);
    }

    /**
     * 网关拒绝，返回401
     *
     * @param
     */
    private Mono<Void> unauthorized(ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        DataBuffer buffer = serverWebExchange.getResponse()
                .bufferFactory().wrap(HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes(StandardCharsets.UTF_8));
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }

    /**
     *
     * @param serverWebExchange
     * @return
     */
    private Mono<Void> conflict(ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.CONFLICT);
        DataBuffer buffer = serverWebExchange.getResponse()
                .bufferFactory().wrap(HttpStatus.CONFLICT.getReasonPhrase().getBytes(StandardCharsets.UTF_8));
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }
}
