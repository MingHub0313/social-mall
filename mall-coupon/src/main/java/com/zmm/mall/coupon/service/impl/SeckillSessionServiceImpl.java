package com.zmm.mall.coupon.service.impl;

import com.zmm.mall.coupon.entity.SecKillSkuRelationEntity;
import com.zmm.mall.coupon.service.SeckillSkuRelationService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;

import com.zmm.mall.coupon.dao.SecKillSessionDao;
import com.zmm.mall.coupon.entity.SecKillSessionEntity;
import com.zmm.mall.coupon.service.SeckillSessionService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

/**
 * 秒杀活动场次
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:33:14
 */
@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SecKillSessionDao, SecKillSessionEntity> implements SeckillSessionService {
    
    @Resource
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SecKillSessionEntity> page = this.page(
                new Query<SecKillSessionEntity>().getPage(params),
                new QueryWrapper<SecKillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SecKillSessionEntity> getLatest3DaySession() {
        // 计算最近的三天时间
        // 2021-04-16 16:05:53

        List<SecKillSessionEntity> list = this.list(new QueryWrapper<SecKillSessionEntity>().between("start_time", startTime(), endTime()));
        if (!CollectionUtils.isEmpty(list)){
            List<SecKillSessionEntity> collect = list.stream().map(session -> {
                Long sessionId = session.getId();
                List<SecKillSkuRelationEntity> relationEntityList = seckillSkuRelationService.list(new QueryWrapper<SecKillSkuRelationEntity>().eq("promotion_session_id", sessionId));
                session.setRelationSkuList(relationEntityList);
                return session;
            }).collect(Collectors.toList());
            return collect;
        }
        return list;
    }
    
    private String startTime(){
        LocalDate now = LocalDate.now();
        // 00:00
        LocalTime min = LocalTime.MIN;
        LocalDateTime start = LocalDateTime.of(now, min);
        String format = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }

    private String endTime(){
        LocalDate now = LocalDate.now();
        LocalDate plus2 = now.plusDays(3);
        LocalTime max = LocalTime.MAX;
        LocalDateTime end = LocalDateTime.of(plus2, max);
        String format = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }
}
