package com.jt.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.SysResult;
import com.jt.web.pojo.Cart;
import com.jt.web.pojo.Order;
import com.jt.web.pojo.User;
import com.jt.web.service.OrderService;
import com.jt.web.threadlocal.UserThreadlocal;

@Controller
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private OrderService orderService;
	
	//转向订单详情页
	@RequestMapping("/create")
	public String orderCart(Model model) throws Exception{
		//准备数据，当前订单下的商品
		User _user = UserThreadlocal.get();
		List<Cart> cartList = orderService.queryCartList(_user.getId());
		model.addAttribute("carts", cartList);
		
		return "order-cart";
	}
	
	//提交订单，提交后获取的是对象json结构
	@RequestMapping("/submit")
	@ResponseBody
	public SysResult orderSubmit(Order order) throws Exception{
		User _user = UserThreadlocal.get();
		order.setUserId(_user.getId());
		return SysResult.oK(orderService.saveOrder(order));
	}
	
	//转向成功页面 /order/success.html?id="+result.data
	@RequestMapping("/success")
	public String success(@RequestParam("id")String orderId, Model model) throws Exception{
		model.addAttribute("order", orderService.queryByOrderId(orderId));
		
		return "success";
	}
}
