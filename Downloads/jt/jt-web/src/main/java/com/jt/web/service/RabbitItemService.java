package com.jt.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.common.service.HttpClientService;
import com.jt.common.service.RedisService;

@Service
public class RabbitItemService {
	@Autowired
	private RedisService redisService;
	@Autowired
	private HttpClientService httpClientService;
	
	//在rabbitmq的配置文件中，配置类名，方法名，消息自动封装到参数中，自动转类型。
	public void updateItem(Long itemId){
		//商品详情写入缓存中
		try {
			//按商品ID查询商品详情
			String url = "http://manage.jt.com/item/"+itemId;
			String jsonData = httpClientService.doGet(url, "utf-8");	//Item对象
			
			redisService.set("ITEM_"+itemId, jsonData, 60*60*24*10);
		} catch (Exception e) {
			//todo log
			e.printStackTrace();
		}
	}
}
