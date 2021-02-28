package com.bigdata.zmm.mall.cart.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Name Cart
 * @Author Administrator
 * @Date By 2021-02-28 22:13:24
 */
@Data
public class Cart {

    List<CartItem> cartItems;

    /**
     * 商品数量
     */
    private Integer countNumber;

    /**
     * 商品类型
     */
    private Integer countType;

    /**
     * 商品总价
     */
    private BigDecimal totalAmount;

    /**
     * 减免价格
     */
    private BigDecimal reduce = BigDecimal.ZERO;

    /**
     * 自定义
     * @return
     */
    public Integer getCountNumber() {
        int count = 0;
        if (!CollectionUtils.isEmpty(cartItems)){
            for (CartItem cartItem : cartItems){
                count += cartItem.getCount();
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = BigDecimal.ZERO;
        // 1.计算购物项总价
        if (!CollectionUtils.isEmpty(cartItems)){
            for (CartItem cartItem : cartItems){
                BigDecimal totalPrice = cartItem.getTotalPrice();
                amount = amount.add(totalPrice);
            }
        }
        amount.subtract(this.reduce);
        return amount;
    }
}
