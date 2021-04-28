package com.bigdata.zmm.mall.cart.interceptor;

import com.bigdata.zmm.mall.cart.to.UserInfoTo;
import com.zmm.common.constant.AuthConstant;
import com.zmm.common.constant.CartConstant;
import com.zmm.common.constant.RedisTimeOutConstant;
import com.zmm.common.constant.StringConstant;
import com.zmm.common.utils.StringUtil;
import com.zmm.common.utils.redis.RedisUtil;
import com.zmm.common.utils.redis.key.CommonKey;
import com.zmm.common.vo.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 在执行目标方法之前. 判断用户的登陆状态 并封装传递给 controller 目标请求
 * @Description:
 * @Name CartInterceptor
 * @Author Administrator
 * @Date By 2021-02-28 23:18:40
 */
@Slf4j
@Configuration
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();
    
    @Resource
    private RedisUtil redisUtil;

    /**
     * 在目标方法执行之前
     * @description:
     * @author: Administrator
     * @date: 2021-02-28 23:21:03
     * @param request: 
     * @param response: 
     * @param handler: 
     * @return: boolean
     **/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        String token = request.getHeader(AuthConstant.HEADER_TOKEN_KEY);
        log.error("Cart token:[{}]",token);
        if (StringUtil.isNotBlank(token)) {
            Object key = redisUtil.get(CommonKey.AUTH_TOKEN_USER_PREFIX.setSuffix(token));
            Object object = redisUtil.hash(CommonKey.AUTH_USER_KEY, key.toString());
            if (object == null || !(object instanceof MemberRespVo)) {
                return false;
            }
            MemberRespVo memberRespVo = (MemberRespVo)object;
            userInfoTo.setUserId(memberRespVo.getId());
        }
        String cookie = request.getHeader(StringConstant.LOGIN_USER);
        if (StringUtil.isNotBlank(cookie)) {
            //Object o = redisUtil.get(CartKey.MALL_CART.setSuffix(cookie))
            userInfoTo.setUserKey(cookie);
            userInfoTo.setTempUser(true);
        }
        /**
        Object session = httpSession.getAttribute(StringConstant.LOGIN_USER)
        if (ObjectUtils.isEmpty(session)){
            // 没有登陆

        } else {
            MemberRespVo memberRespVo = (MemberRespVo)session
            userInfoTo.setUserId(memberRespVo.getId())
        }
        Cookie[] cookies = request.getCookies()
        if (!ObjectUtils.isEmpty(cookies)){
            for (Cookie c:cookies){
                // user-key
                String name = c.getName()
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    // 存在 临时用户
                    userInfoTo.setUserKey(c.getValue())
                    userInfoTo.setTempUser(true)
                }
            }
        }
         */
        // 如果没有临时用户 一定要分配一个临时用户
        if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }
        // 在目标方法执行之前
        threadLocal.set(userInfoTo);
        return true;
    }

    /**
     * 业务执行之后;分配临时用户,让浏览器保存
     * @description:
     * @author: Administrator
     * @date: 2021-02-28 23:44:40
     * @param request:
     * @param response:
     * @param handler:
     * @param modelAndView:
     * @return: void
     **/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        UserInfoTo userInfoTo = threadLocal.get();
        // 是临时的用户 就不需要执行 如果没有临时用户 就需要手动放入
        if (!userInfoTo.isTempUser()) {
            // 持续的延长临时用户的过期时间
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("mall.com");
            cookie.setMaxAge((int) RedisTimeOutConstant.S_2592000);
            response.addCookie(cookie);
        }
    }
}
