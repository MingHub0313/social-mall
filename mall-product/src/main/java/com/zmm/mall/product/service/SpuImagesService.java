package com.zmm.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.product.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

	/**
	 * 查询
	 * @param params
	 * @return
	 */
    PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存图片
	 * @param spu_id
	 * @param images
	 */
	void saveImages(Long spu_id, List<String> images);
}

