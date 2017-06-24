package com.jt.web.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.common.service.RedisService;
import com.jt.web.pojo.Item;
import com.jt.web.pojo.ItemDesc;

@Service
public class ItemService {
	private static final Logger log = Logger.getLogger(ItemService.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();
	@Autowired
	private HttpClientService httpClientService;
	@Autowired
	private RedisService redisService;
	
	public Item getItem(Long itemId){
		//访问后台的请求，获取到json串。httpClient
		String url = "http://manage.jt.com/item/"+itemId;
		try {
			//请求redis，如果有数据就直接返回，如果没有数据执行业务
			String ITEM_KEY = "ITEM_"+itemId;	//习惯设置10天，利用redis自动清除非常高，效率高
			String jsonData = redisService.get(ITEM_KEY);
			if(StringUtils.isNotEmpty(jsonData)){
				Item item = MAPPER.readValue(jsonData, Item.class);
				return item;
			}
			
			//发起httpClient请求，从后台加载数据，返回json串；断点可能时间过长，超时错误。
			jsonData = httpClientService.doGet(url, "utf-8");
			
			//保存数据到redis中
			redisService.set(ITEM_KEY, jsonData, 60*60*24*7);	//编译器自动计算出结果
			
			//把json串数据转换java对象，单个
			Item item = MAPPER.readValue(jsonData, Item.class);
			return item;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ItemDesc getItemDescById(Long itemId){
		try {
			String url = "http://manage.jt.com/item/desc/"+itemId;
			String jsonData = httpClientService.doGet(url, "utf-8");
			ItemDesc itemDesc = MAPPER.readValue(jsonData, ItemDesc.class);
			return itemDesc;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
