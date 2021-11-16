package com.mlr.ecommerce.service;

import com.mlr.ecommerce.common.TableIdVoList;
import com.mlr.ecommerce.goods.DeductGoodsInventory;
import com.mlr.ecommerce.goods.GoodsVo;
import com.mlr.ecommerce.goods.SimpleGoodsVo;
import com.mlr.ecommerce.vo.PageSimpleGoodsInfo;

import java.util.List;

/**
 *
 *
 * <h1>商品微服务相关服务接口定义</h1>
 */
public interface IGoodsService {

  /**
   *
   *
   * <h2>根据tableIdVo 查询商品详细信息</h2>
   */
  List<GoodsVo> getGoodsVoListByTableIdVoList(TableIdVoList tableIdVoList);

  /**
   *
   *
   * <h2>获取分页的商品信息</h2>
   */
  PageSimpleGoodsInfo getSimpleGoodsInfoByPage(int page);

  /**
   *
   *
   * <h2>根据 TableIdVo 查询简单商品信息</h2>
   */
  List<SimpleGoodsVo> getSimpleGoodsVoListByTableIdVo(TableIdVoList tableIdVoList);

  /**
   *
   *
   * <h2>扣减商品库存</h2>
   */
  Boolean deductGoodsInventory(List<DeductGoodsInventory> deductGoodsInventories);
}
