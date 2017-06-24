package com.jt.manage.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;
import com.jt.manage.service.ItemService;

//接收到前台请求
@Controller
public class WebItemController {
	@Autowired
	private ItemService itemService;
	
	//获取商品信息
	@RequestMapping("/item/{itemId}")
	@ResponseBody	//前台httpclient请求，返回json
	public Item getItemById(@PathVariable Long itemId){
		return itemService.queryById(itemId);
	}
	
	//获取商品描述的信息
	@RequestMapping("/item/desc/{itemId}")
	@ResponseBody	//前台httpclient请求，返回json
	public ItemDesc getItemDescById(@PathVariable Long itemId){
		return itemService.getItemDesc(itemId);
	}
	
}
