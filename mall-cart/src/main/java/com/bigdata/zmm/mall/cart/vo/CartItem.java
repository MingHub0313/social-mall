package com.bigdata.zmm.mall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 购物项内容
 * @Name CartItem
 * @Author Administrator
 * @Date By 2021-02-28 22:13:39
 */
@Data
public class CartItem{

    /**
     * skuId
     */
    private Long skuId;

    /**
     * 商品是否被选中
     */
    private boolean check=true;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片
     */
    private String image;

    private List<String> skuAttr;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 总价 -->需要自定义
     */
    private BigDecimal totalPrice;


    /**
     * 计算 当前项的总价
     * @return
     */
    public BigDecimal getTotalPrice() {
        this.totalPrice = this.price.multiply(new BigDecimal(String.valueOf(count)));
        return totalPrice;
    }
}
