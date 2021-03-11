package com.zmm.mall.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Name SpuInfoVo
 * @Author Administrator
 * @Date By 2021-03-11 22:05:40
 */
@Data
public class SpuInfoVo implements Serializable {

    private Long id;
    /**
     * $column.comments
     */
    private String spuName;
    /**
     * $column.comments
     */
    private String spuDescription;
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
    private BigDecimal weight;
    /**
     * $column.comments
     */
    private Integer publishStatus;
    /**
     * $column.comments
     */
    private Date createTime;
    /**
     * $column.comments
     */
    private Date updateTime;
}
