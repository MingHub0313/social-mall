package com.zmm.mall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.constant.NumberConstant;
import com.zmm.common.constant.RedisTimeOutConstant;
import com.zmm.common.exception.BusinessException;
import com.zmm.common.utils.HttpUtils;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.common.utils.redis.RedisUtil;
import com.zmm.common.utils.redis.key.CommonKey;
import com.zmm.common.vo.MemberRespVo;
import com.zmm.mall.member.dao.MemberDao;
import com.zmm.mall.member.dao.MemberLevelDao;
import com.zmm.mall.member.entity.MemberEntity;
import com.zmm.mall.member.entity.MemberLevelEntity;
import com.zmm.mall.member.service.MemberService;
import com.zmm.mall.member.vo.MemberLoginVo;
import com.zmm.mall.member.vo.MemberRegisterVo;
import com.zmm.mall.member.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 会员
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 11:38:56
 */
@Slf4j
@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    
    @Resource
    private MemberLevelDao memberLevelDao;
    
    @Resource
    private RedisUtil redisUtil;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo vo) throws BusinessException {
        MemberEntity memberEntity = new MemberEntity();
        MemberDao memberDao = this.baseMapper;
        // memberEntity.setId() 主键 
        // 设置默认等级
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());
        // 检查用户名和手机号是否唯一 为了让 controller 能感知异常 使用异常机制
        
        checkPhoneUnique(vo.getPhone());
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());
        checkUserNameUnique(vo.getUserName());


        // 盐值加密: 随机值 {$1$+位字符}
        /**
         * 如何验证: 用户传入的 密码: 123456 再次进行盐值加密 得到的字符串 到数据库进行比较
         * 1.即 数据库中要保存 加的 盐. 淘汰
         * 2.使用 BCryptPasswordEncoder
         */
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);

        // 默认用户昵称
        memberEntity.setNickname(vo.getUserName());
        memberDao.insert(memberEntity);
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        MemberDao memberDao = this.baseMapper;
        // 1.根据 登录账号 查询 数据库 是否存在
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("username", vo.getLoginAccount()).or().eq("mobile", vo.getLoginAccount()));
        if (ObjectUtils.isEmpty(memberEntity)){
            // 1.1 不存在 登录失败
            log.error("获取用户信息为空,账号:{}",vo.getLoginAccount());
            return null;
        } else {
            // 1.2 如果存在
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // 2.进行密码匹配
            boolean matches = passwordEncoder.matches(vo.getPassword(), memberEntity.getPassword());
            if (matches){
                // 2.1 匹配成功 登录成功
                log.error("登录成功,账号:{}",vo.getLoginAccount());
                generateToken(memberEntity);
                return memberEntity;
            } else {
                log.error("密码匹配失败,账号:{}",vo.getLoginAccount());
                // 2.2 匹配失败 登录失败
                return null;
            }
        }
    }
    
    
    /**
     * 将用户的认证信息写入缓存
     * @author: 900045
     * @date: 2021-03-25 09:26:45
     * @throws 
     * @param memberEntity: 
     * @return: void
     **/
    private void generateToken(MemberEntity memberEntity){
        long currentTimeMillis = System.currentTimeMillis();
        String tokenVal = UUID.randomUUID().toString();
        long tokenExpires = currentTimeMillis + RedisTimeOutConstant.S_86400 * 10 * 1000;
        redisUtil.set(CommonKey.AUTH_TOKEN_USER_PREFIX.setSuffix(tokenVal), memberEntity.getId(),
                tokenExpires, TimeUnit.MILLISECONDS);
        MemberRespVo memberRespVo = new MemberRespVo();
        BeanUtils.copyProperties(memberEntity,memberRespVo);
        memberRespVo.setAccessToken(tokenVal);
        redisUtil.hash(CommonKey.AUTH_USER_KEY, memberEntity.getId(), memberRespVo);
        
    }

    @Override
    public MemberEntity login(SocialUser socialUser) {
        // 登录和注册合并逻辑
        String uid = socialUser.getUid();
        String access_token = socialUser.getAccess_token();
        String expires_in = socialUser.getExpires_in();
        // 1.根据 social_uid 查询 当前 MemberEntity 是否已经登录过系统
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (!ObjectUtils.isEmpty(memberEntity)){
            // 1.1这个用户已经在系统中注册过 -->更新 social_token / expires_in
            MemberEntity updateEntity = new MemberEntity();
            updateEntity.setId(memberEntity.getId());
            updateEntity.setAccessToken(access_token);
            updateEntity.setExpiresIn(expires_in);
            memberDao.updateById(updateEntity);

            memberEntity.setAccessToken(access_token);
            memberEntity.setExpiresIn(expires_in);
            //返回新数据
            return memberEntity;
        } else {
            // 1.2没有查询到 当前社交用户对应的 social_uid --->进行注册
            MemberEntity register = new MemberEntity();
            
            // 2.查询当前社交用户的账号信息(昵称/性别)
            Map<String,String> map = new HashMap<>();
            try {
                // 获取 用户信息即使异常 也不应该影响 用户注册
                map.put("uid",uid);
                map.put("access_token",access_token);
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "2/users/show.json", "get", new HashMap<>(), map);
                if (response.getStatusLine().getStatusCode() == NumberConstant.TWO_HUNDRED) {
                    // 查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    String profile_image_url = jsonObject.getString("profile_image_url");
                    String location = jsonObject.getString("location");
                    String description = jsonObject.getString("description");
                    // ...
                    register.setNickname(name);
                    register.setGender("m".equals(gender) ? 1 : 0);
                    register.setHeader(profile_image_url);
                    register.setCity(location);
                    register.setSign(description);
                }
            } catch (Exception e){
                log.error("获取用户信息失败---->失败信息:{}",e);
            }
            register.setSourceType(1);
            register.setSocialUid(uid);
            register.setAccessToken(access_token);
            register.setExpiresIn(expires_in);
            memberDao.insert(register);
            return register;
        }
    }

    @Override
    public void checkPhoneUnique(String phone) throws BusinessException {
        MemberDao memberDao = this.baseMapper;
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if ( count > 0 ) {
            // throw new PhoneExistException()
            throw new BusinessException(ResultCode.PHONE_NOT_UNIQUE);
        }
        

    }

    @Override
    public void checkUserNameUnique(String userName) throws BusinessException {
        MemberDao memberDao = this.baseMapper;
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if ( count > 0 ) {
            // throw new UserNameExistException()
            throw new BusinessException(ResultCode.USERNAME_NOT_UNIQUE);
        }
    }
}
