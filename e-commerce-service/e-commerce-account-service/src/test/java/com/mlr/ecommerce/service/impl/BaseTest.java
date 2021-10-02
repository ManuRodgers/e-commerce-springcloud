package com.mlr.ecommerce.service.impl;

import com.mlr.ecommerce.filter.AccessContext;
import com.mlr.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public abstract class BaseTest {
  protected final LoginUserInfo loginUserInfo =
      LoginUserInfo.builder().userId(12L).username("manu@imooc.com").build();

  @Before
  public void init() {
    AccessContext.setLoginUserInfo(loginUserInfo);
  }

  @After
  public void destroy() {
    AccessContext.clearLoginUserInfo();
  }
}
