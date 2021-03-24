package com.zmm.mall.order.interceptor;

import com.zmm.common.constant.StringConstant;
import com.zmm.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登陆检测
 * @Description:
 * @Name LoginUserInterceptor
 * @Author Administrator
 * @Date By 2021-03-08 21:35:32
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
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
        
        // 指定路径放行
        String requestURI = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/order/order/status/**", requestURI);
        if (match) {
            return true;
        }
        
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
        }
    }
}
