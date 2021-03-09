package com.zmm.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.ware.entity.WareInfoEntity;
import com.zmm.mall.ware.vo.FareResponseVo;

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

    /**
     * 根据用户收货地址计算运费
     * @description:
     * @author: Administrator
     * @date: 2021-03-09 21:59:33
     * @param addrId: 
     * @return: com.zmm.mall.ware.vo.FareResponseVo
     **/
    FareResponseVo getFare(Long addrId);
}

