package com.mlr.ecommerce.service.impl;

import com.mlr.ecommerce.account.BalanceVo;
import com.mlr.ecommerce.entity.EcommerceBalance;
import com.mlr.ecommerce.filter.AccessContext;
import com.mlr.ecommerce.repository.EcommerceBalanceRepository;
import com.mlr.ecommerce.service.IBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 *
 * <h1>用于余额相关服务接口实现</h1>
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BalanceServiceImpl implements IBalanceService {
  @Autowired private EcommerceBalanceRepository ecommerceBalanceRepository;

  /**
   *
   *
   * <h2>获取当前用户余额信息</h2>
   */
  @Override
  public BalanceVo getCurrentUserBalance() {
    final Long userId = AccessContext.getLoginUserInfo().getUserId();
    final BalanceVo balanceVo = BalanceVo.builder().userId(userId).balance(0L).build();
    final EcommerceBalance ecommerceBalance = ecommerceBalanceRepository.findByUserId(userId);
    if (ecommerceBalance != null) {
      balanceVo.setBalance(ecommerceBalance.getBalance());
    } else {
      // 如果还没有用户余额记录, 这里创建出来，余额设定为0即可
      final EcommerceBalance newEcommerceBalance =
          EcommerceBalance.builder().userId(userId).balance(0L).build();
      log.info(
          "init user balance record: [{}]",
          ecommerceBalanceRepository.save(newEcommerceBalance).getId());
    }
    return balanceVo;
  }

  /**
   *
   *
   * <h2>扣减用户余额</h2>
   */
  @Override
  public BalanceVo deductBalance(BalanceVo balanceVo) {
    final Long userId = AccessContext.getLoginUserInfo().getUserId();
    // 扣减用户余额的一个基本原则: 扣减额 <= 当前用户余额
    final EcommerceBalance ecommerceBalance = ecommerceBalanceRepository.findByUserId(userId);

    if (ecommerceBalance == null || ecommerceBalance.getBalance() - balanceVo.getBalance() < 0) {
      throw new RuntimeException("user balance is not enough!");
    }
    Long sourceBalance = ecommerceBalance.getBalance();
    ecommerceBalance.setBalance(sourceBalance - balanceVo.getBalance());
    log.info(
        "deduct balance: {}. {},{}",
        ecommerceBalanceRepository.save(ecommerceBalance).getId(),
        sourceBalance,
        balanceVo.getBalance());
    return BalanceVo.builder()
        .balance(ecommerceBalance.getBalance())
        .userId(ecommerceBalance.getUserId())
        .build();
  }
}
