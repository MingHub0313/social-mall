package com.zmm.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Name SkuWareHasStock
 * @Author Administrator
 * @Date By 2021-03-16 20:19:33
 */
@Data
public class SkuWareHasStock {

    /**
     * 商品的 skuId
     */
    private Long skuId;

    /**
     * 锁定几件
     */
    private Integer num;

    /**
     * 仓库地址的集合
     */
    private List<Long> wareId;
}
