package com.mlr.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.mlr.ecommerce.common.TableIdVo;
import com.mlr.ecommerce.common.TableIdVoList;
import com.mlr.ecommerce.constant.GoodsConstant;
import com.mlr.ecommerce.entity.EcommerceGoods;
import com.mlr.ecommerce.goods.DeductGoodsInventory;
import com.mlr.ecommerce.goods.GoodsVo;
import com.mlr.ecommerce.goods.SimpleGoodsVo;
import com.mlr.ecommerce.repository.EcommerceGoodsRepository;
import com.mlr.ecommerce.service.IGoodsService;
import com.mlr.ecommerce.vo.PageSimpleGoodsInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 *
 * <h1>商品微服务相关服务功能的实现</h1>
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsServiceImpl implements IGoodsService {

  private final EcommerceGoodsRepository ecommerceGoodsRepository;
  private final StringRedisTemplate stringRedisTemplate;

  public GoodsServiceImpl(
      EcommerceGoodsRepository ecommerceGoodsRepository, StringRedisTemplate stringRedisTemplate) {
    this.ecommerceGoodsRepository = ecommerceGoodsRepository;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  /**
   *
   *
   * <h2>根据 tableIdVoList 查询商品详细信息</h2>
   *
   * @param tableIdVoList
   */
  @Override
  public List<GoodsVo> getGoodsVoListByTableIdVoList(TableIdVoList tableIdVoList) {
    //    详细的商品信息不能从 redis cache 中去拿
    List<Long> ids =
        tableIdVoList.getTableIdVoList().stream()
            .map(TableIdVo::getId)
            .collect(Collectors.toList());
    log.info("get GoodsVoList by ids: {}", ids);
    List<EcommerceGoods> ecommerceGoods =
        IterableUtils.toList(ecommerceGoodsRepository.findAllById(ids));
    return ecommerceGoods.stream().map(EcommerceGoods::toGoodsVo).collect(Collectors.toList());
  }

  /**
   *
   *
   * <h2>获取分页的商品信息</h2>
   *
   * @param page
   */
  @Override
  public PageSimpleGoodsInfo getSimpleGoodsInfoByPage(int page) {
    //    分页不能从 redis cache 中去拿
    if (page <= 1) {
      page = 1;
    }

    //    这里分页的规则(你可以自由修改) 一页 10 条数据, 按照 id 倒序排列
    Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("id").descending());
    Page<EcommerceGoods> ecommerceGoodsPage = ecommerceGoodsRepository.findAll(pageable);
    //    是否还有更多页，总页数是否大于给定的页数
    boolean hasMore = ecommerceGoodsPage.getTotalPages() > page;
    List<SimpleGoodsVo> simpleGoodsVos =
        ecommerceGoodsPage.getContent().stream()
            .map(EcommerceGoods::toSimpleGoodsVo)
            .collect(Collectors.toList());
    return PageSimpleGoodsInfo.builder().simpleGoodsVos(simpleGoodsVos).hasMore(hasMore).build();
  }

  /**
   *
   *
   * <h2>根据 TableIdVoList 查询简单商品信息</h2>
   *
   * @param tableIdVoList
   */
  @Override
  public List<SimpleGoodsVo> getSimpleGoodsVoListByTableIdVo(TableIdVoList tableIdVoList) {
    // 获取简单商品的信息，可以从 redis cache 中直接去拿，如果拿不到的话需要从数据库里面去拿，并且保存到 Redis 里面
    //    Redis 中的 KV 都是字符串类型的
    List<Object> ids =
        tableIdVoList.getTableIdVoList().stream()
            .map(TableIdVo::getId)
            .collect(Collectors.toList());
    //    fixme 如果 cache 中查不到 ids 对应的数据，返回的是 null， [null, null]
    List<Object> cachedSimpleGoodsVoList =
        stringRedisTemplate
            .opsForHash()
            .multiGet(
                GoodsConstant.ECOMMERCE_GOODS_DICT_KEY,
                ids.stream().map(Object::toString).collect(Collectors.toList()))
            .stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    log.info("cachedSimpleGoodsVoList: {}", JSON.toJSONString(cachedSimpleGoodsVoList));
    //    如果从 Redis中查询到了商品信息，分两种情况去操作
    if (CollectionUtils.isNotEmpty(cachedSimpleGoodsVoList)) {
      //      1. 如果从缓存中查询出所有需要的 SimpleGoodsVo
      if (cachedSimpleGoodsVoList.size() == ids.size()) {
        log.info("get simple goods info by ids (from cache) {}", JSON.toJSONString(ids));
        return parseCachedSimpleGoodsVoList(cachedSimpleGoodsVoList);
      }
      //      2, 有一部分来自数据库(right), 有一部分来自缓存 Redis(left)
      List<SimpleGoodsVo> left = parseCachedSimpleGoodsVoList(cachedSimpleGoodsVoList);
      //       取差集： 传递进来的参数 - 缓存中查到的 = 缓存中没有的(需要从数据库里面拿)
      Collection<Long> subtract =
          CollectionUtils.subtract(
              ids.stream().map(id -> Long.valueOf(id.toString())).collect(Collectors.toList()),
              left.stream().map(SimpleGoodsVo::getId).collect(Collectors.toList()));
      //       从数据库里去查询缓存中没有的数据
      List<SimpleGoodsVo> right =
          queryGoodsVoListFromDBAndCacheToRedis(
              TableIdVoList.builder()
                  .tableIdVoList(subtract.stream().map(TableIdVo::new).collect(Collectors.toList()))
                  .build());
      //       合并 left 和 right
      log.info("get simple goods info by ids (from db and cache): {}", JSON.toJSONString(subtract));
      return new ArrayList<>(CollectionUtils.union(left, right));
    } else {
      //      从 Redis 里面什么都没有查询到
      return queryGoodsVoListFromDBAndCacheToRedis(tableIdVoList);
    }
  }

  /**
   *
   *
   * <h2>将缓冲中的数据反序列化成 Java Pojo 对象</h2>
   */
  private List<SimpleGoodsVo> parseCachedSimpleGoodsVoList(List<Object> cachedSimpleGoodsVoList) {
    return cachedSimpleGoodsVoList.stream()
        .map(s -> JSON.parseObject(String.valueOf(s), SimpleGoodsVo.class))
        .collect(Collectors.toList());
  }

  /**
   *
   *
   * <h2>从数据表中查询数据， 并缓存到 Redis 中</h2>
   */
  private List<SimpleGoodsVo> queryGoodsVoListFromDBAndCacheToRedis(TableIdVoList tableIdVoList) {
    //    从数据表中查询数据并且做转换
    List<Long> ids =
        tableIdVoList.getTableIdVoList().stream()
            .map(TableIdVo::getId)
            .collect(Collectors.toList());
    log.info("get SimpleGoodsVoList by ids(from db): {}", JSON.toJSONString(ids));
    List<EcommerceGoods> ecommerceGoodsList =
        IterableUtils.toList(ecommerceGoodsRepository.findAllById(ids));
    List<SimpleGoodsVo> simpleGoodsVoList =
        ecommerceGoodsList.stream()
            .map(EcommerceGoods::toSimpleGoodsVo)
            .collect(Collectors.toList());
    //    将从数据库里面查询出来的结果缓存到 redis 中
    log.info("cache goods info: {}", JSON.toJSONString(ids));
    Map<String, String> id2JsonObject = new HashMap<>(simpleGoodsVoList.size());
    simpleGoodsVoList.forEach(
        simpleGoodsVo ->
            id2JsonObject.put(
                String.valueOf(simpleGoodsVo.getId()), JSON.toJSONString(simpleGoodsVo)));
    //    保存到 Redis 中
    stringRedisTemplate.opsForHash().putAll(GoodsConstant.ECOMMERCE_GOODS_DICT_KEY, id2JsonObject);
    return simpleGoodsVoList;
  }

  /**
   *
   *
   * <h2>扣减商品库存</h2>
   *
   * @param deductGoodsInventories
   */
  @Override
  public Boolean deductGoodsInventory(List<DeductGoodsInventory> deductGoodsInventories) {

    //    在 service 层面， 每次在要用参数或者数据之前， 都要检查你这个数据或者参数是否合法
    deductGoodsInventories.forEach(
        deductGoodsInventory -> {
          if (deductGoodsInventory.getCount() < 0) {
            log.info("purchase goods count need >0");
          }
        });
    List<EcommerceGoods> ecommerceGoods =
        IterableUtils.toList(
            ecommerceGoodsRepository.findAllById(
                deductGoodsInventories.stream()
                    .map(DeductGoodsInventory::getGoodsId)
                    .collect(Collectors.toList())));
    //    根据传递的 goodsId 查询不到商品对象，抛异常
    if (CollectionUtils.isEmpty(ecommerceGoods)) {
      throw new RuntimeException("can not find any goods by request");
    }

    //    查询出来的商品数量也传递的不一致，抛异常
    if (ecommerceGoods.size() != deductGoodsInventories.size()) {
      throw new RuntimeException("request is not valid");
    }

    //    goodsId -> DeductGoodsInventory
    Map<Long, DeductGoodsInventory> goodsId2Inventory =
        deductGoodsInventories.stream()
            .collect(Collectors.toMap(DeductGoodsInventory::getGoodsId, Function.identity()));

    //    检查是不是可以扣减库存, 再减库存
    ecommerceGoods.forEach(
        g -> {
          Long currentInventory = g.getInventory();

          Integer needDeductCount = goodsId2Inventory.get(g.getId()).getCount();
          if (currentInventory < needDeductCount) {
            log.info(
                "goods inventory is not enough: current: {}, needDeduct {}",
                currentInventory,
                needDeductCount);
            throw new RuntimeException("goods inventory is not enough: {}" + g.getId().toString());
          }
          //          扣减库存
          g.setInventory(currentInventory - needDeductCount);
        });

    ecommerceGoodsRepository.saveAll(ecommerceGoods);
    log.info("deduct goods inventory done");
    return true;
  }
}
