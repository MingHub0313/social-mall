package com.zmm.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmm.common.utils.PageUtils;
import com.zmm.common.utils.Query;
import com.zmm.mall.product.dao.CategoryDao;
import com.zmm.mall.product.entity.CategoryEntity;
import com.zmm.mall.product.service.CategoryBrandRelationService;
import com.zmm.mall.product.service.CategoryService;
import com.zmm.mall.product.vo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品三级分类
 *
 * @author zhangmingming
 * @email 1805783671@qq.com
 * @date 2020-08-21 10:45:46
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    //@Autowired
    //private CategoryDao categoryDao

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1,查出所有分类
        List<CategoryEntity> entityList = baseMapper.selectList(null);

        List<CategoryEntity> entityListFirst = entityList.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map( (menu)->{
            menu.setChildren(getChildren(menu,entityList));
            return menu;
        }).sorted((menu1,menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort())-  (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        // 2,组装成父子的树形结构
        return entityListFirst;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO 检查当前删除的菜单,是否被别的地方引用
        // 逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCateLogPath(Long cateId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(cateId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    private List<Long> findParentPath(Long cateId,List<Long> paths){
        paths.add(cateId);
        CategoryEntity byId = this.getById(cateId);
        if (byId.getParentCid() != 0){
            findParentPath(byId.getParentCid(),paths);
        }
        return paths;

    }

    /**
     * 级联更新所有关联的数据
     * @CacheEvict : 失效模式
     *      @CacheEvict(value = "category",key = "'getLevel1Categories'")
     * Caching : 同时进行多种缓存操作
     *  @Caching(evict = {
     *             @CacheEvict(value = "category",key = "'getLevel1Categories'"),
     *             @CacheEvict(value = "category",key = "'getCatalogJson'")
     *     })
     * CacheEvict  :指定删除某个分区下的所有数据 @CacheEvict(value = "category",allEntries = true)  //清除模式
     *
     * 所以约定 : 存储同一类型的数据 都可以指定成同一分区
     *
     * @CachePut : //双写模式
     * @param category
     */


    @CacheEvict(value = "category",allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {
        //级联更新所有的关联数据
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());

        // 同时修改缓存中的数据
        // 删除缓存中 key
    }

    /**
     * @Cacheable 代表当前方法的结果需要缓存 , 如果缓存中存在 方法不用调用 ,如果缓存中没有 则 调用方法 最后将方法的结果放入缓存
     * 指定每一个需要缓存的数据 都要指定放到那个名字的缓存 【缓存区分:按照业务类型分】
     * 默认行为 :
     *      1).如果缓存命中 ,方法不用调用
     *      2).key 是默认生成  -- category::SimpleKey []   redis 中的 key   组成部分 : 缓存名字 :: 自主生成的key值
     *      3).value 值 .默认使用 jdk序列化机制 ,将序列化后的数据存在 redis
     *      4).默认过期时间 -1 永不过期
     *
     * 自定义操作:
     *      1).指定生成的缓存使用的 key  key属性可以指定 接受一个 SpEL (表达式) 需要用 单引号 区分
     *          SpEL : https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/integration.html#cache-spel-context
     *              @Cacheable(value = {"category"},key = "'Level1Categories'")
     *      2).指定缓存的数据过期时间 在配置文件中设置过期时间 ttl
     *      3).将数据保存为json格式  --自定义缓存管理器
     *              CacheAutoConfiguration 中导入了 RedisCacheConfiguration
     *
     * @return
     */
    @Cacheable(value = {"category"},key = "#root.method.name",sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        log.debug("查询数据库");
        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return entities;
    }


    /**
     * TODO 产生推外内存溢出 : OutOfDirectMemoryError 原因 : springboot 2.0 默认使用 lettuce作为操作redis 的 客户端 .其中网络 使用netty网络通信
     *  lettuce 的 bug 导致的推外内存溢出  可以通过 -Dio.netty.maxDirectMemory 进行设置
     *  解决方案 : 不能使用上述参数来调大推外溢出
     *      1).升级lettuce客户端 (目前没有)
     *      2).切换使用 jedis
     *      redisTemplate :  lettuce 、jedis 操作 redis 的底层客户端.
     *      spring 对 lettuce 、jedis 再次封装 redisTemplate
     * @return
     */

    @Cacheable(value = "category",key = "#root.methodName")
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson(){
        Map<String, List<Catalog2Vo>> dataFromDb = getDataFromDb();

        return dataFromDb;
    }




    public Map<String, List<Catalog2Vo>> getCatalogJson2(){

        //给缓存中放json字符串 ,拿出的json字符串 还要逆转为能用的对象类型   [序列化 与 反序列化]

        /**
         *  1.空结果缓存             : 解决缓存穿透
         *  2.设置过期时间(随机值)   : 解决缓存雪崩
         *  3.加锁                   : 解决缓存击穿 [重点]
         */

        Map<String, List<Catalog2Vo>> result;
        //加入缓存
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)){
            // 缓存中没有 查询 数据库
            result = getCatalogJsonFromDbWithLocalLock();

            //查到的数据 放入缓存  -- 将存入缓存的操作 放在查询数据库后
            //将查出的对象 转为 json 存放在缓存中

            return result;
        }
        log.debug("缓存命中...直接返回....");
        result = JSON.parseObject(catalogJSON,new TypeReference<Map<String, List<Catalog2Vo>>>(){});
        return result;
    }


    /**
     * 从数据库查询 并封装分类数据 本地锁
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithLocalLock() {

        // 1.加锁 --- 同步代码块
        //得到锁以后,我们应该再去缓存中确定一次,如果没有才需要继续查询
        // TODO 本地锁 : synchronized ,JUC(lock) 在分布式情况下 ,想要锁住所有的,必须使用分布式锁

        /*synchronized (this){
         */
            /**
             * String catalogJSON = redisTemplate.opsForValue().get("catalogJSON")
             * if (!StringUtils.isEmpty(catalogJSON)){
             *      Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON,new TypeReference<Map<String, List<Catalog2Vo>>>(){})
             *      return result
             * }
             * /
            // 只要是同一把锁,就能锁住需要这个锁的所有线程   单实例可以  不过我们是分布式 所以此方法不可取
            // 本地锁只能锁住当前进程 所以我们需要分布式锁
            // synchronized (this): SpringBoot 所有的组件在容器中都是单例的
            // synchronized 加在方法上
            // 源代码块...
        }*/

        /**
         * 1.将数据库的多次查询 变为一次 不在 for 内 查询SQL
         * 2.对查询的数据 进行遍历
         */

        // 查询数据库
        Map<String, List<Catalog2Vo>> parent_cid = getDataFromDb();

        // 存入缓存
        String jsonString = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catalogJSON",jsonString);
        return parent_cid;
    }


    /**
     * 使用分布式锁
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {

        /**
         * 1.占分布式锁 --redis 占坑 setNx()
         * 问题 : setNx占好了位 业务代码异常或者程序在页面过程中宕机.没有执行删除锁逻辑,这就造成了死锁
         * 解决: 设置锁的过期时间,即使没有执行删除锁逻辑,redis也会自动删除
         *
         * 2.给锁添加过期时间
         * 问题 : setNx设置好,正要去设置过期时间,宕机,又造成死锁
         * 解决 : 设置过期时间和占位必须是原子的.redis 支持使用 setNx ex命令 即 同时执行
         * 命令 set lock "11" EX 300 NX
         *
         * 3.删除锁直接删除 ??
         * 问题 : 如果由于业务时间很长 ,锁自己过期了 我们直接删除 有可能把别人正在持有的锁删除了
         * 解决 : 占锁的时候,值指定为 uuid 每个人匹配的是自己的锁才删除
         *
         * 问题 : 如果正好判断是当前值 正要删除锁的时候,锁已经过期 ,别人刚好可以设置新的锁.那么我们删除的可能会失别人的锁
         * 解决 : 删除锁必须保证原子性 . 使用redis + Lua 脚本完成
         */
        //Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("lock", "OK")
        // 原子操作 加锁和设置过期时间必须是同步的
        String uuid = UUID.randomUUID().toString();
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("lock", uuid,300,TimeUnit.SECONDS);
        if (ifAbsent){
            log.debug("获取分布式成功...");
            // 加锁成功! 执行业务
            // 设置过期时间
            // redisTemplate.expire("lock",30, TimeUnit.SECONDS)
            Map<String, List<Catalog2Vo>> dataFromDbHandle;
            try{
                dataFromDbHandle = getDataFromDbHandle();
            }finally {
                // 使用 Lua脚本解锁
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                // 原子删除
                redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            //删除锁
            // redisTemplate.delete("lock")
            // 网络耗时 会发生 传回来的值是 本次想要的 但是缓存中的值 可能改变了 则下一步 删的还是别人的锁
            // 与加锁一样 获取值对比 + 对比成功删除 = 原子操作

            /*String lockValue = redisTemplate.opsForValue().get("lock")
            if (uuid.equals(lockValue)){
                redisTemplate.delete("lock")
            }*/
            return dataFromDbHandle;
        } else {
            // 加锁失败...重试. 同 synchronized() 自旋方式 ---->死锁
            //休眠 100ms 重试
            log.debug("获取分布式失败...等待重试...");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock();
        }
    }


    /**
     * 使用分布式锁 RedisSon
     * 缓存中的数据如何和数据库保持一致
     * 1).双写模式   -- 会发生脏数据问题
     * 2).失效模式
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisSonLock() {

        /**
         * 1.锁的名字注意.锁的粒度 具体缓存的是某个数据  11号商品 product-11-lock ... product-12-lock
         */
        RLock lock = redissonClient.getLock("CatalogJson-lock");
        lock.lock();
        Map<String, List<Catalog2Vo>> dataFromDbHandle;
        try{
            dataFromDbHandle = getDataFromDbHandle();
        }finally {
            lock.unlock();
       }
        return dataFromDbHandle;
    }

    private Map<String, List<Catalog2Vo>> getDataFromDbHandle() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)){
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON,new TypeReference<Map<String, List<Catalog2Vo>>>(){});
            return result;
         }

        // 查询数据库
        Map<String, List<Catalog2Vo>> parent_cid = getDataFromDb();

        //存入缓存
        String jsonString = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catalogJSON",jsonString);
        return parent_cid;
    }

    /**
     * 查询数据库
     * @return
     */
    private Map<String, List<Catalog2Vo>> getDataFromDb() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        // 1.查询所有的一级分类
        List<CategoryEntity> categoryEntityList = getParent_cid(categoryEntities, 0L);
        // 2.封装数据
        return categoryEntityList.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 1.每一个的一级分类 -->查询这个一级分类的所有二级分类
            List<CategoryEntity> entities = getParent_cid(categoryEntities, v.getParentCid());
            // 2.封装上面的结果
            List<Catalog2Vo> catalog2Vos = null;
            if (!CollectionUtils.isEmpty(entities)) {
                catalog2Vos = entities.stream().map(l2 -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 1.找当前二级分类的三级分类封装成 vo
                    List<CategoryEntity> level3Catalog = getParent_cid(categoryEntities, l2.getParentCid());
                    if (!CollectionUtils.isEmpty(level3Catalog)) {
                        List<Catalog2Vo.Cat3log3Vo> cat3log3Vos = level3Catalog.stream().map(l3 -> {
                            // 2.封装成指定格式
                            Catalog2Vo.Cat3log3Vo cat3log3Vo = new Catalog2Vo.Cat3log3Vo(l2.getCatId().toString(), l3.getCatId().toString(),
                                    l3.getName());
                            return cat3log3Vo;
                        }).collect(Collectors.toList());
                        catalog2Vo.setCatalog3List(cat3log3Vos);
                    }
                    return catalog2Vo;

                }).collect(Collectors.toList());
            }

            return catalog2Vos;
        }));
    }

    /**
     * 方法抽取
     * @param categoryEntities
     * @param parentCid
     * @return
     */
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> categoryEntities,Long parentCid) {
        List<CategoryEntity> categoryEntityList = categoryEntities.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return categoryEntityList;
    }

    /**
     * 递归查询所有分类的子分类
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> all){

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //1.找到子分类
            categoryEntity.setChildren(getChildren(categoryEntity,all));
            return categoryEntity;
        }).sorted((menu1,menu2)->{
            //2.排序
            return (menu1.getSort()==null?0:menu1.getSort())-  (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return children;

    }

}
