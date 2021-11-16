package com.mlr.ecommerce.service.async;

import com.mlr.ecommerce.goods.GoodsVo;

import java.util.List;

/**
 *
 *
 * <h1>异步服务接口定义</h1>
 *
 * @author manurodgers
 */
public interface IAsyncService {
  /**
   *
   *
   * <h2>异步将商品信息保存下来</h2>
   */
  void asyncImportGoods(List<GoodsVo> goodsVos, String taskId);
}
