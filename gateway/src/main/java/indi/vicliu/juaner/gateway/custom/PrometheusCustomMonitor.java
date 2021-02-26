package indi.vicliu.juaner.gateway.custom;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PrometheusCustomMonitor {

    private Counter httpRequestCount;

    private PrometheusMeterRegistry registry;

    @Autowired
    public PrometheusCustomMonitor(PrometheusMeterRegistry registry) {
        this.registry = registry;
    }
    @PostConstruct
    private void init() {
        httpRequestCount = Counter.build().name("http_request_error_result_count").help("网关获取出错请求统计").labelNames("uri","status")
                .register(this.registry.getPrometheusRegistry());
    }

    public Counter getHttpRequestCount() {
        return httpRequestCount;
    }
}
