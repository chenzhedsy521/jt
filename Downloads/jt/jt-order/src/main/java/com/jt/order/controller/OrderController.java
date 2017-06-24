package com.jt.order.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.order.pojo.Order;
import com.jt.order.service.OrderService;

@Controller
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private OrderService orderService;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	//新增订单 http://order.jt.com/order/create
	@RequestMapping("/create")
	//获取请求中的json格式
	@ResponseBody
	public String orderCreate(@RequestBody String json) throws JsonParseException, JsonMappingException, IOException{	//获取json格式请求参数
		Order _order = MAPPER.readValue(json, Order.class);
		String orderId = orderService.saveOrder(_order);
		return orderId;
	}
	
	//查询订单 http://order.jt.com/order/query/81425700649826
	@RequestMapping("/query/{orderId}")
	@ResponseBody
	public Order queryByOrderId(@PathVariable String orderId){
		return orderService.queryByOrderId(orderId);
	}
}
