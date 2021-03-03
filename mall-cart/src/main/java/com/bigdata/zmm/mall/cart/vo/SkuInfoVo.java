package com.bigdata.zmm.mall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Name SkuInfoVo
 * @Author Administrator
 * @Date By 2021-03-03 21:07:12
 */
@Data
public class SkuInfoVo {

    /**
     * $column.comments
     */
    private Long skuId;
    /**
     * $column.comments
     */
    private Long spuId;
    /**
     * $column.comments
     */
    private String skuName;
    /**
     * $column.comments
     */
    private String skuDesc;
    /**
     * $column.comments
     */
    private Long catalogId;
    /**
     * $column.comments
     */
    private Long brandId;
    /**
     * $column.comments
     */
    private String skuDefaultImg;
    /**
     * $column.comments
     */
    private String skuTitle;
    /**
     * $column.comments
     */
    private String skuSubtitle;
    /**
     * $column.comments
     */
    private BigDecimal price;
    /**
     * $column.comments
     */
    private Long saleCount;
}
