package com.zmm.mall.product.app.controller;

import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.R;
import com.zmm.mall.product.entity.SkuInfoEntity;
import com.zmm.mall.product.service.SkuInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;


/**
 * sku信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Resource
    private SkuInfoService skuInfoService;


    /**
     * 根据skuId 获取商品系统中的商品价格
     * @description:
     * @author: Administrator
     * @date: 2021-03-08 22:39:42
     * @param skuId: 
     * @return: java.math.BigDecimal
     **/
    @GetMapping("/{skuId}/price")
    public R getPrice(@PathVariable("skuId") Long skuId){
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        return R.ok().setData(skuInfoEntity.getPrice().toString());
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
