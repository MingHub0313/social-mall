package com.zmm.mall.member.controller;

import com.zmm.common.base.model.ReqResult;
import com.zmm.common.base.model.RespCode;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.exception.BusinessException;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.R;
import com.zmm.mall.member.entity.MemberEntity;
import com.zmm.mall.member.feign.CouponFeign;
import com.zmm.mall.member.service.MemberService;
import com.zmm.mall.member.vo.MemberLoginVo;
import com.zmm.mall.member.vo.MemberRegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;



/**
 * 会员
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
@Slf4j
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeign couponFeign;

    @RequestMapping("/coupons")
    public R test(){

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        R memberCoupons = couponFeign.memberCoupons();
        return R.ok().put("member", memberEntity).put("coupons",memberCoupons.get("coupons"));
    }


    @PostMapping("/login")
    public ReqResult login(@RequestBody MemberLoginVo vo){
        MemberEntity memberEntity = memberService.login(vo);
        if (ObjectUtils.isEmpty(memberEntity)){
            return new ReqResult(ResultCode.LOGIN_ACCT_PASSWORD_INVALID_ERROR);
        } else {
            return new ReqResult(memberEntity);
        }
        
    }
    /**
     * 注册
     */
    @PostMapping(value = "/register")
    public ReqResult register(@RequestBody MemberRegisterVo vo){
        try {
            memberService.register(vo);
        } catch (BusinessException e) {
            log.error("register error :"+ e);
            RespCode code = e.getCode();
            String message = e.getMessage();
            return new ReqResult(code,message);

        }
        return new ReqResult(ResultCode.SUCCESS);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
