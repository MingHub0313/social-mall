package com.zmm.common.utils.redis;

import com.zmm.common.base.model.EmptyModel;
import com.zmm.common.constant.RedisTimeOutConstant;
import com.zmm.common.enums.RedisChannel;
import com.zmm.common.exception.CustomRunTimeException;
import com.zmm.common.utils.Assert;
import com.zmm.common.utils.redis.key.RedisKey;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Name RedisUtil redis 工具类
 * @Author 900045
 * @Created by 2020/11/18 0018
 */
public final class RedisUtil<T> {

	private static final Random RANDOM = new Random();

	/**
	 *  获取1-1000的随机数  用于加在超时时间后面，防止缓存在同一时间失效
	 *
	 * @param second    超时的秒数
	 * @return
	 */
	private long getTimeout(long second) {
		return second + RANDOM.nextInt(1000) + 1;
	}


	private RedisTemplate<String,T> redisTemplate;

	private HashOperations<String, String, T> hashTemplate;
	private SetOperations<String, T> setTemplate;
	private ZSetOperations<String, T> zSetTemplate;
	private ListOperations<String, T> listTemplate;

	/**
	 * 通过构造方法 创建Bean 将 redisTemplate 作为参数传递
	 * @param redisTemplate
	 */
	public RedisUtil(RedisTemplate<String, T> redisTemplate) {
		if (redisTemplate == null) {
			throw new CustomRunTimeException("RedisUtil<Object>init fail,  redisTemplate is null!");
		}
		this.redisTemplate = redisTemplate;
		this.hashTemplate = this.redisTemplate.opsForHash();
		this.setTemplate = redisTemplate.opsForSet();
		this.zSetTemplate = redisTemplate.opsForZSet();
		this.listTemplate = redisTemplate.opsForList();

	}


	public Long execute(String redisScript,RedisKey redisKey,String param){
		Long execute = redisTemplate.execute(new DefaultRedisScript<Long>(redisScript, Long.class), Arrays.asList(redisKey.getKey()), param);
		return execute;
	}


	/**
	 *
	 * @param key  			redis 键
	 * @param value			redis 值
	 * @param timeout		超时时间
	 * @param timeUnit		单位 :  MILLISECONDS - 毫秒		SECONDS - 秒		MINUTES - 分钟		HOURS - 小时
	 */
	public void set(RedisKey key, T value, long timeout, TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(key.getKey(), value, getTimeout(timeout), timeUnit);
	}

	/**
	 * 设置 key 不超时
	 * @param redisKey
	 * @param value
	 */
	public void setKey(RedisKey redisKey, T value){
		redisTemplate.opsForValue().set(redisKey.getKey(), value);
	}


	/**
	 * 给key设置 一个空模型
	 * @param key
	 */
	@Deprecated
	public void setEmpty(RedisKey key) {
		Assert.notNull(key);
		redisTemplate.opsForValue().set(key.getKey(), (T) EmptyModel.instance(),
				getTimeout(RedisTimeOutConstant.S_7200), TimeUnit.SECONDS);
	}

	/**
	 * 相当于 setNX 设置过期
	 * @param redisKey
	 * @param value
	 * @param time
	 * @param timeUnit
	 * @return
	 */
	public boolean setIfAbsent(RedisKey redisKey, T value ,long time ,TimeUnit timeUnit) {
		return this.redisTemplate.opsForValue().setIfAbsent(redisKey.getKey(),value,time,timeUnit);
	}



	/**
	 * 根据 redis 键 获取 值
	 * @param key
	 * @return value
	 */
	public T get(RedisKey key) {
		Assert.notNull(key);
		return redisTemplate.opsForValue().get(key.getKey());
	}

	/**
	 * 根据 键 删除 记录
	 * @param key
	 * @return  true / false
	 */
	public Boolean delete(RedisKey key) {
		Assert.notNull(key);
		return redisTemplate.delete(key.getKey());
	}

	
	public void leftPushAll(RedisKey key,List<T> object){
		listTemplate.leftPushAll(key.getKey(),object);
	}

	// ========================设置自增 START =================================

	/**
	 * 递增
	 *
	 * @param redisKey
	 * @param delta
	 * @return
	 */
	public Long increment(RedisKey redisKey, long delta) {
		Assert.notNull(redisKey);
		return redisTemplate.opsForValue().increment(redisKey.getKey(), delta);
	}

	// ========================设置自增 END =====================================


	/**
	 * 获取所有key 的后缀
	 * @param redisKey
	 * @param length
	 * @return
	 */
	public List<String> getKeysSuffix(String redisKey,Integer length){
		Set<String> keys = redisTemplate.keys(redisKey);
		List<String> strList = new ArrayList<>();
		for (String key : keys) {
			strList.add(key.substring(length));
		}
		return strList;
	}

	// ========================设置 key 是否存在 START =================================

	public boolean exists(RedisKey key) {
		Boolean flag = redisTemplate.hasKey(key.getKey());
		return flag != null && flag;
	}
	// ========================判断 key 是否存在 END ===================================

