package com.zmm.mall.gateway.config;

import org.springframework.context.annotation.Configuration;

/**
 * @author 900045
 * @description:
 * @name SentinelGatewayConfig
 * @date By 2021-04-21 14:40:17
 */
@Configuration
public class SentinelGatewayConfig {

	/**
	 * TODO 响应式编程
	 */
	public SentinelGatewayConfig(){
		/**
		GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
			// 网关流量了请求 就会调用此回调 Mono Flux
			@Override
			public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
				R error = R.error(ResultCode.TO_MANY_REQUEST.getCode() ,ResultCode.TO_MANY_REQUEST.getDesc());
				String jsonString = JSON.toJSONString(error);
				return ServerResponse.ok().body(Mono.just(jsonString),String.class);
			}
		});
			*/
			
	}
}
