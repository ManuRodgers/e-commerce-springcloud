package com.mlr.ecommerce.controller;

import com.mlr.ecommerce.account.BalanceVo;
import com.mlr.ecommerce.service.IBalanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 *
 * <h1>用户余额服务 Controller</h1>
 */
@Api(tags = "用户余额服务")
@Slf4j
@RestController
@RequestMapping("/balance")
public class BalanceController {
  @Autowired private IBalanceService balanceService;

  @ApiOperation(value = "当前用户", notes = "获取当前用户余额", httpMethod = "GET")
  @GetMapping("/current-balance")
  public BalanceVo getCurrentUserBalance() {
    return balanceService.getCurrentUserBalance();
  }

  @ApiOperation(value = "扣减", notes = "扣减用户余额", httpMethod = "PUT")
  @PutMapping("/deduct-balance")
  public BalanceVo deductBalance(@RequestBody BalanceVo balanceVo) {
    return balanceService.deductBalance(balanceVo);
  }
}
