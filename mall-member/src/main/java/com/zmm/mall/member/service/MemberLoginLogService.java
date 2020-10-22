package com.zmm.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    /**
     * 查询 会员登录记录
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

