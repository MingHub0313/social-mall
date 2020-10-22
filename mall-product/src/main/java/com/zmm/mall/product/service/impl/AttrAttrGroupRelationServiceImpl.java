package com.zmm.mall.product.service.impl;

import com.zmm.mall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;

import com.zmm.mall.product.dao.AttrAttrGroupRelationDao;
import com.zmm.mall.product.entity.AttrAttrGroupRelationEntity;
import com.zmm.mall.product.service.AttrAttrGroupRelationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 属性&属性分组关联
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Service("attrAttrGroupRelationService")
public class AttrAttrGroupRelationServiceImpl extends ServiceImpl<AttrAttrGroupRelationDao, AttrAttrGroupRelationEntity> implements AttrAttrGroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrGroupRelationEntity> page = this.page(
                new Query<AttrAttrGroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrGroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBatch(List<AttrGroupRelationVo> vos) {
        List<AttrAttrGroupRelationEntity> relationEntities = vos.stream().map(item -> {
            AttrAttrGroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrGroupRelationEntity();
            BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        this.saveBatch(relationEntities);
    }
}
