package com.zmm.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.member.entity.WmsWareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
public interface WmsWareOrderTaskService extends IService<WmsWareOrderTaskEntity> {

    /**
     * 查询 库存工作单
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

