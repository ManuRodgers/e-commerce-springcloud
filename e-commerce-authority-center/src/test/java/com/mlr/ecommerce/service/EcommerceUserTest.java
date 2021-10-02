package com.mlr.ecommerce.service;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.mlr.ecommerce.entity.EcommerceUser;
import com.mlr.ecommerce.repository.EcommerceUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 *
 * <h1>EcommerceUser 相关的测试</h1>
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class EcommerceUserTest {
  @Autowired private EcommerceUserRepository ecommerceUserRepository;

  @Test
  public void createUserRecord() {
    EcommerceUser ecommerceUser =
        EcommerceUser.builder()
            .username("manu@imooc.com")
            .password(MD5.create().digestHex("12345678"))
            .extraInfo("{}")
            .build();
    log.info("save user: [{}]", JSON.toJSON(ecommerceUserRepository.save(ecommerceUser)));
  }
}
