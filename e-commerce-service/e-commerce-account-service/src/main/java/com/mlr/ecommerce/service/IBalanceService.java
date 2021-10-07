package com.mlr.ecommerce.service;

import com.mlr.ecommerce.account.BalanceVo;

/**
 *
 *
 * <h2>用于余额相关的服务接口定义</h2>
 */
public interface IBalanceService {

  /**
   *
   *
   * <h2>获取当前用户余额信息</h2>
   */
  BalanceVo getCurrentUserBalance();

  /**
   *
   *
   * <h2>扣减用户余额</h2>
   *
   * @param BalanceVo 代表想要扣减的余额
   */
  BalanceVo deductBalance(BalanceVo BalanceVo);
}
