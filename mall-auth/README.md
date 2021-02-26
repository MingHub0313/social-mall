### 一、OAuth2.0
发起方: client 第三方应用

    1.client ----> 向用户申请请求认证 ----> resource owner (本人)
    2.resource owner     ----> 用户授权(输入自己的社交账号密码)
    3.使用上步授权 进行认证  ----> Authorization Server (微博、QQ...)
    4.Authorization Server ----> 认证通过,返回令牌令牌 ----> client
    5.client ----> 使用令牌 请求开放保护信息 ----> Resource Server (资源服务器 微博、QQ...)
    6.Resource Server ----> 认证令牌 返回保护信息 ----> client
   
### 二、步骤
#### 2.1 引导地址
    https://api.weibo.com/oauth2/authorize?client_id=App_Key&response_type=code&redirect_uri=URL
    YOUR_CLIENT_ID : App_Key
    YOUR_REGISTERED_REDIRECT_URI : URL 授权回调页 http://mall.com/success
#### 2.2 同意授权
    如果用户同意授权,页面跳转至 URL/?code=CODE：
#### 2.3 获取Token
    https://api.weibo.com/oauth2/access_token
    ?client_id=App_Key&client_secret=App_Secret&grant_type=authorization_code&redirect_uri=URL&code=CODE
    
    POST 请求方式
    YOUR_CLIENT_ID : App_Key
    YOUR_CLIENT_SECRET : App_Secret
    YOUR_REGISTERED_REDIRECT_URI : URL
    CODE : code=CODE
    
    返回值 如下:
    {
        "access_token": "SlAV32hkKG",
        "remind_in": 3600,
        "expires_in": 3600 
    }
    
    说明: 1.使用Code获取 access_token ,Code 是能使用一次
          2.用一个用户的 access_token 在一段时间内不是变的

### 二、SpringSession 核心原理

    SessionRepositoryFilter 类
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {
            
        // 请求一进来 设置 setAttribute
        request.setAttribute(SESSION_REPOSITORY_ATTR, this.sessionRepository);
        
        // 对原生请求包装 (原生的请求、响应、上下文) 装饰者模式 返回 wrappedRequest
        SessionRepositoryFilter<S>.SessionRepositoryRequestWrapper wrappedRequest = 
        new SessionRepositoryFilter
        .SessionRepositoryRequestWrapper(request, response, this.servletContext);
        
        /**
         * HTTPSession session = request.getSession();
         */
        // 对原生响应包装
        SessionRepositoryFilter.SessionRepositoryResponseWrapper wrappedResponse = 
        new SessionRepositoryFilter
        .SessionRepositoryResponseWrapper(wrappedRequest, response);
        
        try {
            // 进去放行 的参数是 包装过的 请求和响应 [包装后的对象应用到我们后面的整个执行链]
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            wrappedRequest.commitSession();
        }
    }
    
    
### 三、SSO
#### 3.1 核心说明:
多个系统即使域名不一样,想办法给三个系统同步同一个用户的票据

    1.中央认证服务器: ssoserver.com
    2.其他系统,想要登录去 ssoserver.com 登录 登录成功跳转回来
    3.只要有一个登录,就处处都不用登录
    4.全系统 统一一个 key (sessionid);所有系统可能域名都不相同.
