package com.mlr.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 * <h1>登录用户信息</h1>
 * @author manurodgers
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUserInfo {
  /** 用户 id */
  private Long userId;

  /** 用户名 */
  private String username;
}
