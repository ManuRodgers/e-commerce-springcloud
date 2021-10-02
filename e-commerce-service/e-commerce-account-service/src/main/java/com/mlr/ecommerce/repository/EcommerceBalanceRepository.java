package com.mlr.ecommerce.repository;

import com.mlr.ecommerce.entity.EcommerceBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 *
 *
 * <h1>EcommerceBalance Dao 接口定义</h1>
 */
@Repository
public interface EcommerceBalanceRepository extends JpaRepository<EcommerceBalance, Long> {

  /**
   *
   *
   * <h2>根据 userId 查询 EcommerceBalance 对象</h2>
   */
  EcommerceBalance findByUserId(Long userId);
}
