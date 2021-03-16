package com.zmm.common.exception;

/**
 * 没有库存的异常
 * @Description:
 * @Name NoStockException
 * @Author Administrator
 * @Date By 2021-03-16 20:28:16
 */
public class NoStockException extends RuntimeException{

    private Long skuId;
    public NoStockException(Long skuId) {
        super("商品id:[+"+skuId+"],没有库存了");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
