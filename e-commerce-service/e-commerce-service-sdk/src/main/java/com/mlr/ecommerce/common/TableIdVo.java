package com.mlr.ecommerce.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author manurodgers
 */
@ApiModel(description = "数据表记录主键对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableIdVo {
  @ApiModelProperty(value = "数据表记录主键")
  private Long id;
}
