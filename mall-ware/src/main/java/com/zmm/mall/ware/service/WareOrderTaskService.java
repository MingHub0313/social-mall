package com.zmm.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 15:20:08
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    /**
     * 查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据订单号 查询工作单的状态
     * @author: 900045
     * @date: 2021-03-24 15:21:12
     * @throws 
     * @param orderSn: 
     * @return: com.zmm.mall.ware.entity.WareOrderTaskEntity
     **/
	WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
	
}

