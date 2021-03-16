package com.zmm.mall.ware.vo;

/**
 * 库存锁定结果
 * @Description:
 * @Name LockStockResult
 * @Author Administrator
 * @Date By 2021-03-16 20:06:52
 */
public class LockStockResult {

    /**
     * 锁住的 skuId
     */
    private Long skuId;

    /**
     * 锁住的 数量
     */
    private Integer num;

    /**
     * 锁住的结果
     */
    private boolean locked;
}
