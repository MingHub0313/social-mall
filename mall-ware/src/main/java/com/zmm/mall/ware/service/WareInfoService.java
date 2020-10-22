package com.zmm.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.ware.entity.WareInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 15:20:08
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    /**
     * 查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

