package com.zmm.mall.product.app.controller;

import java.util.Arrays;

import java.util.Map;

import com.zmm.common.valid.AddGroup;
import com.zmm.common.valid.UpdateGroup;
import com.zmm.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zmm.mall.product.entity.BrandEntity;
import com.zmm.mall.product.service.BrandService;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.R;



/**
 * 品牌
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand){
        /**
         *
         * if (result.hasErrors()){
            Map<String,String> map = new HashMap<>()
            // 1.获取校验的错误结果
            result.getFieldErrors().forEach( item->{
                // FiledError 获取到错误提示
                String message = item.getDefaultMessage()
                map.put("message",message)
                // 获取错误的属性名称
                String field = item.getField()
                map.put("field",field)
            })
            return R.error(400,"提交的数据不合法").put("data",map)
        } else {

        } */

        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
		//brandService.updateById(brand)
        // 第一次修改 在修改品牌表中的数据时 需要更新关联的分类表(因为冗余字段 方便查询)
        brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    public R updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
