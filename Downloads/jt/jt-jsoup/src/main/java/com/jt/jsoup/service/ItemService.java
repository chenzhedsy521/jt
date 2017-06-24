package com.jt.jsoup.service;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.jsoup.mapper.ItemDescMapper;
import com.jt.jsoup.mapper.ItemMapper;
import com.jt.jsoup.pojo.Item;
import com.jt.jsoup.pojo.ItemDesc;
import com.jt.jsoup.util.JDUtil;

@Service
public class ItemService extends BaseService<Item>{
	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private ItemDescMapper itemDescMapper;
	private static final Logger log = Logger.getLogger(JDUtil.class);
	
	//新增商品
	public void saveItem(Item item, String desc){
		try{
			item.setCreated(new Date());
			item.setUpdated(item.getCreated());
			item.setStatus(1);
			
			itemMapper.insertSelective(item);
			
			//新增商品描述
			ItemDesc itemDesc = new ItemDesc();
			itemDesc.setItemId(item.getId());
			itemDesc.setItemDesc(desc);
			itemDesc.setCreated(item.getCreated());
			itemDesc.setUpdated(item.getCreated());
			
			itemDescMapper.insertSelective(itemDesc);
		}catch(Exception e){
			//内容重复的主键忽略
			log.error(e.getMessage());
		}
	}
}
