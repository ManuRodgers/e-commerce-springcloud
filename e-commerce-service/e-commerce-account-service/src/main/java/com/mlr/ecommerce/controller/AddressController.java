package com.mlr.ecommerce.controller;

import com.mlr.ecommerce.account.AddressVoList;
import com.mlr.ecommerce.common.TableIdVoList;
import com.mlr.ecommerce.service.IAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 *
 * <h1>用户地址服务 Controller</h1>
 */
@Api(tags = "用户地址服务")
@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController {
  @Autowired private IAddressService addressService;

  // value 是简述, notes 是详细的描述信息
  @ApiOperation(value = "创建", notes = "创建用户地址信息", httpMethod = "POST")
  @PostMapping("/")
  public TableIdVoList createUserAddressInfo(@RequestBody AddressVoList addressVoList) {
    log.info("addressVoList: {}", addressVoList);

    return addressService.createUserAddressInfo(addressVoList);
  }
  // value 是简述, notes 是详细的描述信息
  @ApiOperation(value = "当前用户", notes = "获取当前登录用户地址信息", httpMethod = "GET")
  @GetMapping("/")
  public AddressVoList getUserAddressInfo() {
    return addressService.getUserAddressInfo();
  } // value 是简述, notes 是详细的描述信息

  @ApiOperation(
      value = "获取用户地址信息",
      notes = "通过 id 获取用户地址信息, id 是 EcommerceAddress 表的主键",
      httpMethod = "GET")
  @GetMapping("/address-info-by-address-id")
  public AddressVoList getUserAddressInfoById(@RequestParam Long id) {
    return addressService.getUserAddressInfoById(id);
  }

  @ApiOperation(value = "获取用户地址信息", notes = "通过 TableIdVoList 获取用户地址信息", httpMethod = "POST")
  @PostMapping("/address-info-by-table-id")
  public AddressVoList getUserAddressInfoByTableId(@RequestBody TableIdVoList tableIdVoList) {
    return addressService.getUserAddressInfoByTableId(tableIdVoList);
  }
}
