package com.jt.manage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.RedisSentinelService;
import com.jt.common.service.RedisService;
import com.jt.manage.mapper.ItemCatMapper;
import com.jt.manage.pojo.ItemCat;
import com.jt.manage.pojo.ItemCatData;
import com.jt.manage.pojo.ItemCatResult;

import redis.clients.jedis.JedisCluster;


@Service
public class ItemCatService extends BaseService<ItemCat>{
	@Autowired
	private ItemCatMapper itemCatMapper;
	@Autowired
	private RedisService redisService;	//引入redis支持
	@Autowired(required=false)
	private RedisSentinelService redisSentinelService;	//引入哨兵支持
	@Autowired(required=false)
	private JedisCluster jedisCluster;	//引入redisCluster支持
	
	//引入jackson对Json格式转换支持
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final Logger log = Logger.getLogger(ItemCatService.class);
	
	//查询某个分支商品分类
	public List<ItemCat> queryItemCatList(Integer parentId){
		String ITEM_CAT_KEY = "ITEM_CAT"+parentId;
		//1.读取缓存
		String jsonData = redisService.get(ITEM_CAT_KEY);
		//String jsonData = redisSentinelService.get(ITEM_CAT_KEY);
		//String jsonData = jedisCluster.get(ITEM_CAT_KEY);
		if(StringUtils.isNotEmpty(jsonData)){
			//把json字符串转成java对象
			JsonNode jsonNode;
			try {
				jsonNode = MAPPER.readTree(jsonData);
				//把json格式现在换成JsonNode
				List<ItemCat> itemCatList = MAPPER.readValue(jsonNode.traverse(),
						MAPPER.getTypeFactory().constructCollectionType(List.class, ItemCat.class));
				return itemCatList;
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}	
		}else{
			List<ItemCat> itemCatList = itemCatMapper.queryItemCatList(parentId);
			//2.写缓存
			String json;
			try {	//不能抛出异常，缓存如果出错，应该就去数据库查找
				json = MAPPER.writeValueAsString(itemCatList);
				redisService.set(ITEM_CAT_KEY, json);
				//redisSentinelService.set(ITEM_CAT_KEY, json);
				//jedisCluster.set(ITEM_CAT_KEY, json);
				return itemCatList;
			} catch (JsonProcessingException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	//为前台系统构建json串对象结构
	public ItemCatResult queryItemCat(){
		//声明存储的对象
		ItemCatResult result = new ItemCatResult();
		List<ItemCat> cats = super.queryAll();		//查询所有3级餐单
		
		//获取当前菜单下的所有的子菜单，形成一个数组
		Map<Long,List<ItemCat>> map = new HashMap<Long,List<ItemCat>>();
		for(ItemCat itemCat: cats){
			if(!map.containsKey(itemCat.getParentId())){
				//创建一个元素，元素内容
				map.put(itemCat.getParentId(), new ArrayList<ItemCat>());
			}
			map.get(itemCat.getParentId()).add(itemCat);
		}
		
		//构建3级菜单结构
		List<ItemCatData> itemCatDataList1 = new ArrayList<ItemCatData>();
		//为一级菜单构建它的所有子菜单
		for(ItemCat itemCat1 : map.get(0L)){		//遍历一级菜单
			ItemCatData itemCatData1 = new ItemCatData();
			itemCatData1.setUrl("/products/"+itemCat1.getId()+".html");
			itemCatData1.setName("<a href='/products/"+itemCat1.getId()+".html'>"+itemCat1.getName()+"</a>");
			
			//遍历二级菜单
			List<ItemCatData> itemCatDataList2 = new ArrayList<ItemCatData>();
			for(ItemCat itemCat2: map.get(itemCat1.getId())){
				ItemCatData itemCatData2 = new ItemCatData();
				itemCatData2.setUrl("/products/"+itemCat2.getId()+".html");
				itemCatData2.setName(itemCat2.getName());
				
				//遍历三级菜单
				//三级菜单只是一个字符串，和一级、二级结构不同
				List<String> itemCatDataList3 = new ArrayList<String>();
				for(ItemCat itemCat3 : map.get(itemCat2.getId())){
					itemCatDataList3.add("/products/"+itemCat3.getId()+".html|"+itemCat3.getName());
				}
				itemCatData2.setItems(itemCatDataList3);
				
				itemCatDataList2.add(itemCatData2);
			}
			itemCatData1.setItems(itemCatDataList2);
			
			itemCatDataList1.add(itemCatData1);
			
			//首页菜单要求只返回14条
			if(itemCatDataList1.size()>14){
				break;
			}
		}
		result.setItemCatDataList(itemCatDataList1);
		
		return result;
	}
}
