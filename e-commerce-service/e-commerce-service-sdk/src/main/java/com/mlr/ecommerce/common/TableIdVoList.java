package com.mlr.ecommerce.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 *
 * <h1>主键 ids</h1>
 */
@ApiModel(description = "通用 id 对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableIdVoList {

  @ApiModelProperty(value = "数据表记录主键")
  private List<TableIdVo> tableIdVoList;
}
