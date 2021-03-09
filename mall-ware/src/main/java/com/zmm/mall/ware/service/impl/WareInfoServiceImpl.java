package com.zmm.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.common.utils.R;
import com.zmm.mall.ware.dao.WareInfoDao;
import com.zmm.mall.ware.entity.WareInfoEntity;
import com.zmm.mall.ware.feign.MemberFeignService;
import com.zmm.mall.ware.service.WareInfoService;
import com.zmm.mall.ware.vo.FareResponseVo;
import com.zmm.mall.ware.vo.MemberAddressVo;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 15:20:08
 */
@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Resource
    private MemberFeignService memberFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wareInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wareInfoEntityQueryWrapper.eq("id",key).or()
                    .like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wareInfoEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareResponseVo getFare(Long addrId) {
        FareResponseVo fareResponseVo = new FareResponseVo();
        R r = memberFeignService.addrInfo(addrId);
        MemberAddressVo memberAddressVo = r.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>() {
        });
        if (!ObjectUtils.isEmpty(memberAddressVo)){
            // 假数据
            String phone = memberAddressVo.getPhone();
            // 此处是假取 手机的号的最后两个数字
            String substring = phone.substring(phone.length() - 1, phone.length());
            BigDecimal fare = new BigDecimal(substring);
            fareResponseVo.setFare(fare);
            fareResponseVo.setMemberAddressVo(memberAddressVo);
            return fareResponseVo;
        }
        return null;
    }
}
