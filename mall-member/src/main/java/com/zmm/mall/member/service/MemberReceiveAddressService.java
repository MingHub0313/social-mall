package com.zmm.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.member.entity.MemberReceiveAddressEntity;

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
}

