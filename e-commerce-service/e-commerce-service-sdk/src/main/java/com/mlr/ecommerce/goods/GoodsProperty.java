package com.mlr.ecommerce.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 * <h1>商品属性</h1>
 *
 * @author manurodgers
 */
@ApiModel(description = "商品属性对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsProperty {
  @ApiModelProperty(value = "尺寸")
  private String size;

  @ApiModelProperty(value = "颜色")
  private String color;

  @ApiModelProperty(value = "材质")
  private String material;

  @ApiModelProperty(value = "图案")
  private String pattern;
}
