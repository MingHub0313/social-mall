package com.zmm.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    /**
     * 查询 会员等级
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

