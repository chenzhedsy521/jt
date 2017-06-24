package com.jt.cart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.cart.pojo.Cart;
import com.jt.cart.service.CartService;
import com.jt.common.vo.SysResult;

@Controller
@RequestMapping("/cart")
public class CartController {
	@Autowired
	private CartService cartService;
	
	//我的购物车 http://cart.jt.com/cart/query/{userId}
	@RequestMapping("/query/{userId}")
	@ResponseBody
	public SysResult queryMyCart(@PathVariable Long userId){
		List<Cart> cartList = cartService.queryMyCart(userId);
		
		return SysResult.oK(cartList);
	}
	
	//新增商品到购物车 http://cart.jt.com/cart/save
	@RequestMapping("/save")
	@ResponseBody
	public SysResult save(Cart cart){
		cartService.saveCart(cart);
		return SysResult.oK();
	}
	
	//更新商品数量 http://cart.jt.com/cart/update/num/{userId}/{itemId}/{num}
	@RequestMapping("/update/num/{userId}/{itemId}/{num}")
	@ResponseBody
	public SysResult updateNum(@PathVariable Long userId,@PathVariable Long itemId,@PathVariable Integer num){
		Cart param = new Cart();
		param.setUserId(userId);
		param.setItemId(itemId);
		param.setNum(num);
		
		cartService.updateNum(param);
		return SysResult.oK();
	}
	
	//商品删除 http://cart.jt.com/cart/delete/{userId}/{itemId}
	@RequestMapping("/delete/{userId}/{itemId}")
	@ResponseBody
	public SysResult delete(@PathVariable Long userId,@PathVariable Long itemId){
		Cart param = new Cart();
		param.setUserId(userId);
		param.setItemId(itemId);
		
		cartService.deleteByWhere(param);
		
		return SysResult.oK();
	}
	
}
