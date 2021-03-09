package com.zmm.mall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.common.utils.R;
import com.zmm.common.vo.MemberRespVo;
import com.zmm.mall.order.dao.OrderDao;
import com.zmm.mall.order.entity.OrderEntity;
import com.zmm.mall.order.feign.CartFeignService;
import com.zmm.mall.order.feign.MemberFeignService;
import com.zmm.mall.order.feign.WareFeignService;
import com.zmm.mall.order.interceptor.LoginUserInterceptor;
import com.zmm.mall.order.service.OrderService;
import com.zmm.mall.order.vo.MemberAddressVo;
import com.zmm.mall.order.vo.OrderConfirmVo;
import com.zmm.mall.order.vo.OrderItemVo;
import com.zmm.mall.order.vo.SkuStockVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Resource
    private MemberFeignService memberFeignService;

    @Resource
    private CartFeignService cartFeignService;

    @Resource
    private ThreadPoolExecutor executor;

    @Resource
    private WareFeignService wareFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        // 让所有的线程都共享 这个 requestAttributes RequestContextHolder 中的线程的数据都不一样
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 使用异步 feign 会丢失上下文
        CompletableFuture<Void> futureAddressVoList = CompletableFuture.runAsync(() -> {
            // 1.远程查询所有的收货地址列表
            // 每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> memberAddressVoList = memberFeignService.getAddress(memberRespVo.getId());
            orderConfirmVo.setMemberAddressVoList(memberAddressVoList);
        }, executor);


        CompletableFuture<Void> futureItemVoList = CompletableFuture.runAsync(() -> {
            // 2.远程查询购物车所有选中的购物项
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> orderItemVoList = cartFeignService.getCartItemByUser();
            // feign 远程调用会丢失请求头的 ===> 解决方案:加上 feign 远程调用的请求拦截器
            // feign 在远程调用之前要构造请求,调用很多的拦截器 RequestInterceptor interceptor : requestInterceptors
            orderConfirmVo.setOrderItemVoList(orderItemVoList);
        }, executor).thenRunAsync(()->{
            // 上一步执行完之后 去库存服务查询 库存信息
            List<OrderItemVo> orderItemVoList = orderConfirmVo.getOrderItemVoList();
            List<Long> skuIdList = orderItemVoList.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R result = wareFeignService.getSkuHasStock(skuIdList);
            // 获取每一个 skuId 的库存信息
            List<SkuStockVo> stockVoList = result.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (!CollectionUtils.isEmpty(stockVoList)){
                Map<Long,Boolean> stocks = stockVoList.stream().collect(
                        Collectors.toMap(SkuStockVo::getSkuId,SkuStockVo::getHasStock));
                orderConfirmVo.setStocks(stocks);
            }
        },executor);


        // 3.查询用户积分
        orderConfirmVo.setIntegration(memberRespVo.getIntegration());

        CompletableFuture.allOf(futureAddressVoList,futureItemVoList).get();

        // 4.其他数据自动计算

        // 5.TODO 防重令牌 

        return orderConfirmVo;
    }
}