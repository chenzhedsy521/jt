package com.jt.manage.controller;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jt.common.vo.EasyUIResult;
import com.jt.common.vo.SysResult;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;
import com.jt.manage.service.ItemService;

@Controller
@RequestMapping("item")
public class ItemController {
	//加log4j/selflog
	private static final Logger log = Logger.getLogger(ItemController.class);
	
	@Autowired
	private ItemService itemService;
	
	//访问：/item/query
	@RequestMapping("query")
	@ResponseBody	//EasyUI.datagrid组件封装当前页page，每页条数rows
	public EasyUIResult query(Integer page, Integer rows){
		PageHelper.startPage(page, rows);	//只是开启进行拦截标识，规范：只拦截这个标识下的第一条查询语句
		List<Item> itemList = itemService.queryItemList();
		//List<Item> itemList2 = itemService.queryItemList();
		
		//同PageInfo对象来封装两个结果，记录总数，当前页的记录；Page支持线程安全
		PageInfo<Item> pageInfo = new PageInfo<Item>(itemList);	//真正的执行者
		
		return new EasyUIResult(pageInfo.getTotal(), pageInfo.getList());
	}
	
	//商品新增 /item/save
	@RequestMapping("save")
	@ResponseBody
	public SysResult saveItem(Item item, String desc){
		try{	//ajax不抛出异常，记录错误信息，前台js去根据返回值判断提示成功还是失败！
			itemService.saveItem(item, desc);
			return SysResult.oK();
		}catch(Exception e){
			String msg = "新增错误："+e.getMessage();
			log.debug(msg);		//出错一定要输出，保存日志文件中
			return SysResult.build(201, msg);
		}
	}
	
	//商品修改 /item/update
	@RequestMapping("/update")
	@ResponseBody
	public SysResult updateItem(Item item, String desc){
		try{
			itemService.updateItem(item, desc);
			return SysResult.oK();
		}catch(Exception e){
			log.debug(e.getMessage());
			return SysResult.build(201, e.getMessage());
		}
	}
	
	//商品批量删除 /item/delete
	@RequestMapping("/delete")
	@ResponseBody
	public SysResult deleteItem(Long[] ids){
		try{
			itemService.deleteItem(ids);
			return SysResult.oK();
		}catch(Exception e){
			log.debug(e.getMessage());
			return SysResult.build(201, e.getMessage());
		}
	}
	
	//获取商品的详情	http://manage.jt.com/item/query/item/desc/1474391953 
	@RequestMapping("/query/item/desc/{itemId} ")
	@ResponseBody
	public SysResult getItemDesc(@PathVariable Long itemId){
		ItemDesc itemDesc = itemService.getItemDesc(itemId);
		return SysResult.oK(itemDesc);
	}
}
