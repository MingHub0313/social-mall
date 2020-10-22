package com.zmm.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
public interface MemberService extends IService<MemberEntity> {

    /**
     * 查询 会员
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

