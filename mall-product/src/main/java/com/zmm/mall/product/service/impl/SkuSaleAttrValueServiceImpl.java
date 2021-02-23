package com.zmm.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.mall.product.dao.SkuSaleAttrValueDao;
import com.zmm.mall.product.entity.SkuSaleAttrValueEntity;
import com.zmm.mall.product.service.SkuSaleAttrValueService;
import com.zmm.mall.product.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrsBySpId(Long spuId) {
        SkuSaleAttrValueDao skuSaleAttrValueDao = this.baseMapper;
        List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueDao.getSaleAttrsBySpId(spuId);
        return skuItemSaleAttrVos;
    }
}
