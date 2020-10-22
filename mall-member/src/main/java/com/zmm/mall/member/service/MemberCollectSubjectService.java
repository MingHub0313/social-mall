package com.zmm.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmm.common.utils.PageUtils;
import com.zmm.mall.member.entity.MemberCollectSubjectEntity;

import java.util.Map;

/**
 * 会员收藏的专题活动
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
public interface MemberCollectSubjectService extends IService<MemberCollectSubjectEntity> {

    /**
     *  查询 会员收藏的专题活动
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);
}

