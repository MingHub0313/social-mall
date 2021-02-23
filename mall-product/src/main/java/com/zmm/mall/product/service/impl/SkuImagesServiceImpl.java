package com.zmm.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.mall.product.dao.SkuImagesDao;
import com.zmm.mall.product.entity.SkuImagesEntity;
import com.zmm.mall.product.service.SkuImagesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuImagesEntity> getImagesBySkuId(Long skuId) {
        SkuImagesDao skuImagesDao = this.baseMapper;
        List<SkuImagesEntity> skuImagesEntities = skuImagesDao.selectList(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
        return skuImagesEntities;
    }
}
