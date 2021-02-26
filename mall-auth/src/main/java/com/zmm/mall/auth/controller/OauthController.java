package com.zmm.mall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.zmm.common.base.model.ReqResult;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.constant.NumberConstant;
import com.zmm.common.utils.HttpUtils;
import com.zmm.mall.auth.constant.StringConstant;
import com.zmm.mall.auth.feign.MemberFeignService;
import com.zmm.mall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 900045
 * @description:
 * @name OauthController
 * @date By 2021-02-25 11:35:05
 */
@Slf4j
@RestController
public class OauthController {
	
	@Resource
	private MemberFeignService memberFeignService;

	/**
	 * HttpSession session ---共享问题
	 * session 原理:
	 * 		1.第一次访问服务器(进行登录)  ---> 登录成功 将用户信息保存至 session; (相当于一个 map)
	 * 		2.命令浏览器保存一个 jsessionid = 123 的cookie; (相当于 银行给用户办一张银行卡) [银行卡不能跨行]
	 * 		3.以后访问的会带上cookie;
	 * 		4.浏览器关闭,清楚会话 cookie;
	 * 		5.再次访问 如果没有 jsessionid 会再次创建一个 同第一步.
	 * 	
	 * 	目前的情况:
	 * 		登录: mall-member
	 * 		认证: mall-auth
	 * 		name				value					domain
	 * 		jsessionid			6284 5464 2613 264		mall-member
	 * 	总结:
	 * 		1.不能跨不同域名共享
	 * 		2.同一个服务 ,复制多份 	session 不同步问题
	 */

	/**
	 * 方案一:session 复制
	 * 		说明:同一个服务 不同的系统 Session同步方案 (让两个服务器之间同步 session )
	 * 		优点: web-server (Tomcat)原生支持,只需要修改配置文件	
	 * 		缺点:
	 * 			1).session 同步需要数据传输,占用大量网络带宽,降低了服务器群的业务处理能力
	 * 			2).任意一台服务器保存的数据都是所有服务器的 session 总和,受到内存限制无法水平扩展更多服务器
	 * 			3).大型分布式集群下,由于所有的服务器都全量保存数据.此方案不可取
	 * 		总结: 该方案不使用于大型分布式服务
	 * 	
	 * 	方案二:客户端存储
	 * 		说明: session 存储在客户端 cookie 中
	 * 		优点:服务器不存储 session 用户保存自己的 session 信息到 cookie中.节省服务器资源
	 * 		缺点:
	 * 			1).每次 http 请求,携带用户在 cookie中的完整信息,浪费网络带宽
	 * 			2).cookie 是有大小限制的 (4k),不能保存大量的信息
	 * 			3).不安全,存在泄露、篡改、窃取
	 * 		总结：该方案不可取
	 * 	方案三:(IP)hash一致性
	 * 		说明:浏览器不变 都是这个人访问,通过计算得到 这个 session 存储在哪台服务器 ,永远访问的是这台服务器
	 * 		优点:
	 * 			1).只需要修改 nginx 配置	不需要修改应用代码
	 * 			2).负载均衡,只要hash属性的值分布是均匀的 多台服务器的负载是均衡的
	 * 			3).可以支持服务器水平扩展,( session 同步法是不行的,受内存限制)
	 * 		缺点:
	 * 			1).session 存在一个服务器,如果该服务器重启 可能导致部分 session 丢失,影响业务,需要重新登录
	 * 			2).如果 服务器水平扩展,rehash 后 session 重新分布,也会有一部分用户路由不到正确的 session.
	 * 		总结:问题不是很大.因为 session 本来都是有有效性的.所以这两种反向代理的方式可以使用.	
	 * 	
	 * 方案四:统一存储
	 * 		说明:请求进来因为有负载均衡的存在 不确定一定落在哪一台服务器(不管了) 无论哪台服务器的 session 不存储在服务器上 直接存储于 db/noSql
	 * 		优点:1.没有隐患 2.可以水平扩展 ,数据库/缓存水平切分即可 3.服务器重启 不会有 session 丢失
	 * 		缺点:1.增加了一次网络调用,并且需要修改应用代码;如将所有的 getSession 方法替换为从 redisSession查数据的方式.redis获取数据比内存慢很多
	 * 		但是上面的缺点可以用 SpringSession完美解决.	
	 * 	
	 */

	/**
	 * 微博社交登录
	 * @author: 900045
	 * @date: 2021-02-25 16:21:54
	 * @throws 
	 * @param code: 
	 * @param session: 
	 * @return: com.zmm.common.base.model.ReqResult
	 **/
	@GetMapping("/oauth/wb/success")
	public ReqResult loginWeiBo(@RequestParam("code") String code, HttpSession session) throws Exception {
		// 1.根据 code 获取 access_token
		Map<String,String> map = new HashMap<>();
		map.put("client_id","123456789");
		map.put("client_secret","123456789");
		map.put("grant_type","authorization_code");
		map.put("redirect_uri","http://mall.com/oauth/wb/success");
		map.put("code",code);
		// 获取 accessToken
		HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", null, null, map);
		if (response.getStatusLine().getStatusCode()== NumberConstant.TWO_HUNDRED){
			// 获取到了 accessToken
			/**
			 * {
			 * 		"access_token": "2.0token",   	用户授权的唯一票据,用于调用微博的开放接口,同时也是第三方应用验证微博用户登录的唯一票据
			 * 		"remind_in": "157679999";		access_token 生命周期,(废弃 开发者请使用expires_in)
			 * 		"expires_in": "157679999",		access_token  生命周期 单位是秒数
			 * 		"uid": "123456",				授权用户的uuid
			 * 		"isRealName": "true"
			 * 	}	
			 */
			String json = EntityUtils.toString(response.getEntity());
			// 目前是知道 是哪个社交用户
			SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
			ReqResult reqResult = memberFeignService.login(socialUser);
			if (reqResult.getResultCode() == NumberConstant.THOUSAND){
				//reqResult.getEntityObject(reqResult.getData(), new TypeReference<MemberRespVo>() {})
				// 2.登录成功就跳回首页
				// return "redirect:http://mall.com"
				// session.setAttribute("loginUser",new MemberRespVo())
				// 注意: SpringSession 会将 session 中的值存入 redis  [key:spring:session:sessions] reqResult 要序列化
				/**
				 * 已经可以存入 redis
				 * 还存在的问题:
				 * 		1.默认发的令牌 key 为 session = fang 作用域: (解决子域 session 共享 问题)
				 * 		2.	使用 json的序列化方式来徐序列化对象数据到 redis
				 * 
				 */
				session.setAttribute(StringConstant.LOGIN_USER,reqResult);
				return reqResult;
			} else {
				return new ReqResult(ResultCode.APP_FAIL);
				// return "redirect:http://auth.malll.com.login.html"
			}
			// 1.如果当前用户是第一次进入网址 -->自动注册(为当前社交用户生成一个会员信息账号,以后这个社交账号就对应指定会员)
			// 判断是登录还是注册用户
		} else {
			// 获取失败
			// return "redirect:http://auth.malll.com.login.html"
			return new ReqResult(ResultCode.APP_FAIL);
		}
	}
}
