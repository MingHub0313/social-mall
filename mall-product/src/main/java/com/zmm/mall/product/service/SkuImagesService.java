package com.zmm.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    /**
     * 查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据 skuId 获取 sku图片的信息
     * @author: 900045
     * @date: 2021-02-23 16:07:29
     * @throws 
     * @param skuId: 
     * @return: java.util.List<com.zmm.mall.product.entity.SkuImagesEntity>
     **/
	List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

