package com.jt.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.web.pojo.Cart;
import com.jt.web.pojo.Item;

@Service
public class CartService{
	@Autowired
	private HttpClientService httpClientService;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	//http://cart.jt.com/cart/query/{userId}
	public List<Cart> show(Long userId) throws Exception{
		String url = "http://cart.jt.com/cart/query/"+userId;
		String jsonData = httpClientService.doGet(url, "utf-8");
		
		JsonNode jsonNode = MAPPER.readTree(jsonData);
        JsonNode data = jsonNode.get("data");
        Object obj = null;
        if (data.isArray() && data.size() > 0) {
            obj = MAPPER.readValue(data.traverse(),
                    MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
        }
        return (List<Cart>)obj;
	}
	
	//保存商品到购物车  http://cart.jt.com/cart/save
	public void saveCart(Long userId, Long itemId, Integer num) throws Exception{
		//1.调用后台系统，获取3个冗余字段
		String  url = "http://manage.jt.com/item/"+itemId;
		String jsonItem = httpClientService.doGet(url, "utf-8");
		Item item = MAPPER.readValue(jsonItem, Item.class);
		
		//2.调用购物车系统接口，保存商品到购物车
		url = "http://cart.jt.com/cart/save";
		Map<String, String> params = new HashMap<String,String>();
		params.put("userId", userId+"");
		params.put("itemId", itemId+"");
		params.put("num", num+"");
		
		//从后台系统中获取数据，保存到对象中
		params.put("itemTitle", item.getTitle());
		try{
			params.put("itemImage", item.getImage().split(",")[0]);
		}catch(Exception e){
			//todo: 写日志
		}
		params.put("itemPrice", item.getPrice()+"");
		
		httpClientService.doPost(url, params, "utf-8");
	}
	
	//商品更新 http://cart.jt.com/cart/update/num/{userId}/{itemId}/{num}
	public void updateNum(Long userId, Long itemId, Integer num) throws Exception{
		String url = "http://cart.jt.com/cart/update/num/"+userId+"/"+itemId+"/"+num;
		httpClientService.doGet(url, "utf-8");
	}
	
	//商品删除 http://cart.jt.com/cart/delete/{userId}/{itemId}
	public void delete(Long userId, Long itemId) throws Exception{
		String url = "http://cart.jt.com/cart/delete/"+userId+"/"+itemId;
		httpClientService.doGet(url, "utf-8");
	}
	
}
