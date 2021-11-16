package com.mlr.ecommerce.converter;

import com.mlr.ecommerce.constant.GoodsStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 *
 * <h1>商品状态枚举属性转换器</h1>
 *
 * @author manurodgers
 */
@Converter
public class GoodsStatusConverter implements AttributeConverter<GoodsStatus, Integer> {
  @Override
  public Integer convertToDatabaseColumn(GoodsStatus goodsStatus) {
    return goodsStatus.getStatusCode();
  }

  @Override
  public GoodsStatus convertToEntityAttribute(Integer statusCode) {
    return GoodsStatus.of(statusCode);
  }
}
