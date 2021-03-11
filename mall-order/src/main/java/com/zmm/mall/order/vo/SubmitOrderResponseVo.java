package com.zmm.mall.order.vo;

import com.zmm.mall.order.entity.OrderEntity;
import lombok.Data;

/**
 * 下单返回数据
 * @Description:
 * @Name SubmitOrderResponseVo
 * @Author Administrator
 * @Date By 2021-03-11 20:49:36
 */
@Data
public class SubmitOrderResponseVo {


    private OrderEntity orderEntity;

    /**
     * 状态码 0 : 错误
     */
    private Integer code;


}
