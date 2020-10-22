package com.zmm.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.member.entity.GrowthChangeHistoryEntity;

import java.util.Map;

/**
 * 成长值变化历史记录
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

    /**
     * 查询 成长值变化历史记录
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

