package com.mlr.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 * <h1>授权中心鉴权之后给客户端的 Token</h1>
 *
 * @author manurodgers
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenVo {
  /** JWT */
  private String token;
}
