package com.zmm.mall.member.feign;

import com.zmm.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Name CouponFeign
 * @Author 900045
 * @Created by 2020/8/22 0022
 */
@FeignClient(name = "mall-coupon",url = "127.0.0.1:88/api")
public interface CouponFeign {

	@RequestMapping("/coupon/coupon/member/list")
	public R memberCoupons();

}
