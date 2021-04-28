package com.zmm.mall.product.app.controller;

import com.zmm.common.utils.R;
import com.zmm.mall.product.entity.CategoryEntity;
import com.zmm.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;



/**
 * 商品三级分类
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查出所有的分类以及子分类,以树形结构组装起来
     */
    @RequestMapping("/list/tree")
    public List<CategoryEntity> list(){
        List<CategoryEntity> entities = categoryService.listWithTree();

        return entities;
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);
        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity[] category){
        List<CategoryEntity> entities = Arrays.asList(category);
        categoryService.updateBatchById(entities);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category){
		//categoryService.updateById(category)
        categoryService.updateCascade(category);

        return R.ok();
    }

    /**
     * 删除
     * RequestBody : 获取请求体 必须发送POST请求
     * SpringMVC 自动将请求体的数据(json) 装为对应的对象
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds){
        // 1.检查当前删除的菜单,是否被别的地方引用

		//categoryService.removeByIds(Arrays.asList(catIds))

        categoryService.removeMenuByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
