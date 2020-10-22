package com.zmm.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.member.entity.WmsWareInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
public interface WmsWareInfoService extends IService<WmsWareInfoEntity> {

    /**
     * 查询 仓库信息
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

