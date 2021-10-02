package com.mlr.ecommerce.service;

import com.mlr.ecommerce.account.AddressVoList;
import com.mlr.ecommerce.common.TableIdVoList;

/**
 *
 *
 * <h1>用户地址相关服务接口定义</h1>
 */
public interface IAddressService {
  /**
   *
   *
   * <h2>创建用户地址信息</h2>
   */
  TableIdVoList createUserAddressInfo(AddressVoList addressListVo);

  /**
   *
   *
   * <h2>获取当前登录的用户地址信息</h2>
   */
  AddressVoList getUserAddressInfo();

  /**
   *
   *
   * <h2>通过 id 获取用户地址信息, id 是 EcommerceAddress 表的主键</h2>
   */
  AddressVoList getUserAddressInfoById(Long id);

  /**
   *
   *
   * <h2>通过 TableIdListVo 获取用户地址信息</h2>
   */
  AddressVoList getUserAddressInfoByTableId(TableIdVoList tableIdListVo);
}
