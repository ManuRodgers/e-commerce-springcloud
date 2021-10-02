package com.mlr.ecommerce.entity;

import com.mlr.ecommerce.account.AddressVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "t_ecommerce_address")
public class EcommerceAddress implements Serializable {
  /** 自增主键 */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "phone", nullable = false)
  private String phone;

  @Column(name = "province", nullable = false)
  private String province;

  @Column(name = "city", nullable = false)
  private String city;

  @Column(name = "address_detail", nullable = false)
  private String addressDetail;

  @CreatedDate
  @Column(name = "create_time", nullable = false)
  private Date createTime;

  @LastModifiedDate
  @Column(name = "update_time", nullable = false)
  private Date updateTime;
  /**
   *
   *
   * <h2>根据 userId + AddressVo 得到 EcommerceAddress</h2>
   */
  public static EcommerceAddress toEcommerceAddress(Long userId, AddressVo addressVo) {
    EcommerceAddress ecommerceAddress = EcommerceAddress.builder().userId(userId).build();
    BeanUtils.copyProperties(addressVo, ecommerceAddress);
    return ecommerceAddress;
  }

  /**
   *
   *
   * <h2>将 EcommerceAddress 对象转成 AddressVo</h2>
   */
  public AddressVo toAddressVo() {
    AddressVo addressVo = AddressVo.builder().build();
    BeanUtils.copyProperties(this, addressVo);
    return addressVo;
  }
}
