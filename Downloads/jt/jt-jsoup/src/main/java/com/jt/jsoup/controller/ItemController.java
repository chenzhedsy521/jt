package com.jt.jsoup.controller;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jt.common.service.RedisService;
import com.jt.jsoup.pojo.Item;
import com.jt.jsoup.service.ItemService;
import com.jt.jsoup.util.JDUtil;

@Controller	//利用cotnroller连接来执行
public class ItemController {
	@Autowired
	private RedisService redisService;
	@Autowired
	private ItemService itemService;
	
	//初始化爬虫，把所有的item链接放入redis（分片）。只能执行一次
	@RequestMapping("/init")
	public String init() throws IOException{
		//for(String catPageUrl : JDUtil.getAllCat3()){
			String catPageUrl = "http://list.jd.com/list.html?cat=670,677,678";
			for(String url : JDUtil.getItemUrl(catPageUrl)){
				redisService.set(url, url);
			}
			//break;	//方便测试
		//}
		return "index";
	}
	
	//执行爬虫
	@RequestMapping("/go")
	public String go() throws IOException{
		//for(String catPageUrl : JDUtil.getAllCat3()){
		String catPageUrl = "http://list.jd.com/list.html?cat=670,677,678";
			for(String url : JDUtil.getItemUrl(catPageUrl)){
				String itemUrl = redisService.get(url);
				
				//如果redis中没有了，代表此商品已经处理过了
				if(StringUtils.isNotEmpty(itemUrl)){
					Item item = JDUtil.getItem(itemUrl);
					
					itemService.saveItem(item, item.getDesc());
					redisService.del(url);	//业务完成就删除此key
				}
			}
			//break;	//方便测试
		//}
		return "index";
	}

}
