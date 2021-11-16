package com.mlr.ecommerce.converter;

import com.mlr.ecommerce.constant.BrandCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 *
 * <h1>品牌分类枚举属性转换器</h1>
 *
 * @author manurodgers
 */
@Converter
public class BrandCategoryConverter implements AttributeConverter<BrandCategory, String> {
  @Override
  public String convertToDatabaseColumn(BrandCategory brandCategory) {
    return brandCategory.getCode();
  }

  @Override
  public BrandCategory convertToEntityAttribute(String code) {
    return BrandCategory.of(code);
  }
}
