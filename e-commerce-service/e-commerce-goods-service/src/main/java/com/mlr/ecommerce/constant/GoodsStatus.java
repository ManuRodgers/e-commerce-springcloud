package com.mlr.ecommerce.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 *
 * <h1>商品状态枚举类</h1>
 *
 * @author manurodgers
 */
@Getter
@AllArgsConstructor
public enum GoodsStatus {
  /** */
  ONLINE(101, "online"),
  OFFLINE(102, "offline"),
  OUT_OF_STOCK(103, "out of stock"),
  ;
  /** 状态码 */
  private final Integer statusCode;

  /** 状态描述 */
  private final String description;

  /**
   *
   *
   * <h2>根据 code 获取到 GoodsStatus</h2>
   */
  public static GoodsStatus of(Integer statusCode) {
    Objects.requireNonNull(statusCode);

    return Stream.of(values())
        .filter(bean -> bean.statusCode.equals(statusCode))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException(statusCode + "not exists"));
  }
}
