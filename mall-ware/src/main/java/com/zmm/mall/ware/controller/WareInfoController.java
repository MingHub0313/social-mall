package com.zmm.mall.ware.controller;

import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.R;
import com.zmm.mall.ware.entity.WareInfoEntity;
import com.zmm.mall.ware.service.WareInfoService;
import com.zmm.mall.ware.vo.FareResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 仓库信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 15:20:08
 */
@RestController
@RequestMapping("ware/wareinfo")
public class WareInfoController {
    @Autowired
    private WareInfoService wareInfoService;

    /**
     * 获取运费
     * @description:
     * @author: Administrator
     * @date: 2021-03-09 21:33:08
     * @param addrId: 
     * @return: com.zmm.common.utils.R
     **/
    @GetMapping("/fare")
    public R getFare(@RequestParam("addrId") Long addrId){
        FareResponseVo fareResponseVo = wareInfoService.getFare(addrId);
        return R.ok().setData(fareResponseVo);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareInfoEntity wareInfo = wareInfoService.getById(id);

        return R.ok().put("wareInfo", wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.save(wareInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.updateById(wareInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
