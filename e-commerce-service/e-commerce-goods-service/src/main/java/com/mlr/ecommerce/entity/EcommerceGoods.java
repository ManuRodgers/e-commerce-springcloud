package com.mlr.ecommerce.entity;

import com.alibaba.fastjson.JSON;
import com.mlr.ecommerce.constant.BrandCategory;
import com.mlr.ecommerce.constant.GoodsCategory;
import com.mlr.ecommerce.constant.GoodsStatus;
import com.mlr.ecommerce.converter.BrandCategoryConverter;
import com.mlr.ecommerce.converter.GoodsCategoryConverter;
import com.mlr.ecommerce.converter.GoodsStatusConverter;
import com.mlr.ecommerce.goods.GoodsProperty;
import com.mlr.ecommerce.goods.GoodsVo;
import com.mlr.ecommerce.goods.SimpleGoodsVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 *
 * <h1>商品表实体类定义</h1>
 *
 * @author manurodgers
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "t_ecommerce_goods")
public class EcommerceGoods implements Serializable {
  /** 自增主键 */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  /** 商品分类 */
  @Column(name = "goods_category", nullable = false)
  @Convert(converter = GoodsCategoryConverter.class)
  private GoodsCategory goodsCategory;

  /** 品牌分类 */
  @Column(name = "brand_category", nullable = false)
  @Convert(converter = BrandCategoryConverter.class)
  private BrandCategory brandCategory;

  /** 商品名称 */
  @Column(name = "goods_name", nullable = false)
  private String goodsName;

  /** goods picture */
  @Column(name = "goods_pic", nullable = false)
  private String goodsPic;

  /** goods description */
  @Column(name = "goods_description", nullable = false)
  private String goodsDescription;

  /** goods status */
  @Column(name = "goods_status", nullable = false)
  @Convert(converter = GoodsStatusConverter.class)
  private GoodsStatus goodsStatus;

  /** goods price */
  @Column(name = "price", nullable = false)
  private Integer price;

  /** total supply */
  @Column(name = "supply", nullable = false)
  private Long supply;

  /** inventory */
  @Column(name = "inventory", nullable = false)
  private Long inventory;

  /** goods properties store in json string format */
  @Column(name = "goods_property", nullable = false)
  private String goodsProperty;

  /** create time */
  @CreatedDate
  @Column(name = "create_time", nullable = false)
  private Date createTime;

  /** update time */
  @LastModifiedDate
  @Column(name = "update_time", nullable = false)
  private Date updateTime;

  /**
   *
   *
   * <h2>将 GoodsVo 转成实体对象</h2>
   */
  public static EcommerceGoods toEcommerceGoods(GoodsVo goodsVo) {

    return EcommerceGoods.builder()
        .goodsCategory(GoodsCategory.of(goodsVo.getGoodsCategory()))
        .brandCategory(BrandCategory.of(goodsVo.getBrandCategory()))
        .goodsName(goodsVo.getGoodsName())
        .goodsPic(goodsVo.getGoodsPic())
        .goodsDescription(goodsVo.getGoodsDescription())
        .goodsStatus(GoodsStatus.ONLINE)
        .price(goodsVo.getPrice())
        .supply(goodsVo.getSupply())
        .inventory(goodsVo.getSupply())
        .goodsProperty(JSON.toJSONString(goodsVo.getGoodsProperty()))
        .build();
  }

  /**
   *
   *
   * <h2>将实体对象转成 GoodsVo 对象</h2>
   */
  public GoodsVo toGoodsVo() {
    return GoodsVo.builder()
        .id(this.id)
        .goodsCategory(this.goodsCategory.getCode())
        .brandCategory(this.brandCategory.getCode())
        .goodsName(this.goodsName)
        .goodsPic(this.goodsPic)
        .goodsDescription(this.goodsDescription)
        .goodsStatus(this.goodsStatus.getStatusCode())
        .price(this.price)
        .goodsProperty(JSON.parseObject(this.goodsProperty, GoodsProperty.class))
        .supply(this.supply)
        .inventory(this.inventory)
        .createTime(this.createTime)
        .updateTime(this.updateTime)
        .build();
  }

  /**
   *
   *
   * <h2>将实体对象转成 SimpleGoodsVo 对象</h2>
   */
  public SimpleGoodsVo toSimpleGoodsVo() {
    return SimpleGoodsVo.builder()
        .id(this.id)
        .goodsName(this.goodsName)
        .goodsPic(this.goodsPic)
        .price(this.price)
        .build();
  }
}
