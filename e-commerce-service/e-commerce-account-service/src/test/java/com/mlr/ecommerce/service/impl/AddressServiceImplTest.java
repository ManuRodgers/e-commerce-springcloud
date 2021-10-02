package com.mlr.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.mlr.ecommerce.account.AddressVo;
import com.mlr.ecommerce.account.AddressVoList;
import com.mlr.ecommerce.service.IAddressService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

/**
 *
 *
 * <h1>用户地址相关服务功能测试</h1>
 */
@Slf4j
class AddressServiceImplTest extends BaseTest {

  @Autowired private IAddressService addressService;
  /**
   *
   *
   * <h2>测试创建用户地址信息</h2>
   */
  @Test
  void createUserAddressInfo() {
    final AddressVo addressVo =
        AddressVo.builder()
            .addressDetail("22 Nicholas Drive")
            .username("manu")
            .city("Hobart")
            .phone("0451962387")
            .province("TAS")
            .build();
    final AddressVoList addressVoList =
        AddressVoList.builder()
            .userId(loginUserInfo.getUserId())
            .addressVoList(Collections.singletonList(addressVo))
            .build();

    log.info(
        "test create User addressVoList {}",
        JSON.toJSONString(addressService.createUserAddressInfo(addressVoList)));
  }

  @Test
  void getUserAddressInfo() {}

  @Test
  void getUserAddressInfoById() {}

  @Test
  void getUserAddressInfoByTableId() {}
}
