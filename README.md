# social-mall
## 2020-微服务架构

### 统一说明
#### 1.服务中使用的数据库为 mysql 地址: 47.111.112.220
#### 2.服务中使用的缓存为 redis 地址:  47.111.112.220 库统一为: 9
#### 3.服务中使用的消息为 rabbitmq 地址:   47.111.112.220
#### 4.服务中使用的OSS为 阿里云
#### 5.服务中使用的搜索为 elasticsearch 地址:  47.116.113.35
#### 6.服务中使用线程池:    ThreadPoolExecutor
#### 7.服务中使用 redisSon  作为分布式锁
#### 8.服务中使用 SpringCache 简化缓存
# mall-auth
    port:20000
# mall-cat
    port:30000
# mall-common
# mall-coupon
# mall-gateway
    prot:88
# mall-member
    port:8000
# mall-order
    port:9000
# mall-product
    port:5000
# mall-search
    port:12000
# mall-third
    port:13000
# mall-ware
    prot:11000
