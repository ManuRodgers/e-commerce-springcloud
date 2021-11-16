package com.mlr.ecommerce.repository;

import com.mlr.ecommerce.constant.BrandCategory;
import com.mlr.ecommerce.constant.GoodsCategory;
import com.mlr.ecommerce.entity.EcommerceGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 *
 * <h1>EcommerceGoods Repository 接口定义</h1>
 *
 * @author manurodgers
 */
@Repository
public interface EcommerceGoodsRepository extends JpaRepository<EcommerceGoods, Long> {
  /**
   *
   *
   * <h2>searching for EcommerceGoods based on specific criteria and restrict the search results
   * </h2>
   */
  @Query(
      "select e from EcommerceGoods e where e.goodsCategory = ?1 and e.brandCategory = ?2 and e.goodsName = ?3")
  Optional<EcommerceGoods> findFirst1ByGoodsCategoryAndBrandCategoryAndGoodsName(
      GoodsCategory goodsCategory, BrandCategory brandCategory, String goodsName);
}
