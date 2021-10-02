package com.mlr.ecommerce.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 *
 *
 * <h2>单个的地址信息</h2>
 */
@ApiModel(description = "用户的单个地址信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressVo {

  @ApiModelProperty(value = "地址表主键 id")
  private Long id;

  @ApiModelProperty(value = "用户姓名")
  private String username;

  @ApiModelProperty(value = "电话")
  private String phone;

  @ApiModelProperty(value = "省")
  private String province;

  @ApiModelProperty(value = "市")
  private String city;

  @ApiModelProperty(value = "详细的地址")
  private String addressDetail;

  @ApiModelProperty(value = "创建时间")
  private Date createTime;

  @ApiModelProperty(value = "更新时间")
  private Date updateTime;

  /**
   *
   *
   * <h2>将 AddressVo 转换成 UserAddressVo</h2>
   */
  public UserAddressVo toUserAddressVo() {
    UserAddressVo userAddressVo = UserAddressVo.builder().build();
    BeanUtils.copyProperties(this, userAddressVo);
    return userAddressVo;
  }
}
