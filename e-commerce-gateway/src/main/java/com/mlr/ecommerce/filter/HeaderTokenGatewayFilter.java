package com.mlr.ecommerce.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 *
 *
 * <h1>HTTP 请求头部携带 Token 验证过滤器</h1>
 */
@Slf4j
public class HeaderTokenGatewayFilter implements GatewayFilter, Ordered {
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // 从 HTTP Header 中寻找 key 为 token, value 为 imooc 的键值对
    log.info("HeaderTokenGatewayFilter");
    final String token = exchange.getRequest().getHeaders().getFirst("token");
    if ("imooc".equals(token)) {
      return chain.filter(exchange);
    }
    // 标记此次请求没有权限, 并结束这次请求
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    return exchange.getResponse().setComplete();
  }

  @Override
  public int getOrder() {
    return HIGHEST_PRECEDENCE + 2;
  }
}
