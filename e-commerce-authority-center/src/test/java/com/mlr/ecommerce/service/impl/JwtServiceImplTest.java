package com.mlr.ecommerce.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.mlr.ecommerce.service.IJwtService;
import com.mlr.ecommerce.util.TokenParseUtil;
import com.mlr.ecommerce.vo.LoginUserInfo;
import com.mlr.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 *
 * <h1>JWT 相关服务测试类</h1>
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
class JwtServiceImplTest {

  @Autowired private IJwtService iJwtService;

  @Test
  void testGenerateAndParseToken() throws Exception {

    String jwtToken =
        iJwtService.generateToken("manu@imooc.com", "25d55ad283aa400af464c76d713c07ad");
    log.info("jwt token is: [{}]", jwtToken);

    LoginUserInfo loginUserInfo = TokenParseUtil.parseUserInfoFromToken(jwtToken);
    log.info("parse token: [{}]", JSON.toJSONString(loginUserInfo));
  }

  @Test
  void registerUser() throws Exception {
    UsernameAndPassword usernameAndPassword =
        UsernameAndPassword.builder()
            .username("pop@imooc.com")
            .password(MD5.create().digestHex("12345678"))
            .build();
    iJwtService.registerUserAndGenerateToken(usernameAndPassword);
  }
}
