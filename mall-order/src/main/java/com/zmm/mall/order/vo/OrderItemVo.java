package com.zmm.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Name OrderItemVo
 * @Author Administrator
 * @Date By 2021-03-08 22:05:48
 */
@Data
public class OrderItemVo {

    /**
     * skuId
     */
    private Long skuId;

    /**
     * 商品是否被选中
     */
    private boolean check;

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

}
