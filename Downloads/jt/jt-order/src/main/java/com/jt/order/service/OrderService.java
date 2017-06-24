package com.jt.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.order.mapper.OrderMapper;
import com.jt.order.pojo.Order;

@Service
public class OrderService extends BaseService<Order>{
	@Autowired
	private OrderMapper orderMapper;
	
	//新增订单
	public String saveOrder(Order order){
		//产生一个订单号：userId+currentTime
		String orderId = order.getUserId()+""+System.currentTimeMillis();
		order.setOrderId(orderId);
		
		orderMapper.orderCreate(order);
		return orderId;
	}
	
	//根据订单好查询某个订单
	public Order queryByOrderId(String orderId){
		Order order = orderMapper.orderByOrderId(orderId);
		return order;
	}
}
