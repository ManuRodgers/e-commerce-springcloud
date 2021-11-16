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
 * <h1>扣减商品库存</h1>
 */
@ApiModel(description = "扣减商品库存对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeductGoodsInventory {
  @ApiModelProperty(value = "商品主键 Id")
  private Long goodsId;

  @ApiModelProperty(value = "扣减个数")
  private Integer count;
}
