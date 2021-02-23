package com.zmm.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.mall.product.dao.AttrGroupDao;
import com.zmm.mall.product.entity.AttrEntity;
import com.zmm.mall.product.entity.AttrGroupEntity;
import com.zmm.mall.product.service.AttrGroupService;
import com.zmm.mall.product.service.AttrService;
import com.zmm.mall.product.vo.AttrGroupWithAttrsVo;
import com.zmm.mall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 属性分组
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long cateId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<AttrGroupEntity>();
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and( item ->{
                item.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }
        if (cateId == 0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),queryWrapper);
            return new PageUtils(page);
        } else {
            queryWrapper.eq("catelog_id",cateId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),queryWrapper);
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCateLogId(Long cateLogId) {
        // 1.查询分组信息
        List<AttrGroupEntity> attrGroupEntityList = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cateLogId));

        // 2.查询所有属性
        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVoList = attrGroupEntityList.stream().map(item -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item, attrGroupWithAttrsVo);
            List<AttrEntity> attrEntities = attrService.getRelationAttr(attrGroupWithAttrsVo.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(attrEntities);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());
        return attrGroupWithAttrsVoList;
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long catalogId,Long spuId) {
        // 查出当前 spuId 对应的所有属性的分组信息以及当期分组下的所有属性的值
        // 1.当前 spu 有多少对应的属性分组 (根据三级分类 catalog_id) 
        // pro.`spu_id`,ag.`attr_group_name`,ag.`attr_group_id`,aar.`attr_id`,attr.`attr_name`,pro.`attr_value` from pms_attr_group ag 
        // left join pms_attrgroup_relation aar on aar.`attr_group_id` = ag.`attr_group_id` 
        // left join pms_attr attr on attr.`attr_id` = aar.`attr_id`
        // left join pms_product_attr_value pro on pro.`attr_id` = attr.`attr_id`
        // where ag.catalog_id = 225 AND pro.spu_id = 13

        AttrGroupDao attrGroupDao = this.baseMapper;
        List<SpuItemAttrGroupVo> spuItemAttrGroupVos = attrGroupDao.getAttrGroupWithAttrsBySpuId(catalogId,spuId);
        return spuItemAttrGroupVos;
    }
}
