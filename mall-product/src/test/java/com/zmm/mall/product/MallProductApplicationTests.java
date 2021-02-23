package com.zmm.mall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sandy.supply.supplier.jd.request.JdToken;
import com.sandy.supply.supplier.jd.request.Token;
import com.sandy.supply.supplier.jd.request.TokenUtil;
import com.sandy.supply.supplier.jd.request.UserForAuth;
import com.zmm.common.base.model.EmptyModel;
import com.zmm.common.base.model.ResultCode;
import com.zmm.common.constant.RedisTimeOutConstant;
import com.zmm.common.exception.BusinessException;
import com.zmm.common.utils.redis.RedisUtil;
import com.zmm.common.utils.redis.key.CommonKey;
import com.zmm.common.utils.redis.key.RedisTimeOut;
import com.zmm.common.utils.redis.key.TestRedisKey;
import com.zmm.mall.product.dao.AttrGroupDao;
import com.zmm.mall.product.dao.SkuSaleAttrValueDao;
import com.zmm.mall.product.entity.BrandEntity;
import com.zmm.mall.product.service.BrandService;
import com.zmm.mall.product.service.CategoryService;
import com.zmm.mall.product.service.impl.MessageConsumerService;
import com.zmm.mall.product.service.impl.MessageProducerService;
import com.zmm.mall.product.vo.SkuItemSaleAttrVo;
import com.zmm.mall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MallProductApplicationTests {

	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RedissonClient redisSon;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * redisUtil 工具类已经将 redisTemplate 包含了
	 */
	@Autowired
	private RedisUtil redisUtil;

	@Resource
	private MessageProducerService messageProducerService;

	@Resource
	private MessageConsumerService messageConsumerService;
	
	@Autowired
	private AttrGroupDao attrGroupDao;
	
	@Autowired
	private SkuSaleAttrValueDao skuSaleAttrValueDao;


	@Test
	public void getSaleAttrsBySpId(){
		List<SkuItemSaleAttrVo> saleAttrsBySpId = skuSaleAttrValueDao.getSaleAttrsBySpId(1L);
		log.info("查询结果:{}",saleAttrsBySpId);
	}
	@Test
	public void testGetAttrGroupWithAttrsBySpuId(){
		List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(225L, 3L);
		log.info("查询结果:{}",attrGroupWithAttrsBySpuId);
	}
	public static int split(String s, String regex) {
		String a[] = new String[2];
		int index = s.indexOf(regex);
		if (index == -1) {
			a[0] = s.substring(0, s.length());
		} else {
			if (index != 0)
				a[0] = s.substring(0, index);
			a[1] = s.substring(index + regex.length(), s.length());
		}
		return a[0].length();
	}

	@Test
	public void test3() {
		String str = "我今天吃饭了大家快点发大广告";
		int a = split(str, "大");
		System.out.println("第一个大的下标位置:" + a);
	}


	@Test
	public void test2() {
		String str = "我今天吃饭了大家快点发大广告";
		String regex = "(^.*?)大";
		String[] a = str.split(regex);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}

	@Test
	public void test() {
		String key = "";
		if (StringUtils.isEmpty(key)) {
			log.info("为空");
		} else {
			log.info("不为空");
		}

		UserForAuth userAuth = new UserForAuth();
		userAuth.setServicer(false);
		userAuth.setUserId(1250);
		userAuth.setUserName("18814823736");
		String userIdStr = TokenUtil.genernateCustomerId(userAuth.getUserId());
		//写入缓存
		redisUtil.hash(CommonKey.AUTH_USER_KEY, userIdStr, userAuth);
		Token token = genernateToken(userIdStr);
		token.setUserId(1250);
		token.setUserName("18814823736");
		token.setPhoneNumber("18814823736");
		System.out.println(token.getAccess_token());
	}

	protected Token genernateToken(String userIdOfToken) {
		Token token = new Token();
		long currentTimeMillis = System.currentTimeMillis();
		String tokenVal = UUID.randomUUID().toString();
		long tokenExpires = currentTimeMillis + RedisTimeOut.S_86400 * 60 * 1000; //60天   毫秒数
		redisUtil.set(CommonKey.AUTH_TOKEN_USER_PREFIX.setSuffix(tokenVal), userIdOfToken,
				tokenExpires, TimeUnit.MILLISECONDS); //二天
		String refreshToken = UUID.randomUUID().toString();
		long refreshExpires = currentTimeMillis + RedisTimeOut.S_86400 * 100 * 1000; //100天  毫秒数
		redisUtil.set(CommonKey.AUTH_TOKEN_REFRESH_PREFIX.setSuffix(refreshToken), userIdOfToken,
				refreshExpires, TimeUnit.MILLISECONDS); // 三十天
		token.setAccess_token(tokenVal);
		token.setToken_expires(tokenExpires);
		token.setRefresh_token(refreshToken);
		token.setRefresh_expires(refreshExpires);
		return token;
	}

	@Test
	public void testRedisMq() {
		messageConsumerService.start();
		messageProducerService.sendMessage("123456789");
		try {
			Thread.sleep(20000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		messageProducerService.sendMessage("987654321");

		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		messageConsumerService.interrupt();
	}

	@Test
	public void testLuckyWheel() {
		Object luckyWheelPrizeListObj = redisUtil.get(CommonKey.ACT_LUCK_WHEEL_PRIZE_INFO);
		if (luckyWheelPrizeListObj == null) {
			log.error("为空");
		}
		JSONObject jsonObject = JSONObject.fromObject(luckyWheelPrizeListObj);
		Map<String, String> prizeCodeMap = (Map<String, String>) JSONObject.toBean(jsonObject, Map.class);
		log.info("奖品--->{}", prizeCodeMap);
	}


	@Test
	public void testRedisUtil() throws BusinessException {
		// 1. 创建一个 key 存在过期时间
		// 2. 创建一个 key 存在不过期
		// 3. 判断某个key 是否存在
		// 4. 给某个 key 重新设置 过期时间
		// 5. 删除 某个 key
		// 6. 设置一个 key 值为 空对象
		// 7. 取出这个 空 对象 并对其进行处理
		Integer type = 8;

		switch (type) {

			case 1:
				setRedisKeyTimeOut(TestRedisKey.TEST_KEY_ONE, "MING", RedisTimeOutConstant.S_60);
				break;
			case 2:
				setRedisKeyNotTimeOut(TestRedisKey.TEST_KEY_TWO, "CH");
				break;
			case 3:
				isExistRedisKey(TestRedisKey.TEST_KEY_NOT_EXIST);
				break;
			case 4:
				setExpireRedisKey(TestRedisKey.TEST_KEY_TWO, RedisTimeOutConstant.S_60);
				break;
			case 5:
				Boolean b = redisUtil.delete(TestRedisKey.TEST_KEY_TWO);
				log.info("执行的结果 : {}", b);
				break;
			case 6:
				redisUtil.set(TestRedisKey.TEST_KEY_NULL_EMPTY, EmptyModel.instance(), RedisTimeOutConstant.S_60, TimeUnit.SECONDS);
				break;
			case 7:
				Object object = redisUtil.get(TestRedisKey.TEST_KEY_NULL_EMPTY);
				if (object instanceof EmptyModel) {
					//  优惠券 不存在
					throw new BusinessException(ResultCode.TEST_REDIS_KEY_NULL, ResultCode.TEST_REDIS_KEY_NULL.getDesc());
				}
				break;
			case 8:
				String lockValue = Long.toString(System.currentTimeMillis());
				boolean ifAbsent = redisUtil.setIfAbsent(CommonKey.ORDER_LOCK_ZSET_LOCK_KEY, lockValue, 50L, TimeUnit.SECONDS);
				log.info("执行的结果是:{}", ifAbsent);
				break;
			case 9:
				break;

			default:
				log.info("请输入正确的 type 值!!!");
		}

	}

	/**
	 * 重新 设置 redis key 的过期时间
	 * 注意 如果 这个 key 不存在 则 重新设置就会失败
	 *
	 * @param testKeyTwo
	 * @param time
	 */
	private void setExpireRedisKey(TestRedisKey testKeyTwo, long time) {
		boolean expire = redisUtil.expire(testKeyTwo, time, TimeUnit.MINUTES);
		if (expire) {
			log.info("重新设置 key 的过期时间成功!!!");
		} else {
			log.error("重新设置 key 的过期时间失败!!!");
		}
	}


	/**
	 * 判断redis 是否存在某个 key
	 *
	 * @param testKeyTwo
	 * @return
	 */
	private boolean isExistRedisKey(TestRedisKey testKeyTwo) {
		boolean exists = redisUtil.exists(testKeyTwo);
		if (exists) {
			log.info("存在这个key!!!");
		} else {
			log.error("不存在这个key!!!");
		}
		return exists;
	}

	/**
	 * 2.创建一个 key 存在不过期
	 *
	 * @param testKeyTwo 存入 redis中的key
	 * @param object     值
	 */
	private void setRedisKeyNotTimeOut(TestRedisKey testKeyTwo, Object object) {
		redisUtil.setKey(testKeyTwo, "CH");
	}

	/**
	 * 1.创建一个 key 存在过期时间
	 *
	 * @param testKeyOne 存入 redis中的key
	 * @param object     值
	 * @param time       过期时间
	 */
	private void setRedisKeyTimeOut(TestRedisKey testKeyOne, Object object, long time) {
		redisUtil.set(testKeyOne, object, time, TimeUnit.SECONDS);
	}


	@Test
	public void testRedisSon() {
		System.out.println(redisSon);

	}

	@Test
	public void testToken() {
		Object o = redisTemplate.opsForValue().get("SUPPLY:TOKEN_JD");
		JdToken token = (JdToken) o;
		System.out.println(token);

	}

	@Test
	public void testStringRedisTemplate() {
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();

		//保存
		opsForValue.set("hello", "world_" + UUID.randomUUID().toString());

		//查询
		String hello = opsForValue.get("hello");
		System.out.println(hello);
	}

	@Test
	public void testFindPath() {
		Long[] cateLogPath = categoryService.findCateLogPath(225L);
		log.info("完整路径:{}", Arrays.asList(cateLogPath));
	}

	@Test
	public void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();
		//brandEntity.setName("华为");
		//brandService.save(brandEntity);

		//brandEntity.setBrandId(1L);
		//brandEntity.setDescript("国产");
		//brandService.updateById(brandEntity);

		List<BrandEntity> entityList = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
		entityList.forEach(item -> {
			System.out.println(item);
		});

		System.out.println("保存成功....");
	}

	@Test
	public void testStream() {
		BrandEntity brandEntity1 = new BrandEntity();
		brandEntity1.setBrandId(1L);
		brandEntity1.setName("华为1");

		BrandEntity brandEntity2 = new BrandEntity();
		brandEntity2.setBrandId(2L);
		brandEntity2.setName("华为2");

		BrandEntity brandEntity3 = new BrandEntity();
		brandEntity3.setBrandId(null);
		brandEntity3.setName("华为3");

		BrandEntity brandEntity4 = new BrandEntity();
		brandEntity4.setBrandId(null);
		brandEntity4.setName("华为4");

		List<BrandEntity> brandEntities = new ArrayList<>();
		brandEntities.add(brandEntity1);
		brandEntities.add(brandEntity2);
		brandEntities.add(brandEntity3);
		brandEntities.add(brandEntity4);

		brandEntities.stream().map(brandEntity -> {
			if (brandEntity.getBrandId() == null) {
				brandEntity.setBrandId(-1L);
			}
			return brandEntity;
		}).collect(Collectors.toList());

		System.out.println(brandEntities);
	}

}
