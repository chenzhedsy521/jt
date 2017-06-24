package com.jt.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.web.pojo.Cart;
import com.jt.web.service.CartService;
import com.jt.web.threadlocal.UserThreadlocal;

@Controller
@RequestMapping("/cart")
public class CartController {
	@Autowired
	private CartService cartService;
	
	//我的购物车	/cart/show.html
	@RequestMapping("/show")
	public String show(Model model) throws Exception{
		//准备数据
		//Long userId = 7L;
		Long userId = UserThreadlocal.getUserId();
		
		List<Cart> cartList = cartService.show(userId);
		model.addAttribute("cartList", cartList);
		
		return "cart";
	}
	
	//保存商品到购物车 /cart/add/${item.id}.html
	@RequestMapping("/add/{itemId}")
	public String addCart(@PathVariable Long itemId, Integer num) throws Exception{
		Long userId = 7L;
		cartService.saveCart(userId, itemId, num);
		
		return "redirect:/cart/show.html";	//cart.jsp
	}
	
	//更新商品数量 /service/cart/update/num/562379/7
	@RequestMapping("/update/num/{itemId}/{num}")
	@ResponseBody	//ajax不刷新页面
	public String updateNum(@PathVariable Long itemId,@PathVariable Integer num) throws Exception{
		Long userId = 7L;
		cartService.updateNum(userId, itemId, num);
		
		return "";
	}
	
	//商品删除 http://www.jt.com/cart/delete/562379.html
	@RequestMapping("/delete/{itemId}")
	public String delete(@PathVariable Long itemId) throws Exception{
		Long userId = 7L;
		cartService.delete(userId, itemId);
		
		return "redirect:/cart/show.html";
	}
}
