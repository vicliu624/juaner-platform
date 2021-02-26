/**
 * @Auther: vicliu
 * Date: 2021/2/26 下午4:07
 * @Description:
 */

package indi.vicliu.juaner.gateway.exception;

/**
 * @Auther: vicliu
 * Date: 2021/2/26 下午3:16
 * @Description:
 */

import com.alibaba.fastjson.JSONObject;
import indi.vicliu.juaner.common.core.exception.ErrorType;
import indi.vicliu.juaner.common.core.message.Result;
import indi.vicliu.juaner.gateway.custom.PrometheusCustomMonitor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;


@Component
//如果没有这个注解，你自定义的这个可能不会生效。因为 WebFlux 有一个默认的实现 DefaultErrorWebExceptionHandler，它的优先级是 @Order(-1)。所以加上 @Order(-2) 是让我们自己的实现拥有更高的优先级。
@Order(-2)
@Slf4j
public class JuanerErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    @Autowired
    PrometheusCustomMonitor monitor;

    public JuanerErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                          ApplicationContext applicationContext,
                                          ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new ResourceProperties(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(
            ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(
            ServerRequest request) {
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults()
                .including(ErrorAttributeOptions.Include.MESSAGE)
                .including(ErrorAttributeOptions.Include.BINDING_ERRORS)
                .including(ErrorAttributeOptions.Include.EXCEPTION)
                .including(ErrorAttributeOptions.Include.STACK_TRACE);
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, options);
        // 这里可以自定义处理逻辑
        Integer httpStatus = (Integer)errorPropertiesMap.get("status");
        String path = (String)errorPropertiesMap.get("path");
        log.info("全局异常->path:{},http status:{}",path,httpStatus);
        log.debug("全局异常->trace:{}",errorPropertiesMap.get("trace"));
        Tips tips = new Tips();
        tips.setHttpStatus(httpStatus);
        tips.setPath(path);
        //记录到metric
        monitor.getHttpRequestCount().labels(path,String.valueOf(httpStatus)).inc();
        Result result;
        if (httpStatus.equals(404)) {
            result = Result.fail(ErrorType.NOT_FOUND,tips);
        } else if (httpStatus.equals(429)) {
            result = Result.fail(ErrorType.TOO_MANY_REQUEST_ERROR,tips);
        } else if (httpStatus.equals(500)) {
            result = Result.fail(ErrorType.HANDLE_ERROR,tips);
        } else if (httpStatus.equals(504)) {
            result = Result.fail(ErrorType.REQUEST_DOWNSTREAM_TIMEOUT_ERROR,tips);
        } else {
            result = Result.fail(ErrorType.UNKNOWN_ERROR,tips);
        }
        return ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(result));
    }

    @Data
    class Tips{
        private int httpStatus;
        private String path;
    }
}

