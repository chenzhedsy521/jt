package com.jt.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.manage.pojo.ItemCat;
import com.jt.manage.service.ItemCatService;

@Controller
@RequestMapping("/item/cat")
public class ItemCatController {
	@Autowired
	private ItemCatService itemCatService;
	
	//商品分类查询，返回json串
	@RequestMapping("/queryall")
	@ResponseBody	//springmvc会将返回的对象利用jackson提供工具类，把对象转换成json字符串
	public List<ItemCat> queryAll(){
		List<ItemCat> itemCatList = itemCatService.queryAll();
		return itemCatList;
	}
	
	//商品分类树	/item/cat/list，ID参数被easyUI.tree封装，直接看不到；跟踪id值来验证
	//访问地址：/item/cat/list
	@RequestMapping("/list")
	@ResponseBody	//EasyUI.tree js会自动把id拼接提交；第一次id=null，改成0，做为根节点的父节点的值
	public List<ItemCat> queryItemCatList(@RequestParam(defaultValue="0") Integer id){
		//if (null ==id){ id = 0; }
		return itemCatService.queryItemCatList(id);
	}
}
