package com.mlr.ecommerce.service.async;

import com.alibaba.fastjson.JSON;
import com.mlr.ecommerce.constant.GoodsConstant;
import com.mlr.ecommerce.entity.EcommerceGoods;
import com.mlr.ecommerce.goods.GoodsVo;
import com.mlr.ecommerce.goods.SimpleGoodsVo;
import com.mlr.ecommerce.repository.EcommerceGoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 *
 * <h1>异步服务接口实现</h1>
 *
 * @author manurodgers
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AsyncServiceImpl implements IAsyncService {
  private final EcommerceGoodsRepository ecommerceGoodsRepository;
  private final StringRedisTemplate stringRedisTemplate;

  public AsyncServiceImpl(
      EcommerceGoodsRepository ecommerceGoodsRepository, StringRedisTemplate stringRedisTemplate) {
    this.ecommerceGoodsRepository = ecommerceGoodsRepository;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  /**
   *
   *
   * <h2>Async tasks need to add annotation and to specify the using thread pool.</h2>
   *
   * Two things that need to be done by async task 1, storing the goods in the database 2. updating
   * the goods cache in redis
   */
  @Override
  @Async("getAsyncExecutor")
  public void asyncImportGoods(List<GoodsVo> goodsVos, String taskId) {
    log.info("async task running taskId: {}", taskId);
    StopWatch stopWatch = new StopWatch();
    //    1. If there is duplicate goods in the goods list, then do not save and directly returns
    // recoding the error log
    //    the flag for request data
    boolean isLegal = true;
    //    将商品信息字段 joint 在一起， 用来判断是否存在重复
    //    Use of Set<Object> when you have to judge whether there are duplicate data or not
    Set<String> jointGoodsVos = new HashSet<>(goodsVos.size());
    //    过虐出来的， 可以入库的商品信息(规则按照自己的业务需求自定义即可)
    List<GoodsVo> filteredGoodsVos = new ArrayList<>(goodsVos.size());
    //    做一篇循环，过虐非法参数与判定当前请求是否合法
    for (GoodsVo goodsVo : goodsVos) {
      //      基本条件不满足的， 直接过滤器
      if (goodsVo.getPrice() <= 0 || goodsVo.getSupply() <= 0) {
        log.info("goods data is invalid: {}", JSON.toJSONString(goodsVo));
        continue;
      }
      //      组合商品信息
      String jointGoodsVo =
          String.format(
              "%s,%s,%s",
              goodsVo.getGoodsCategory(), goodsVo.getBrandCategory(), goodsVo.getGoodsName());
      log.info("jointGoodsVo: {}", jointGoodsVo);
      if (jointGoodsVos.contains(jointGoodsVo)) {
        isLegal = false;
      }

      jointGoodsVos.add(jointGoodsVo);
      filteredGoodsVos.add(goodsVo);
    }
    //    如果存在重复的商品或者是没有需要入库的商品，直接打印错误日志并且返回
    if (!isLegal || CollectionUtils.isEmpty(filteredGoodsVos)) {
      stopWatch.stop();
      log.warn("import nothing: {}", JSON.toJSONString(filteredGoodsVos));
      log.info("check and import goods done: {}ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
      return;
    }
    List<EcommerceGoods> ecommerceGoods =
        filteredGoodsVos.stream()
            .map(EcommerceGoods::toEcommerceGoods)
            .collect(Collectors.toList());
    List<EcommerceGoods> targetGoods = new ArrayList<>(ecommerceGoods.size());
    //    2. 保存 goodsVo 之前判断下是否存在重复的商品
    ecommerceGoods.forEach(
        g -> {
          // To retrieve existing goods from database making sure that there are no duplicate goods
          // in the database. and obviously limit 1
          if (null
              != ecommerceGoodsRepository
                  .findFirst1ByGoodsCategoryAndBrandCategoryAndGoodsName(
                      g.getGoodsCategory(), g.getBrandCategory(), g.getGoodsName())
                  .orElse(null)) {
            return;
          }
          targetGoods.add(g);
        });
    //    商品信息入库
    List<EcommerceGoods> newSavedEcommerceGoods =
        IterableUtils.toList(ecommerceGoodsRepository.saveAll(targetGoods));
    saveNewGoodsToRedis(newSavedEcommerceGoods);
    log.info("save goods to db and redis: {}", newSavedEcommerceGoods.size());
    stopWatch.stop();
    log.info("check and import goods success: {}ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
  }

  /**
   *
   *
   * <h2>将保存到数据表中的数据缓存到 Redis 中</h2>
   *
   * dict: key -> <id, SimpleGoodsInfo(json)>
   */
  private void saveNewGoodsToRedis(List<EcommerceGoods> newSavedEcommerceGoods) {
    // Because the Redis is memory storage kind of database system, so it only stores the simple
    // goods info
    List<SimpleGoodsVo> simpleGoodsVos =
        newSavedEcommerceGoods.stream()
            .map(EcommerceGoods::toSimpleGoodsVo)
            .collect(Collectors.toList());
    Map<String, String> id2JsonObject = new HashMap<>(simpleGoodsVos.size());
    simpleGoodsVos.forEach(
        simpleGoodsVo ->
            id2JsonObject.put(simpleGoodsVo.getId().toString(), JSON.toJSONString(simpleGoodsVo)));

    // store id2JsonObject in Redis
    stringRedisTemplate.opsForHash().putAll(GoodsConstant.ECOMMERCE_GOODS_DICT_KEY, id2JsonObject);
  }
}
