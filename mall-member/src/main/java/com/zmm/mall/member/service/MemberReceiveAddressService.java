package com.zmm.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    /**
     * 查询 会员收货地址
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据 memberId 获取用户下的全部地址列表
     * @description:
     * @author: Administrator
     * @date: 2021-03-08 22:16:58
     * @param memberId :
     * @return: java.util.List<com.zmm.mall.member.entity.MemberReceiveAddressEntity>
     **/
    List<MemberReceiveAddressEntity> getAddress(Long memberId);
}

