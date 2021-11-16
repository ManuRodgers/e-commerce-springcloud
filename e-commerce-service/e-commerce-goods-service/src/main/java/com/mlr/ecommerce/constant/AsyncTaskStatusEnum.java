package com.mlr.ecommerce.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 *
 * <h1>异步任务状态枚举</h1>
 *
 * @author manurodgers
 */
@Getter
@AllArgsConstructor
public enum AsyncTaskStatusEnum {
  /** */
  STARTED(0, "started"),
  RUNNING(1, "running"),
  SUCCEEDED(2, "succeeded"),
  FAILED(3, "failed"),
  ;
  /** 执行状态编码 */
  private final Integer statusCode;
  /** 执行状态描述 */
  private final String statusInfo;
}
