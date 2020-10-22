package com.zmm.mall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.zmm.common.constant.ProductConstant;
import com.zmm.common.to.SkuHasStockVo;
import com.zmm.common.to.SkuReductionTo;
import com.zmm.common.to.SpuBoundTo;
import com.zmm.common.to.es.SkuEsModel;
import com.zmm.common.utils.R;
import com.zmm.mall.product.entity.AttrEntity;
import com.zmm.mall.product.entity.BrandEntity;
import com.zmm.mall.product.entity.CategoryEntity;
import com.zmm.mall.product.entity.ProductAttrValueEntity;
import com.zmm.mall.product.entity.SkuImagesEntity;
import com.zmm.mall.product.entity.SkuInfoEntity;
import com.zmm.mall.product.entity.SkuSaleAttrValueEntity;
import com.zmm.mall.product.entity.SpuInfoDescEntity;
import com.zmm.mall.product.feign.CouponFeignService;
import com.zmm.mall.product.feign.SearchFeignService;
import com.zmm.mall.product.feign.WareFeignService;
import com.zmm.mall.product.service.AttrService;
import com.zmm.mall.product.service.BrandService;
import com.zmm.mall.product.service.CategoryService;
import com.zmm.mall.product.service.ProductAttrValueService;
import com.zmm.mall.product.service.SkuImagesService;
import com.zmm.mall.product.service.SkuInfoService;
import com.zmm.mall.product.service.SkuSaleAttrValueService;
import com.zmm.mall.product.service.SpuImagesService;
import com.zmm.mall.product.service.SpuInfoDescService;
import com.zmm.mall.product.vo.Attr;
import com.zmm.mall.product.vo.BaseAttrs;
import com.zmm.mall.product.vo.Bounds;
import com.zmm.mall.product.vo.Images;
import com.zmm.mall.product.vo.Skus;
import com.zmm.mall.product.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;

import com.zmm.mall.product.dao.SpuInfoDao;
import com.zmm.mall.product.entity.SpuInfoEntity;
import com.zmm.mall.product.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * spu信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SpuInfoService spuInfoService;
    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        // 1.保存商品基本信息  pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        // 2.保存spu 描述图片 pms_spu_info_desc
        List<String> desc = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",desc));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);
        // 3.保存spu的图片集 pms_spu_images
        List<String> imagesList = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),imagesList);

        // 4.保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;

        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);
        // 远程保存
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode() != 0){
            log.error("远程保存spu积分信息失败");
        }

        // 5.保存sku集合
        List<Skus> skuList = vo.getSkus();
        if (skuList != null && skuList.size() > 0){
            skuList.forEach(item->{
                String defaultImg = "";
                for(Images images: item.getImages()){
                    if (images.getDefaultImg() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
                        defaultImg = images.getImgUrl();
                    }
                }
                // 5.1) sku基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSkuId(spuInfoEntity.getId());

                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();

                // 5.2) sku的图片信息 pms_sku_images
                List<SkuImagesEntity> imagesEntityList = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(img->{
                    //返回true就是需要，false就是剔除
                    return !StringUtils.isEmpty(img.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntityList);

                // 5.3) sku的销售属性 pms_sku_sale_attr_value
                List<Attr> attrList = item.getAttr();
                List<SkuSaleAttrValueEntity> saleAttrValueEntityList = attrList.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(saleAttrValueEntityList);

                // 5.4) sku的优惠 满减 mall_sms 库
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount() >0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) == 1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode() != 0){
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public void up(Long spuId) {
        // 1.组装需要的数据
        // 1.查出当前 spuId 对应的所有sku信息 品牌名称
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkuBySpuId(spuId);
        // 4
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIdList = productAttrValueEntities.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
       List<Long> searchAttrIdList = attrService.selectSearchAttrs(attrIdList);
        Set<Long> idSet = new HashSet<>(searchAttrIdList);
        List<SkuEsModel.Attrs> attrsList = productAttrValueEntities.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs);
            return attrs;
        }).collect(Collectors.toList());

        List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        Map<Long, Boolean> stockMap = null;
        try {
            R skuHasStock = wareFeignService.getSkuHasStock(skuIdList);
            // 内部类
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>(){};

            stockMap = skuHasStock.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId,
                    item -> item.getHasStock()));
        }catch (Exception e){
            log.error("库存服务查询异常-->{}",e);
        }
        // 2.封装每个sku信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> collect = skuInfoEntities.stream().map(sku -> {
            SkuEsModel  skuEsModels = new SkuEsModel();
            BeanUtils.copyProperties(sku,skuEsModels);
            // 对比不同的字段 skuPrice skuImg hasStock hasScore brandName brandImg catalogName  List<Attrs> attrs
            skuEsModels.setSkuPrice(sku.getPrice());
            skuEsModels.setSkuImg(sku.getSkuDefaultImg());
            // hasStock hasScore TODO 1.发送远程调用 查询 库存系统是否有库存
            // 设置库存信息
            if (finalStockMap == null){
                skuEsModels.setHasStock(true);
            }else {
                skuEsModels.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            // hasScore TODO 2.热度评分 默认 0
            skuEsModels.setHasScore(0L);
            // 3. brandName brandImg catalogName
            BrandEntity brandEntity = brandService.getById(skuEsModels.getBrandId());
            skuEsModels.setBrandName(brandEntity.getName());
            skuEsModels.setBrandImg(brandEntity.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(skuEsModels.getCatalogId());
            skuEsModels.setCatalogName(categoryEntity.getName());

            // 4.查询当前sku所有可以被用来检索的规格属性 查询一遍 放外面
            //设置检索属性
            skuEsModels.setAttrs(attrsList);

            return skuEsModels;
        }).collect(Collectors.toList());

        // 5,发送 es 进行保存 mall-search
        R r = searchFeignService.productStatusUp(collect);
        if (r.getCode() == 0){
            // 远程调用成功
            // TODO 修改当前spu状态
            baseMapper.updateSpuStatus(spuId,ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            //远程调用失败
            // TODO 重复调用 ? 接口幂等性 重试机制?
        }

    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        // status=1 and (id=1 or spu_name like xxx)
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String catalogId = (String) params.get("catelog_id");
        if(!StringUtils.isEmpty(catalogId)&&!"0".equalsIgnoreCase(catalogId)){
            wrapper.eq("catelog_id",catalogId);
        }

        /**
         * status: 2
         * key:
         * brandId: 9
         * catelogId: 225
         */

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }
}
