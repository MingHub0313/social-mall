package com.zmm.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.common.vo.MemberRespVo;
import com.zmm.mall.order.dao.OrderDao;
import com.zmm.mall.order.entity.OrderEntity;
import com.zmm.mall.order.feign.CartFeignService;
import com.zmm.mall.order.feign.MemberFeignService;
import com.zmm.mall.order.interceptor.LoginUserInterceptor;
import com.zmm.mall.order.service.OrderService;
import com.zmm.mall.order.vo.MemberAddressVo;
import com.zmm.mall.order.vo.OrderConfirmVo;
import com.zmm.mall.order.vo.OrderItemVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Resource
    private MemberFeignService memberFeignService;

    @Resource
    private CartFeignService cartFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        // 1.远程查询所有的收货地址列表
        List<MemberAddressVo> memberAddressVoList = memberFeignService.getAddress(memberRespVo.getId());
        // 2.远程查询购物车所有选中的购物项
        List<OrderItemVo> orderItemVoList = cartFeignService.getCartItemByUser();


        // 3.查询用户积分
        orderConfirmVo.setOrderItemVoList(orderItemVoList);
        orderConfirmVo.setMemberAddressVoList(memberAddressVoList);
        orderConfirmVo.setIntegration(memberRespVo.getIntegration());

        // 4.其他数据自动计算

        // 5.TODO 防重令牌

        return orderConfirmVo;
    }
}