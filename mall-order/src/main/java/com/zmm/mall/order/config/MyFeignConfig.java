package com.zmm.mall.order.config;

import com.zmm.common.constant.AuthConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Name MyFeignConfig
 * @Author Administrator
 * @Date By 2021-03-09 19:51:23
 */
@Configuration
public class MyFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        // 是从构造器(SynchronousMethodHandler)中获得的 List<RequestInterceptor>
        /**
         * RequestInterceptor 是在 SynchronousMethodHandler 构造器中创建的
         * 谁 创建了 构造器  -- > MethodHandler 在创建时 创建了 SynchronousMethodHandler 构造器
         *  Feign 在 build 的时候 创建了 Factory
         *  只要容器中有拦截器就会自动从容器中拿到我们的所有拦截器,并添加进去 requestInterceptors
         *  public Feign.Builder requestInterceptor(RequestInterceptor requestInterceptor)
         *     this.requestInterceptors.add(requestInterceptor)
         *      return this
         *  public Feign build()
         */
        

        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                System.out.println("Order feign 远程之前先进行apply 方法 ---线程...."+Thread.currentThread().getId());
                // 上下文环境的保持器 RequestContextHolder 
                // 拿到刚进赖来的这个请求 -->相当于就是拿到我们调用toTrade()方法 当时的 这个 HttpServletRequest request
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes == null) {
                    return;
                }
                // 同步请求头数据
                HttpServletRequest request = attributes.getRequest();
                if (ObjectUtils.isEmpty(request)){
                    return;
                }
                // 2.获取当前请求(存在请求头信息) 此处可能为 null --> java.lang.NullPointException
                String token = request.getHeader(AuthConstant.HEADER_TOKEN_KEY);
                if (token == null) {
                    Object obj = request.getAttribute(AuthConstant.HEADER_TOKEN_KEY);
                    token = obj == null ? null : obj.toString();
                }
                // 3.同步1请求数据 cookie
                //String cookie = request.getHeader("Cookie")
                // 4.给新的请求(java方式的请求)同步之前的请求数据 (header 请求头) --> 给远程服务发送请求 并携带这些(header)数据
                requestTemplate.header(AuthConstant.HEADER_TOKEN_KEY, token);
            }
        };
    }
    
    
    /**
    @Bean
    public Decoder feignDecoder(){
        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper());
        List<MediaType> unmodifiedMediaTypeList = jacksonConverter.getSupportedMediaTypes();
        // 因为上面是得到 unmodified list 类型 还需要一个 List进行操作
        List<MediaType> mediaTypeList = new ArrayList<>(unmodifiedMediaTypeList.size()+1);
        mediaTypeList.addAll(unmodifiedMediaTypeList);
        mediaTypeList.add(MediaType.APPLICATION_OCTET_STREAM);
        ((MappingJackson2HttpMessageConverter) jacksonConverter).setSupportedMediaTypes(mediaTypeList);
        ObjectFactory<HttpMessageConverters> objectFactory = new ObjectFactory<HttpMessageConverters>(){
            @Override
            public HttpMessageConverters getObject() throws BeansException {
                return new HttpMessageConverters(jacksonConverter);
            }
        };
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }
    
    private static ObjectMapper customObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
    }
     */
}
