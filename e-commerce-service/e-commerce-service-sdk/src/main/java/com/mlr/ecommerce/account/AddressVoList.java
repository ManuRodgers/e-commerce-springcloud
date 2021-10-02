package com.mlr.ecommerce.account;

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
 * <h1>用户地址信息</h1>
 */
@ApiModel(description = "用户地址信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressVoList {

  @ApiModelProperty(value = "地址所属的用户 id")
  private Long userId;

  @ApiModelProperty(value = "地址详细信息")
  private List<AddressVo> addressVoList;
}
