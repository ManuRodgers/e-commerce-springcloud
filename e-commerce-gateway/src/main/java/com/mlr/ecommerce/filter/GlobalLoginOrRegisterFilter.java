package com.mlr.ecommerce.filter;

import com.alibaba.fastjson.JSON;
import com.mlr.ecommerce.constant.CommonConstant;
import com.mlr.ecommerce.constant.GatewayConstant;
import com.mlr.ecommerce.util.TokenParseUtil;
import com.mlr.ecommerce.vo.JwtTokenVo;
import com.mlr.ecommerce.vo.LoginUserInfo;
import com.mlr.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 *
 * <h1>全局登录鉴权过滤器</h1>
 */
@Component
@Slf4j
public class GlobalLoginOrRegisterFilter implements GlobalFilter, Ordered {
  /** 注册中心客户端, 可以从注册中心中获取服务实例信息 */
  @Autowired private LoadBalancerClient loadBalancerClient;

  @Autowired private RestTemplate restTemplate;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    final ServerHttpRequest request = exchange.getRequest();
    final ServerHttpResponse response = exchange.getResponse();
    // 1. 如果是登录
    if (request.getURI().getPath().contains(GatewayConstant.LOGIN_URI)) {
      // 去授权中心拿 token
      final String token =
          getTokenFromAuthorityCenter(request, GatewayConstant.AUTHORITY_CENTER_TOKEN_URL_FORMAT);
      // header 中不能设置 null
      response.getHeaders().add(CommonConstant.JWT_USER_INFO_KEY, token == null ? "null" : token);
      response.setStatusCode(HttpStatus.OK);
      return response.setComplete();
    }
    // 2. 如果是注册
    if (request.getURI().getPath().contains(GatewayConstant.REGISTER_URI)) {
      // 去授权中心拿 token: 先创建用户, 再返回 Token
      final String token =
          getTokenFromAuthorityCenter(
              request, GatewayConstant.AUTHORITY_CENTER_REGISTER_URL_FORMAT);
      response.getHeaders().add(CommonConstant.JWT_USER_INFO_KEY, token == null ? "null" : token);
      response.setStatusCode(HttpStatus.OK);
      return response.setComplete();
    }
    // 3. 访问其他的服务, 则鉴权, 校验是否能够从 Token 中解析出用户信息
    final String token = request.getHeaders().getFirst(CommonConstant.JWT_USER_INFO_KEY);
    LoginUserInfo loginUserInfo = null;
    try {
      loginUserInfo = TokenParseUtil.parseUserInfoFromToken(token);
    } catch (Exception e) {
      log.info("parse user info from token error: [{}]", e.getMessage(), e);
    }
    // 获取不到登录用户信息, 返回 401
    if (loginUserInfo == null) {
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      return response.setComplete();
    }
    // 解析通过, 则放行
    return chain.filter(exchange);
  }

  @Override
  public int getOrder() {
    return HIGHEST_PRECEDENCE + 2;
  }

  /**
   *
   *
   * <h2>从授权中心获取 Token</h2>
   */
  private String getTokenFromAuthorityCenter(ServerHttpRequest request, String uriFormat) {
    // service id 就是服务名字, 负载均衡
    final ServiceInstance serviceInstance =
        loadBalancerClient.choose(CommonConstant.AUTHORITY_CENTER_SERVICE_ID);
    log.info(
        "Nacos client info: {}, {}, {}",
        serviceInstance.getServiceId(),
        serviceInstance.getInstanceId(),
        JSON.toJSONString(serviceInstance.getMetadata()));
    final String requestUrl =
        String.format(uriFormat, serviceInstance.getHost(), serviceInstance.getPort());
    final UsernameAndPassword requestBody =
        JSON.parseObject(parseBodyFromRequest(request), UsernameAndPassword.class);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    final JwtTokenVo jwtTokenVo =
        restTemplate.postForObject(
            requestUrl,
            new HttpEntity<>(JSON.toJSONString(requestBody), httpHeaders),
            JwtTokenVo.class);
    if (jwtTokenVo == null) {
      return null;
    }
    return jwtTokenVo.getToken();
  }

  /**
   *
   *
   * <h2>从 Post 请求中获取到请求数据</h2>
   */
  private String parseBodyFromRequest(ServerHttpRequest request) {
    // 获取请求体
    final Flux<DataBuffer> body = request.getBody();
    AtomicReference<String> bodyRef = new AtomicReference<>();
    // 订阅缓冲区去消费请求体中的数据
    body.subscribe(
        dataBuffer -> {
          final CharBuffer charBuffer = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer());
          // 一定要使用 DataBufferUtils.release 释放掉, 否则, 会出现内存泄露
          DataBufferUtils.release(dataBuffer);
          bodyRef.set(charBuffer.toString());
        });
    // 获取 request body
    return bodyRef.get();
  }
}
