package com.zmm.mall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.common.utils.R;
import com.zmm.mall.product.dao.SkuInfoDao;
import com.zmm.mall.product.entity.SkuImagesEntity;
import com.zmm.mall.product.entity.SkuInfoEntity;
import com.zmm.mall.product.entity.SpuInfoDescEntity;
import com.zmm.mall.product.feign.SeckillFeignService;
import com.zmm.mall.product.service.AttrGroupService;
import com.zmm.mall.product.service.SkuImagesService;
import com.zmm.mall.product.service.SkuInfoService;
import com.zmm.mall.product.service.SkuSaleAttrValueService;
import com.zmm.mall.product.service.SpuInfoDescService;
import com.zmm.mall.product.vo.SeckillInfoVo;
import com.zmm.mall.product.vo.SkuItemSaleAttrVo;
import com.zmm.mall.product.vo.SkuItemVo;
import com.zmm.mall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * sku信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Slf4j
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    
    @Autowired
    private SkuImagesService skuImagesService;
    
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    
    @Autowired
    private AttrGroupService attrGroupService;
    
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    
    @Resource
    private SeckillFeignService seckillFeignService;
    
    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
        return list;
    }

    @Override
    public SkuItemVo item(Long skuId) {

        SkuItemVo skuItemVo = null;
        try {
            // 使用异步方法 执行的查询
            skuItemVo = useCompletableFuture(skuId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return skuItemVo;
    }
    
    /**
     * 改造方法走 异步
     * @author: 900045
     * @date: 2021-02-23 17:49:58
     * @throws 
     * @param skuId: 
     * @return: com.zmm.mall.product.vo.SkuItemVo
     **/
    public  SkuItemVo useCompletableFuture(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();
        // 1.
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfoEntity = getSkuInfoEntity(skuId, skuItemVo);
            return skuInfoEntity;
        }, executor);

        // 3.
        CompletableFuture<Void> attrFuture = skuInfoFuture.thenAcceptAsync((skuInfoEntity) -> {
            getSkuItemSaleAttrVos(skuInfoEntity.getSpuId(),skuItemVo);
        }, executor);

        // 4.
        CompletableFuture<Void> descFuture = skuInfoFuture.thenAcceptAsync((skuInfoEntity) -> {
            getSpuInfoDescEntity(skuInfoEntity.getSpuId(),skuItemVo);
        }, executor);

        // 5.
        CompletableFuture<Void> groupAttrFuture = skuInfoFuture.thenAcceptAsync((skuInfoEntity) -> {
            getSpuItemAttrGroupVos(skuInfoEntity.getCatalogId(), skuInfoEntity.getSpuId(),skuItemVo);
        }, executor);

        // 2.
        CompletableFuture<Void> completableFutureImg = CompletableFuture.runAsync(() -> {
             getSkuImagesEntities(skuId,skuItemVo);
        }, executor);
        
        
        // 6.查询当前sku是否参与秒杀活动
        CompletableFuture<Void> completableFutureSeckill = CompletableFuture.runAsync(() -> { 
            R r = seckillFeignService.getSkuSeckillInfo(skuId);
            if ( r.getCode() == 0){ 
                SeckillInfoVo data = r.getData(new TypeReference<SeckillInfoVo>() {
                });
                skuItemVo.setSeckillInfoVo(data); 
            }
        }, executor);
       
        
        // 等待所有任务都完成
        CompletableFuture.allOf(attrFuture, descFuture, groupAttrFuture, completableFutureImg,completableFutureSeckill).get();
        
        return skuItemVo; 
    }

    private SkuItemVo packageSkuItemVo(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        SkuInfoEntity skuInfoEntity = getSkuInfoEntity(skuId,skuItemVo);
        
        Long spuId = skuInfoEntity.getSpuId();
        // 2.sku图片信息
        getSkuImagesEntities(skuId,skuItemVo);
        
        // 3.获取 spu 的销售属性组合
        getSkuItemSaleAttrVos(spuId,skuItemVo);
       
        // 4.获取 spu 的介绍

        getSpuInfoDescEntity(spuId,skuItemVo);

        // 5.获取 spu的规格参数信息
        getSpuItemAttrGroupVos(skuInfoEntity.getCatalogId(), spuId,skuItemVo);
        
        return skuItemVo;
    }

    private void getSkuImagesEntities(Long skuId,SkuItemVo skuItemVo) {
        List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
        skuItemVo.setImages(images);
    }

    private void getSpuItemAttrGroupVos(Long catalogId, Long spuId,SkuItemVo skuItemVo) {
        List<SpuItemAttrGroupVo> groupAttrs = attrGroupService.getAttrGroupWithAttrsBySpuId(catalogId, spuId);
        skuItemVo.setGroupAttrs(groupAttrs);
    }

    private void getSpuInfoDescEntity(Long spuId,SkuItemVo skuItemVo) {
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
        skuItemVo.setSpuInfoDescEntity(spuInfoDescEntity);
    }

    private void getSkuItemSaleAttrVos(Long spuId,SkuItemVo skuItemVo) {
        List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpId(spuId);
        skuItemVo.setSaleAttr(saleAttrVos);
    }

    private SkuInfoEntity getSkuInfoEntity(Long skuId,SkuItemVo skuItemVo) {
        // 1.获取 sku 基本信息
        SkuInfoEntity skuInfoEntity = getById(skuId);
        skuItemVo.setSkuInfoEntity(skuInfoEntity);
        return skuInfoEntity;
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("sku_id",key).or().like("sku_name",key);
            });
        }

        String catalogId = (String) params.get("catelog_id");
        if(!StringUtils.isEmpty(catalogId)&&!"0".equalsIgnoreCase(catalogId)){

            queryWrapper.eq("catelog_id",catalogId);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(catalogId)){
            queryWrapper.eq("brand_id",brandId);
        }

        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            queryWrapper.ge("price",min);
        }

        String max = (String) params.get("max");

        if(!StringUtils.isEmpty(max)  ){
            try{
                BigDecimal bigDecimal = new BigDecimal(max);

                if(bigDecimal.compareTo(new BigDecimal("0"))==1){
                    queryWrapper.le("price",max);
                }
            }catch (Exception e){

            }

        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }
}
