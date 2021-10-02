package com.mlr.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.mlr.ecommerce.account.AddressVoList;
import com.mlr.ecommerce.account.AddressVo;
import com.mlr.ecommerce.common.TableIdVoList;
import com.mlr.ecommerce.common.TableIdVo;
import com.mlr.ecommerce.entity.EcommerceAddress;
import com.mlr.ecommerce.filter.AccessContext;
import com.mlr.ecommerce.repository.EcommerceAddressRepository;
import com.mlr.ecommerce.service.IAddressService;
import com.mlr.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AddressServiceImpl implements IAddressService {

  @Autowired private EcommerceAddressRepository ecommerceAddressRepository;
  /**
   *
   *
   * <h2>创建用户地址信息</h2>
   *
   * @param addressListVo
   */
  @Override
  public TableIdVoList createUserAddressInfo(AddressVoList addressListVo) {
    // 不能直接从参数中获取用户的 id 信息
    LoginUserInfo loginUserInfo = AccessContext.getLoginUserInfo();
    // 将传递的参数转换成实体对象
    final List<EcommerceAddress> ecommerceAddressList =
        addressListVo.getAddressVoList().stream()
            .map(
                addressVo ->
                    EcommerceAddress.toEcommerceAddress(loginUserInfo.getUserId(), addressVo))
            .collect(Collectors.toList());
    final List<EcommerceAddress> savedEcommerceAddressList =
        ecommerceAddressRepository.saveAll(ecommerceAddressList);
    final List<Long> ids =
        savedEcommerceAddressList.stream()
            .map(EcommerceAddress::getId)
            .collect(Collectors.toList());
    log.info("create address info: [{}], [{}]", loginUserInfo.getUserId(), JSON.toJSONString(ids));
    final List<TableIdVo> tableIdVoList =
        ids.stream().map(id -> TableIdVo.builder().id(id).build()).collect(Collectors.toList());
    return TableIdVoList.builder().tableIdVoList(tableIdVoList).build();
  }

  /**
   *
   *
   * <h2>获取当前登录的用户地址信息</h2>
   */
  @Override
  public AddressVoList getUserAddressInfo() {
    final List<EcommerceAddress> ecommerceAddressList =
        ecommerceAddressRepository.findAllByUserId(AccessContext.getLoginUserInfo().getUserId());
    final List<AddressVo> addressVoList =
        ecommerceAddressList.stream()
            .map(EcommerceAddress::toAddressVo)
            .collect(Collectors.toList());
    return AddressVoList.builder().addressVoList(addressVoList).build();
  }

  /**
   *
   *
   * <h2>通过 id 获取用户地址信息, id 是 EcommerceAddress 表的主键</h2>
   *
   * @param id
   */
  @Override
  public AddressVoList getUserAddressInfoById(Long id) {
    final EcommerceAddress ecommerceAddress = ecommerceAddressRepository.findById(id).orElse(null);
    if (ecommerceAddress == null) {
      throw new RuntimeException("address is not exist");
    }
    return AddressVoList.builder()
        .userId(ecommerceAddress.getUserId())
        .addressVoList(Collections.singletonList(ecommerceAddress.toAddressVo()))
        .build();
  }

  /**
   *
   *
   * <h2>通过 TableIdVoList 获取用户地址信息</h2>
   *
   * @param tableIdListVo
   */
  @Override
  public AddressVoList getUserAddressInfoByTableId(TableIdVoList tableIdListVo) {
    final List<Long> ids =
        tableIdListVo.getTableIdVoList().stream()
            .map(TableIdVo::getId)
            .collect(Collectors.toList());
    log.info("get address info by table id: {}", JSON.toJSONString(ids));
    final List<EcommerceAddress> ecommerceAddressList = ecommerceAddressRepository.findAllById(ids);
    if (CollectionUtils.isEmpty(ecommerceAddressList)) {
      return AddressVoList.builder().userId(-1L).addressVoList(Collections.emptyList()).build();
    }
    final List<AddressVo> addressVoList =
        ecommerceAddressList.stream()
            .map(EcommerceAddress::toAddressVo)
            .collect(Collectors.toList());
    return AddressVoList.builder()
        .userId(ecommerceAddressList.get(0).getUserId())
        .addressVoList(addressVoList)
        .build();
  }
}