	// ========================设置超时时间 START =================================
	/**
	 * 超时设置
	 *
	 * @param redisKey
	 * @param date
	 * @return
	 */
	public boolean expire(RedisKey redisKey, Date date) {
		Assert.notNull(redisKey);
		Assert.notNull(date);
		Boolean flag = this.redisTemplate.expireAt(redisKey.getKey(), date);
		return flag != null && flag;
	}

	/**
	 * 超时设置
	 *
	 * @param redisKey
	 * @param timeout
	 * @param timeUnit
	 * @return
	 */
	public boolean expire(RedisKey redisKey, long timeout, TimeUnit timeUnit) {
		Assert.notNull(redisKey);
		Assert.notNull(timeUnit);
		Boolean flag = this.redisTemplate.expire(redisKey.getKey(), timeout, timeUnit);
		return flag != null && flag;
	}


	// ============================设置超时时间 END =============================



	// ==================================== hash 相关  START ==============================================

	/**
	 * 获取hash的所有key
	 *
	 * @param redisKey
	 * @return
	 */
	public Set<String> hashKeys(String redisKey) {
		Assert.notNull(redisKey);
		return hashTemplate.keys(redisKey);
	}


	/**
	 * 获取hash的所有key的数量
	 *
	 * @param redisKey
	 * @return
	 */
	public Long hashSize(String redisKey) {
		Assert.notNull(redisKey);
		return hashTemplate.size(redisKey);
	}

	/**
	 *  HASH  累加
	 *
	 * @param redisKey
	 * @param key
	 * @param init    累加值
	 * @return
	 */

	private int hashIncrement(String redisKey, String key, int init) {
		Assert.notNull(redisKey);
		Assert.notEmpty(key);
		return hashTemplate.increment(redisKey, key, init).intValue();
	}

	public Double hashIncrement(RedisKey redisKey, String key, Double delta) {
		Assert.notNull(redisKey);
		Assert.notEmpty(key);
		return hashTemplate.increment(redisKey.getKey(), key, delta);
	}

	public void hashDelete(String redisKey, String key) {
		Assert.notNull(redisKey);
		Assert.notEmpty(key);
		hashTemplate.delete(redisKey, key);
	}

	public <HK> void hashDelete(RedisKey redisKey, HK key) {
		hashDelete(redisKey.getKey(), key.toString());
	}

	public void hashDelete(RedisKey redisKey, Object... keys) {
		Assert.notNull(redisKey);
		if (keys != null && keys.length != 0) {
			hashTemplate.delete(redisKey.getKey(), keys);
		}
	}
	public Map<String, T> hash(RedisKey redisKey) {
		Assert.notNull(redisKey);
		return hashTemplate.entries(redisKey.getKey());
	}


	public List<T> hashValues(RedisKey redisKey) {
		Assert.notNull(redisKey);
		return hashTemplate.values(redisKey.getKey());
	}


	public <HK> T hash(RedisKey redisKey, HK key) {
		Assert.notNull(redisKey);
		Assert.notNull(key);
		return hashTemplate.get(redisKey.getKey(), key.toString());
	}

	public List<T> hashMultiGet(RedisKey redisKey, Collection<String> keys) {
		Assert.notNull(redisKey);
		Assert.notNull(keys);
		return hashTemplate.multiGet(redisKey.getKey(), keys);
	}

	public void hashAddAll(RedisKey redisKey, Map<String, T> map) {
		Assert.notNull(redisKey);
		hashTemplate.putAll(redisKey.getKey(), map);
	}

	public <HK> void hash(RedisKey redisKey, HK key, T value) {
		Assert.notNull(redisKey);
		Assert.notNull(key);
		hashTemplate.put(redisKey.getKey(), key.toString(), value);
	}

	public Long getExpire(RedisKey redisKey, TimeUnit timeUnit) {
		Assert.notNull(redisKey);
		Assert.notNull(timeUnit);
		return redisTemplate.getExpire(redisKey.getKey(), timeUnit);
	}

	/**
	 * 根据 hash 的key 以及 field 判断是否存在
	 * @author: Administrator
	 * @date: 2021-03-03 23:25:19
	 * @param redisKey: 
	 * @param value: 
	 * @return: boolean
	 **/
	public boolean hashExists(RedisKey redisKey,T value){
		return redisTemplate.opsForHash().hasKey(redisKey.getKey(),value);

	}

	/**
	 * 根据 hash 的key 以及 field 获取对应的值
	 * @author: Administrator
	 * @date: 2021-03-03 23:24:56
	 * @param redisKey: 
	 * @param field: 
	 * @return: java.lang.String
	 **/
	public String hashGet(RedisKey redisKey,T field){
		Object val = redisTemplate.opsForHash().get(redisKey.getKey(), field);
		return  val == null ? null : val.toString();
	}

	// ==================================== hash 相关  END   ==============================================


	//  redis 消息  ---------------------------------------------------------- START ------------------------------------

	/**
	 * redis 消息发送
	 *
	 * @param channel
	 * @param message
	 */
	public void sendMsg(RedisChannel channel, Object message) {
		Assert.notNull(channel, "redis sendMsg  channel is null");
		Assert.notNull(message, "redis sendMsg  message is null");
		// logger.debug("redis-sendMsg : {} -> {} ", channel, message)
		redisTemplate.convertAndSend(channel.name(), message);
	}

