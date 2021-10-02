package com.mlr.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 * <h1>用户名和密码</h1>
 *
 * @author manurodgers
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsernameAndPassword {
  /** 用户名 */
  private String username;

  /** 密码 */
  private String password;
}