package com.mlr.ecommerce.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 *
 *
 * <h1>全局接口耗时日志过滤器</h1>
 */
@Slf4j
@Component
public class GlobalElapsedLogFilter implements GlobalFilter, Ordered {
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // 前置逻辑
    final StopWatch stopWatch = StopWatch.createStarted();
    final String path = exchange.getRequest().getURI().getPath();
    return chain
        .filter(exchange)
        .then(
            Mono.fromRunnable(
                () ->
                    log.info("{} elapsed: {}ms", path, stopWatch.getTime(TimeUnit.MILLISECONDS))));
  }

  @Override
  public int getOrder() {
    return HIGHEST_PRECEDENCE;
  }
}