	//  redis 消息  ---------------------------------------------------------- END ------------------------------------


	/**
	 * 范围检索,返回List(key的集合)
	 * @param start
	 * @param end
	 * @return
	 */
	public List<T> range(RedisKey redisKey, long start, long end) {
		return redisTemplate.opsForList().range(redisKey.getKey(), start, end);
	}

	/**
	 * 队列获取数量
	 * @param redisKey
	 * @return
	 */
	public Long listSize(RedisKey redisKey) {
		return redisTemplate.opsForList().size(redisKey.getKey());
	}


	public Long hSet(RedisKey redisKey, T value) {
		Assert.notNull(redisKey);

		return setTemplate.add(redisKey.getKey(), value);
	}

	public Boolean hSetExists(RedisKey redisKey, T value) {
		Assert.notNull(redisKey);
		return setTemplate.isMember(redisKey.getKey(), value);
	}

	public Long hSetDelete(RedisKey redisKey, T value) {
		Assert.notNull(redisKey);
		return setTemplate.remove(redisKey.getKey(), value);
	}


	public T hSet(RedisKey redisKey) {
		Assert.notNull(redisKey);
		return setTemplate.pop(redisKey.getKey());
	}

	public Long size(RedisKey redisKey) {
		Assert.notNull(redisKey);
		return setTemplate.size(redisKey.getKey());
	}

	public long zSetSize(RedisKey redisKey) {
		Assert.notNull(redisKey);
		return zSetTemplate.size(redisKey.getKey());
	}


	/**
	 *    获取指定分数里的第一个
	 *
	 * @param redisKey
	 * @param min
	 * @param max
	 * @return
	 */
	public T zSetOne(RedisKey redisKey, double min, double max) {
		//  zSetTemplate.rangeByScore(key, min, max)
		Set<T> range = zSetTemplate.rangeByScore(redisKey.getKey(), min, max, 0, 1);
		if (range != null && range.iterator().hasNext()) {
			return range.iterator().next();
		}
		return null;
	}

	public boolean zsetAdd(RedisKey redisKey, T value, double score) {
		Assert.notNull(redisKey);
		return zSetTemplate.add(redisKey.getKey(), value, score);
	}

	public Set<T> zSetRange(RedisKey redisKey, double min, double max) {
		//  zSetTemplate.rangeByScore(key, min, max)
		return zSetTemplate.rangeByScore(redisKey.getKey(), min, max);
	}

	public Set<T> zSetRangeNew(RedisKey redisKey, long start, long end) {
		//  zSetTemplate.rangeByScore(key, min, max)
		return zSetTemplate.range(redisKey.getKey(), start, end);
	}

	public Set<T> zSetRange(RedisKey redisKey, double min, double max, long length) {
		//  zSetTemplate.rangeByScore(key, min, max)
		return zSetTemplate.rangeByScore(redisKey.getKey(), min, max, 0, length);
	}

	public Set<T> zSetrange(RedisKey key, long start, long end) {
		Set<T> resultSet = zSetTemplate.range(key.getKey(), start, end);
		return resultSet;
	}


	public long zSetRemove(RedisKey redisKey, Object... objects) {
		Assert.notNull(redisKey);

		return zSetTemplate.remove(redisKey.getKey(), objects);
	}

	public boolean zSetAdd(RedisKey redisKey, T value, double score) {
		Assert.notNull(redisKey);
		return zSetTemplate.add(redisKey.getKey(), value, score);
	}

	public Set<T> zSetReverseRange(RedisKey redisKey, long start, long end) {
		Assert.notNull(redisKey);
		return zSetTemplate.reverseRange(redisKey.getKey(), start, end);
	}


	/**
	 * 队列添加
	 *
	 * @param value
	 */
	public Long leftPush(RedisKey redisKey, T value) {
		return redisTemplate.opsForList().leftPush(redisKey.getKey(), value);
	}

	public T leftPop(RedisKey redisKey,long time){
		return redisTemplate.opsForList().leftPop(redisKey.getKey(),time,TimeUnit.SECONDS);
	}

	public Long rightPush(RedisKey redisKey, T value) {
		return redisTemplate.opsForList().rightPush(redisKey.getKey(), value);
	}
	/**
	 * 队列取出
	 */
	public T rightPop(RedisKey redisKey) {
		return redisTemplate.opsForList().rightPop(redisKey.getKey());
	}


	public T rightPop(RedisKey redisKey,long time){
		return redisTemplate.opsForList().rightPop(redisKey.getKey(),time,TimeUnit.SECONDS);
	}



	/**
	 * 取出指定索引的值
	 *
	 * @param redisKey
	 * @param index
	 * @return
	 */
	public T listIndex(RedisKey redisKey, long index) {
		return listIndex(redisKey.getKey(), index);
	}

	public T listIndex(String key, long index) {
		return redisTemplate.opsForList().index(key, index);
	}


}
