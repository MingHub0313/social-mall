package com.zmm.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.exception.BusinessException;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.mall.member.dao.MemberDao;
import com.zmm.mall.member.dao.MemberLevelDao;
import com.zmm.mall.member.entity.MemberEntity;
import com.zmm.mall.member.entity.MemberLevelEntity;
import com.zmm.mall.member.service.MemberService;
import com.zmm.mall.member.vo.MemberLoginVo;
import com.zmm.mall.member.vo.MemberRegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Map;

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
                return memberEntity;
            } else {
                log.error("密码匹配失败,账号:{}",vo.getLoginAccount());
                // 2.2 匹配失败 登录失败
                return null;
            }
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
