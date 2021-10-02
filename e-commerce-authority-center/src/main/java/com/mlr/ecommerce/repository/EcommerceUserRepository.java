package com.mlr.ecommerce.repository;

import com.mlr.ecommerce.entity.EcommerceUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 *
 * <h1>EcommerceUser Dao 接口定义</h1>
 *
 * @author manurodgers
 */
@Repository
public interface EcommerceUserRepository extends JpaRepository<EcommerceUser, Long> {
  /**
   *
   *
   * <h2>根据用户名查询 EcommerceUser 对象</h2>
   *
   * @param username select * from t_ecommerce_user where username = ?
   * @return EcommerceUser
   */
  EcommerceUser findByUsername(String username);

  /**
   *
   *
   * <h2>根据用户名和密码查询实体对象</h2>
   *
   * select * from t_ecommerce_user where username = ? and password = ?
   */
  EcommerceUser findByUsernameAndPassword(String username, String password);
}
