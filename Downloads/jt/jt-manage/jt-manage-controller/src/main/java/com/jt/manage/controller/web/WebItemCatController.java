package com.jt.manage.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.manage.pojo.ItemCatResult;
import com.jt.manage.service.ItemCatService;

@Controller
@RequestMapping("/web/itemcat")
public class WebItemCatController {
	@Autowired
	private ItemCatService itemCatService;
	//查询所有 http://manage.jt.com/web/itemcat/all?callback=category.getDataService
	@RequestMapping("/all")
	@ResponseBody	//这个结构不动，然后返回还想要jsonp，函数名称传入
	public ItemCatResult queryItemCatList(){
		return itemCatService.queryItemCat();
	}
}
