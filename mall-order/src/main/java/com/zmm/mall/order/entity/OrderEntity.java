package com.zmm.mall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单
 * 
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:50:05
 */
@Data
@TableName("oms_order")
public class OrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * $column.comments
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * $column.comments
	 */
	private Long memberId;
	/**
	 * $column.comments
	 */
	private String orderSn;
	/**
	 * $column.comments
	 */
	private Long couponId;
	/**
	 * $column.comments
	 */
	private Date createTime;
	/**
	 * $column.comments
	 */
	private String memberUsername;
	/**
	 * $column.comments
	 */
	private BigDecimal totalAmount;
	/**
	 * $column.comments
	 */
	private BigDecimal payAmount;
	/**
	 * $column.comments
	 */
	private BigDecimal freightAmount;
	/**
	 * $column.comments
	 */
	private BigDecimal promotionAmount;
	/**
	 * $column.comments
	 */
	private BigDecimal integrationAmount;
	/**
	 * $column.comments
	 */
	private BigDecimal couponAmount;
	/**
	 * $column.comments
	 */
	private BigDecimal discountAmount;
	/**
	 * $column.comments
	 */
	private Integer payType;
	/**
	 * $column.comments
	 */
	private Integer sourceType;
	/**
	 * $column.comments
	 */
	private Integer status;
	/**
	 * $column.comments
	 */
	private String deliveryCompany;
	/**
	 * $column.comments
	 */
	private String deliverySn;
	/**
	 * $column.comments
	 */
	private Integer autoConfirmDay;
	/**
	 * $column.comments
	 */
	private Integer integration;
	/**
	 * $column.comments
	 */
	private Integer growth;
	/**
	 * $column.comments
	 */
	private Integer billType;
	/**
	 * $column.comments
	 */
	private String billHeader;
	/**
	 * $column.comments
	 */
	private String billContent;
	/**
	 * $column.comments
	 */
	private String billReceiverPhone;
	/**
	 * $column.comments
	 */
	private String billReceiverEmail;
	/**
	 * $column.comments
	 */
	private String receiverName;
	/**
	 * $column.comments
	 */
	private String receiverPhone;
	/**
	 * $column.comments
	 */
	private String receiverPostCode;
	/**
	 * $column.comments
	 */
	private String receiverProvince;
	/**
	 * $column.comments
	 */
	private String receiverCity;
	/**
	 * $column.comments
	 */
	private String receiverRegion;
	/**
	 * $column.comments
	 */
	private String receiverDetailAddress;
	/**
	 * $column.comments
	 */
	private String note;
	/**
	 * $column.comments
	 */
	private Integer confirmStatus;
	/**
	 * $column.comments
	 */
	private Integer deleteStatus;
	/**
	 * $column.comments
	 */
	private Integer useIntegration;
	/**
	 * $column.comments
	 */
	private Date paymentTime;
	/**
	 * $column.comments
	 */
	private Date deliveryTime;
	/**
	 * $column.comments
	 */
	private Date receiveTime;
	/**
	 * $column.comments
	 */
	private Date commentTime;
	/**
	 * $column.comments
	 */
	private Date modifyTime;
	
	@TableField(exist = false)
	private List<OrderItemEntity> itemEntityList;

}
