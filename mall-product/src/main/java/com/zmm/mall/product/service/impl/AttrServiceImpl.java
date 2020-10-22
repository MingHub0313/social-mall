package com.zmm.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zmm.common.constant.ProductConstant;
import com.zmm.mall.product.dao.AttrAttrGroupRelationDao;
import com.zmm.mall.product.dao.AttrGroupDao;
import com.zmm.mall.product.dao.CategoryDao;
import com.zmm.mall.product.entity.AttrAttrGroupRelationEntity;
import com.zmm.mall.product.entity.AttrGroupEntity;
import com.zmm.mall.product.entity.CategoryEntity;
import com.zmm.mall.product.service.CategoryService;
import com.zmm.mall.product.vo.AttrGroupRelationVo;
import com.zmm.mall.product.vo.AttrRespVo;
import com.zmm.mall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;

import com.zmm.mall.product.dao.AttrDao;
import com.zmm.mall.product.entity.AttrEntity;
import com.zmm.mall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


/**
 * 商品属性
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrGroupRelationDao attrAttrGroupRelationDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        //attrEntity.setAttrName(attrEntity.getAttrName())
        BeanUtils.copyProperties(attrVo,attrEntity);
        // 1.保存基本数据
        this.save(attrEntity);

        // 2.保存关联关系
        if (attrVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attrVo.getAttrGroupId() != null){
            AttrAttrGroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrGroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrGroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long cateId, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type","base".equalsIgnoreCase(attrType)
                        ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode():ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

        if (cateId != 0){
            queryWrapper.eq("catelog_id",cateId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and(wrapper->{
                wrapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),queryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> attrRespVoList = records.stream().map(attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            // 1.设置分组和分组的名称
            if ("base".equalsIgnoreCase(attrType)){
                AttrAttrGroupRelationEntity attrAttrgroupRelationEntity =
                        attrAttrGroupRelationDao.selectOne(
                                new QueryWrapper<AttrAttrGroupRelationEntity>().eq("attr_id", attrEntity.getAttrId())
                        );
                if (!ObjectUtils.isEmpty(attrAttrgroupRelationEntity) &&  attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                    if (!ObjectUtils.isEmpty(attrAttrgroupRelationEntity)) {
                        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                        attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
                }
            }

            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (!ObjectUtils.isEmpty(categoryEntity)) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }

            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(attrRespVoList);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,attrRespVo);
        AttrAttrGroupRelationEntity attrAttrgroupRelationEntity = attrAttrGroupRelationDao.selectOne(
                new QueryWrapper<AttrAttrGroupRelationEntity>().eq("attr_id", attrId));

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            // 1.设置分组信息
            if (!ObjectUtils.isEmpty(attrAttrgroupRelationEntity)){
                attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (!ObjectUtils.isEmpty(attrGroupEntity)){
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());

                }
            }
        }

        // 2.设置分类信息
        Long cateLogId = attrEntity.getCatelogId();
        Long[] cateLogPath = categoryService.findCateLogPath(cateLogId);
        attrRespVo.setCatelogPath(cateLogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(cateLogId);
        if (!ObjectUtils.isEmpty(categoryEntity)){
            attrRespVo.setCatelogName(categoryEntity.getName());
        }

        return attrRespVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo,attrEntity);
        this.updateById(attrEntity);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            // 1.修改分组关联
            AttrAttrGroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrGroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrVo.getAttrId());

            Integer count = attrAttrGroupRelationDao.selectCount(
                    new QueryWrapper<AttrAttrGroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
            if (count>0){
                attrAttrGroupRelationDao.update(attrAttrgroupRelationEntity,
                        new UpdateWrapper<AttrAttrGroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
            } else {
                attrAttrGroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }
    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrGroupId) {
        List<AttrAttrGroupRelationEntity> attrgroupRelationEntityList = attrAttrGroupRelationDao.selectList(new QueryWrapper<AttrAttrGroupRelationEntity>().eq("attr_group_id", attrGroupId));
        List<Long> attrIds = attrgroupRelationEntityList.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        if (attrGroupId == null || attrIds.size() == 0){
            return null;
        }

        List<AttrEntity> entities = this.listByIds(attrIds);
        return entities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVos) {
        //attrAttrGroupRelationDao.delete(new QueryWrapper<>().eq("attr_id",1L).eq("attr_group_id",1L))
        List<AttrAttrGroupRelationEntity> attrAttrGroupRelationEntityList = Arrays.asList(attrGroupRelationVos).stream().map(item -> {
            AttrAttrGroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrGroupRelationEntity();
            BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        attrAttrGroupRelationDao.deleteBatchRelation(attrAttrGroupRelationEntityList);
    }

    @Override
    public List<Long> selectSearchAttrs(List<Long> attrIdList) {

        return baseMapper.selectSearchAttrs(attrIdList);
    }

    @Override
    public PageUtils getRelationNoAttr(Map<String, Object> params, Long attrGroupId) {
        // 1.当前分组只能关联自己所属分类里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
        Long cateLogId = attrGroupEntity.getCatelogId();
        // 2.当前分组只能关联别的分组没有引用的属性
        // 2.1当前分类下的其他分组
        List<AttrGroupEntity> attrGroupEntityList = attrGroupDao.selectList(
                new QueryWrapper<AttrGroupEntity>().
                        eq("catelog_id", cateLogId));
        List<Long> collect = attrGroupEntityList.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());
        // 2.2这些分组关联的属性
        List<AttrAttrGroupRelationEntity> attrAttrGroupRelationEntities =
                attrAttrGroupRelationDao.selectList(new QueryWrapper<AttrAttrGroupRelationEntity>().in("attr_group_id", collect));
        List<Long> attrIds = attrAttrGroupRelationEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        // 2.3从当前分类的所有属性中移除这些属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", cateLogId).eq("attr_type",
                ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!CollectionUtils.isEmpty(attrIds) && attrIds.size() >0){
            queryWrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and( (w) ->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }
}
