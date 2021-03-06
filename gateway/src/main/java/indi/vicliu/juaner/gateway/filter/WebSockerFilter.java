package indi.vicliu.juaner.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import indi.vicliu.juaner.common.core.CommonConstant;
import indi.vicliu.juaner.gateway.client.service.AuthService;
import indi.vicliu.juaner.gateway.utils.RedisStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.WebsocketRoutingFilter;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.jwt.Jwt;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @program: juaner-platform
 * @description:
 * @author: liuwenqi
 * @create: 2019-12-23 18:12
 **/
@Slf4j
@Configuration
public class WebSockerFilter extends WebsocketRoutingFilter {
    public WebSockerFilter(WebSocketClient webSocketClient, WebSocketService webSocketService, ObjectProvider<List<HttpHeadersFilter>> headersFiltersProvider) {
        super(webSocketClient, webSocketService, headersFiltersProvider);
    }

    @Autowired
    private AuthService authService;

    @Autowired
    private RedisStringUtil redisStringUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
        String scheme = requestUrl.getScheme();
        log.info("WebSockerFilter 当前访问路径为 {}",requestUrl.toString());
        if (requestUrl.toString().indexOf("ws/endpoint") == -1) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        String authentication = request.getHeaders().getFirst("Sec-WebSocket-Protocol");
        log.info("websoket 传入的 Token {}" ,authentication);
        if (Strings.isNullOrEmpty(authentication)) { //如果token为空则直接报权限异常
            return unauthorized(exchange);
        }else {
            authentication = "bearer " + authentication;
        }
        String method = request.getMethodValue();
        String url = request.getPath().value();
        log.info("url:{},method:{},headers:{}", url, method, request.getHeaders());
        Jwt jwt = authService.getJwt(authentication);
        //校验jwt
        //token是否有效
        if (authService.invalidJwtAccessToken(jwt)) {
            return unauthorized(exchange);
        }else {
            log.info("token无效");
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
        log.info("进行权限判断");
        //调用签权服务看用户是否有权限，若有权限进入下一个filter
        if (authService.hasPermission(authentication, url, method)) {
            log.debug("该WebSocket有访问权限");
            ServerHttpRequest.Builder builder = request.mutate();
            //TODO 转发的请求都加上服务间认证token
            builder.header(CommonConstant.X_CLIENT_TOKEN_USER, claims);
            return super.filter(exchange.mutate().request(builder.build()).build(),chain);
        }else {
            log.info("该WebSocket无访问权限");
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
