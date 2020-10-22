package com.zmm.mall.product.dao;

import com.zmm.mall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

	/**
	 * 修改商品的状态
	 * @param spuId
	 * @param code
	 */
	void updateSpuStatus(@Param("spuId") Long spuId,@Param("code") int code);
}
