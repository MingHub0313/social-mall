package com.zmm.mall.order.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmm.common.auth.AuthParam;
import com.zmm.common.base.model.ReqResult;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.constant.AuthConstant;
import com.zmm.common.utils.IpUtil;
import com.zmm.common.utils.redis.RedisUtil;
import com.zmm.common.utils.redis.key.CommonKey;
import com.zmm.common.vo.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登陆检测
 * @Description:
 * @Name LoginUserInterceptor
 * @Author Administrator
 * @Date By 2021-03-08 21:35:32
 */
@Slf4j
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    protected Logger logger   = LoggerFactory.getLogger(this.getClass());
    
    @Resource
    private RedisUtil redisUtil;
    
    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    /**
     * 在目标方法执行之前
     * @author: Administrator
     * @date: 2021-03-08 21:35:56
     * @param request: 
     * @param response: 
     * @param handler: 
     * @return: boolean
     **/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        log.error("进来了~~~~~~~~");
        // 指定路径放行
        String requestURI = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/order/order/status/**", requestURI);
        boolean match2 = antPathMatcher.match("/pay/notify",requestURI);
        boolean match3 = antPathMatcher.match("/order/verity/code",requestURI);
        if (match || match2 || match3) {
            return true;
        }
        String token = request.getHeader(AuthConstant.HEADER_TOKEN_KEY);
        AuthParam param = new AuthParam(request, response);
        param.setIpAddress(IpUtil.getIpAddress(request));
        // 2.从 redis 中获取
        Object key = getUserFromRedis(token);
        if (key == null){
            return returnResp(param.getResponse(), new ReqResult<String>(ResultCode.USER_NOT_LOGIN));
        }
        // 3.获取当前用户
        Object object = redisUtil.hash(CommonKey.AUTH_USER_KEY, key.toString());
        if (object == null) {
            logger.error("AUTH-GET-USER null: {}", key);
            return returnResp(param.getResponse(), new ReqResult<String>(ResultCode.USER_NOT_LOGIN));
        }
        MemberRespVo memberRespVo = JSONObject.parseObject(JSON.toJSONString(object),MemberRespVo.class);
        loginUser.set(memberRespVo);
        return true;
        /**
        Object attribute = request.getSession().getAttribute(StringConstant.LOGIN_USER);
        if (ObjectUtils.isEmpty(attribute)){
            // 没有登陆就去登陆 重定向到登陆页面
            request.getSession().setAttribute("msg","请先登陆");
            response.sendRedirect("http://zmm.mall.com/login.html");
            return false;
        } else {
            // 放行
            MemberRespVo memberRespVo = (MemberRespVo) attribute;
            loginUser.set(memberRespVo);
            return true;
        }*/
    }

    public Object getUserFromRedis(String token){

        // 判断参数是否为空
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        // 从redis中获取 此token对应用的用户 然后保存在当前线程中
        Object key = redisUtil.get(CommonKey.AUTH_TOKEN_USER_PREFIX.setSuffix(token));
        if (key == null) {
            return null;
        }
        return key;
    }

    /**
     *  响应异常
     *
     * @param response
     * @param result
     * @throws Exception
     */
    protected boolean returnResp(HttpServletResponse response,
                                 ReqResult<String> result) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        logger.info("REQ-INTERCEPTOR-BASE:{}", result);
        try (PrintWriter writer = response.getWriter();) {
            writer.print(JSON.toJSONString(result));
            writer.flush();
        } catch (IOException e) {
            logger.error("response error", e);
        }
        return false;
    }
}
