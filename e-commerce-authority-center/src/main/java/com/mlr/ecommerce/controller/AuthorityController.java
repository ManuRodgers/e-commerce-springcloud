package com.mlr.ecommerce.controller;

import com.alibaba.fastjson.JSON;
import com.mlr.ecommerce.annotation.IgnoreResponseAdvice;
import com.mlr.ecommerce.service.IJwtService;
import com.mlr.ecommerce.vo.JwtTokenVo;
import com.mlr.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * <h1>对外暴露的授权服务接口</h1>
 *
 * @author manurodgers
 */
@Slf4j
@RestController
@RequestMapping("/authority")
public class AuthorityController {
  @Autowired private IJwtService iJwtService;

  /**
   *
   *
   * <h2>从授权中心获取 Token (其实就是登录功能), 且返回信息中没有统一响应的包装</h2>
   */
  @IgnoreResponseAdvice
  @PostMapping("/token")
  public JwtTokenVo login(@RequestBody UsernameAndPassword usernameAndPassword) throws Exception {
    log.info("request to get token with param: [{}]", JSON.toJSONString(usernameAndPassword));
    return JwtTokenVo.builder()
        .token(
            iJwtService.generateToken(
                usernameAndPassword.getUsername(), usernameAndPassword.getPassword()))
        .build();
  }

  /**
   *
   *
   * <h2>注册用户并返回当前注册用户的 Token, 即通过授权中心创建用户</h2>
   */
  @IgnoreResponseAdvice
  @PostMapping("/register")
  public JwtTokenVo register(@RequestBody UsernameAndPassword usernameAndPassword)
      throws Exception {

    log.info("usernameAndPassword : {}", usernameAndPassword);
    log.info("register user with param: [{}]", JSON.toJSONString(usernameAndPassword));
    return new JwtTokenVo(iJwtService.registerUserAndGenerateToken(usernameAndPassword));
  }
}
