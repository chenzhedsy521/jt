package com.jt.order.mapper;

import java.util.Date;

import com.jt.common.mapper.SysMapper;
import com.jt.order.pojo.Order;

public interface OrderMapper extends SysMapper<Order>{
	public void orderCreate(Order order);
	public Order orderByOrderId(String orderId);
	
	//配合未支付訂單定時任務
	public void paymentOrderScan(Date date);
}
