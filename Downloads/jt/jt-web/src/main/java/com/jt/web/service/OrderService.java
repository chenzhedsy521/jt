package com.jt.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.web.pojo.Cart;
import com.jt.web.pojo.Order;

@Service
public class OrderService {
	@Autowired
	private HttpClientService httpClientService;
	private ObjectMapper MAPPER = new ObjectMapper();
	
	//访问购物车子系统，获取某个用户的购物车数据
	public List<Cart> queryCartList(Long userId) throws Exception{
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
	
	//访问订单系统，创建订单
	public String saveOrder(Order order) throws Exception{
		//提交以json格式提交
		String url = "http://order.jt.com/order/create";
		String json = MAPPER.writeValueAsString(order);
		String orderId = httpClientService.doPostJson(url, json);
		
		return orderId;
	}
	
	//查询某个订单
	public Order queryByOrderId(String orderId) throws Exception{
		String url = "http://order.jt.com/order/query/"+orderId;
		String jsonData = httpClientService.doGet(url, "utf-8");
		Order _order = MAPPER.readValue(jsonData, Order.class);
		return _order;
	}
}
