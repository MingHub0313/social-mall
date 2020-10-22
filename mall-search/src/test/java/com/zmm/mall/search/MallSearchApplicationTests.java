package com.zmm.mall.search;

import com.alibaba.fastjson.JSON;
import com.zmm.mall.search.config.MallElasticSearchConfig;
import com.zmm.mall.search.enums.AggregationFunctionEnums;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;

import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MallSearchApplicationTests {

	@Autowired
	private RestHighLevelClient restHighLevelClient;


	@Test
	public void contextLoads() throws IOException {
		System.out.println(restHighLevelClient);
		//log.info(restHighLevelClient);

		GetRequest getRequest = new GetRequest("bank","1");
		boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
		log.info("是否存在{}",exists);
	}


	/**
	 * 指定 [索引名]判断是否存在
	 * @throws IOException
	 */
	@Test
	public void indexExists() throws IOException {
		GetIndexRequest request = new GetIndexRequest("bank","product");
		IndicesClient indices = restHighLevelClient.indices();

		boolean exists = indices.exists(request, RequestOptions.DEFAULT);

		log.info("是否存在{}",exists);
		log.info("{},{}",Math.round(-11.6),Math.round(-11.2));
	}


	@Test
	public void searchAggregationData() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("bank");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));

		// 1.1按照年龄的值分布进行聚合
		TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(AggregationFunctionEnums.AGE_AGG.getName()).field("age").size(10);
		searchSourceBuilder.aggregation(aggregationBuilder);
		// 1.2计算平均薪资
		AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg(AggregationFunctionEnums.BALANCE_AVG.getName()).field("balance");
		searchSourceBuilder.aggregation(avgAggregationBuilder);

		//
		searchRequest.source(searchSourceBuilder);
		log.info(searchSourceBuilder.toString());
		/**
		 * {
		 *   "query": {
		 *     "match": {
		 *       "address": {
		 *         "query": "mill"
		 *       }
		 *     }
		 *   },
		 *   "aggregations": {
		 *     "ageAgg": {
		 *       "terms": {
		 *         "field": "age",
		 *         "size": 10
		 *         ]
		 *       }
		 *     },
		 *     "balanceAvg": {
		 *       "avg": {
		 *         "field": "balance"
		 *       }
		 *     }
		 *   }
		 * }
		 */
		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		log.info(searchResponse.toString());
		SearchHits searchHits = searchResponse.getHits();
		TotalHits totalHits = searchHits.getTotalHits();
		long value = totalHits.value;
		log.info("总计数量{}",value);
		List<Account> accountList = new ArrayList<>();
		SearchHit[] hits = searchHits.getHits();
		for (SearchHit searchHit : hits){
			log.info(searchHit.toString());
			String sourceAsString = searchHit.getSourceAsString();
			Account account = JSON.parseObject(sourceAsString, Account.class);
			log.info("account 对象--->{}",account);
			accountList.add(account);
		}

		// 3.2)获取分析数据
		Aggregations aggregations = searchResponse.getAggregations();
		List<Aggregation> aggregationList = aggregations.asList();
		for (Aggregation aggregation:aggregationList) {
			String aggregationName = aggregation.getName();
			log.info("当前聚合的名称--->{}",aggregationName);
			// TODO 制定统一的规则 -----> 定义聚合函数名称
			Integer type = 0;
			if (aggregationName.equals(AggregationFunctionEnums.AGE_AGG.getName())){
				type = AggregationFunctionEnums.AGE_AGG.getCode();
			} else if (aggregationName.equals(AggregationFunctionEnums.BALANCE_AVG.getName())){
				type = AggregationFunctionEnums.BALANCE_AVG.getCode();
			}
			switch (type){
				case 1:
					Terms terms = aggregations.get(AggregationFunctionEnums.AGE_AGG.getName());
					for (Terms.Bucket bucket : terms.getBuckets()) {
						String keyAsString = bucket.getKeyAsString();
						log.info("年龄:--->{}===>存在{}个",keyAsString,bucket.getDocCount());
						//log.info("存在几个{}",bucket.getDocCount());
					}
					break;
				case 2:
					Avg balanceAvg = aggregations.get(AggregationFunctionEnums.BALANCE_AVG.getName());
					log.info("平均薪资:--->{}",balanceAvg.getValue());
					break;
				case 0:
					log.error("一个都没有!!!!");
					break;
					default:
			}

		}

	}

	@Test
	public void searchData() throws IOException {
		// 1.创建检索请求
		SearchRequest searchRequest = new SearchRequest();
		// 指定查询索引
		searchRequest.indices("bank");
		// 指定 DSL 检索条件
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// 1.1) 检索条件拼接
		// searchSourceBuilder.query(QueryBuilders.matchAllQuery())
		searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(5);
		log.info(searchSourceBuilder.toString());
		/**
		 * {
		 *   "from": 0,
		 *   "size": 5,
		 *   "query": {
		 *     "match": {
		 *       "address": {
		 *         "query": "mill",
		 *         "operator": "OR",
		 *         "prefix_length": 0,
		 *         "max_expansions": 50,
		 *         "fuzzy_transpositions": true,
		 *         "lenient": false,
		 *         "zero_terms_query": "NONE",
		 *         "auto_generate_synonyms_phrase_query": true,
		 *         "boost": 1.0										==>关联相关性得分 默认都是 1.0
		 *       }
		 *     }
		 *   }
		 * }
		 */
		searchRequest.source(searchSourceBuilder);

		// 2.执行检索
		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

		// 3.分析结果 searchResponse
		log.info(searchResponse.toString());

		/**
		 * {
		 *   "took": 4,
		 *   "timed_out": false,
		 *   "_shards": {
		 *     "total": 1,
		 *     "successful": 1,
		 *     "skipped": 0,
		 *     "failed": 0
		 *   },
		 *   "hits": {
		 *     "total": {
		 *       "value": 4,
		 *       "relation": "eq"
		 *     },
		 *     "max_score": 5.4032025,
		 *     "hits": [
		 *       {
		 *         "_index": "bank",
		 *         "_type": "account",
		 *         "_id": "970",
		 *         "_score": 5.4032025,
		 *         "_source": {
		 *           "account_number": 970,
		 *           "balance": 19648,
		 *           "firstname": "Forbes",
		 *           "lastname": "Wallace",
		 *           "age": 28,
		 *           "gender": "M",
		 *           "address": "990 Mill Road",
		 *           "employer": "Pheast",
		 *           "email": "forbeswallace@pheast.com",
		 *           "city": "Lopezo",
		 *           "state": "AK"
		 *         }
		 *       },
		 *       }
		 *     ]
		 *   }
		 * }
		 */
	}

	/**
	 * 存储数据到 es中
	 */
	@Test
	public void indexData() throws IOException {

		IndexRequest indexRequest = new IndexRequest("users");
		indexRequest.id("1");
		// 方式一
		// indexRequest.source("userName","zhangsan","age",18,"gender","男")

		//方式二
		User user = new User();
		user.setAge(18);
		user.setGender("男");
		user.setUserName("张三");
		String jsonString = JSON.toJSONString(user);
		// 要保存的数据
		indexRequest.source(jsonString, XContentType.JSON);

		// 执行操作
		IndexResponse index = restHighLevelClient.index(indexRequest, MallElasticSearchConfig.COMMON_OPTIONS);

		//提取有用的响应数据
		log.info(index.toString());

		/**
		 * IndexResponse[
		 *   index=users,
		 *   type=_doc,
		 *   id=1,
		 *   version=1,
		 *   result=created,
		 *   seqNo=0,
		 *   primaryTerm=1,
		 *   shards={
		 *     "total": 2,
		 *     "successful": 1,
		 *     "failed": 0
		 *   }
		 * ]
		 */
	}

	@ToString
	@Data
	static class Account {
		private int account_number;
		private int balance;
		private String firstname;
		private String lastname;
		private int age;
		private String gender;
		private String address;
		private String employer;
		private String email;
		private String city;
		private String state;
	}

	@Data
	class User {
		private String userName;
		private String gender;
		private Integer age;
	}
}
