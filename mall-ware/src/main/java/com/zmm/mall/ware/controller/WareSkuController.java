package com.zmm.mall.ware.controller;

import com.zmm.common.exception.BizCodeEnums;
import com.zmm.common.exception.NoStockException;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.R;
import com.zmm.mall.ware.entity.WareSkuEntity;
import com.zmm.mall.ware.service.WareSkuService;
import com.zmm.mall.ware.vo.SkuHasStockVo;
import com.zmm.mall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品库存
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 15:20:08
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;


    /**
     * 保存订单成功锁库存
     * @description:
     * @author: Administrator
     * @date: 2021-03-16 20:08:46
     * @param vo: 
     * @return: com.zmm.common.utils.R
     **/
    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo){
        try {
            boolean stock = wareSkuService.orderLockStock(vo);
            return R.ok();
        } catch (NoStockException e) {
            return R.error(BizCodeEnums.NO_STOCK_EXCEPTION.getCode(),BizCodeEnums.NO_STOCK_EXCEPTION.getMsg());
        }
    }

    @PostMapping("/has/stock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds){
        // sku_id , stock
        List<SkuHasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);

        return R.ok().setData(vos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
