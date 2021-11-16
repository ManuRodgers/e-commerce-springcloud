package com.mlr.ecommerce.converter;

import com.mlr.ecommerce.constant.GoodsCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 *
 * <h1>商品类别枚举属性转换器</h1>
 *
 * @author manurodgers
 */
@Converter
public class GoodsCategoryConverter implements AttributeConverter<GoodsCategory, String> {
  @Override
  public String convertToDatabaseColumn(GoodsCategory goodsCategory) {
    return goodsCategory.getCode();
  }

  @Override
  public GoodsCategory convertToEntityAttribute(String code) {
    return GoodsCategory.of(code);
  }
}
