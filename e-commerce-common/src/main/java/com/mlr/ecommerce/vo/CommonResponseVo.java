package com.mlr.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 *
 * <h1>通用响应对象定义</h1>
 *
 * { "code": 0, "message": "", "data": {} }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResponseVo<T> implements Serializable {
  /** 错误码 */
  private Integer code;

  /** 错误消息 */
  private String message;

  /** 泛型响应数据 */
  private T Data;
}
