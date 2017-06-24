package com.jt.manage.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.RedisService;
import com.jt.manage.mapper.ItemDescMapper;
import com.jt.manage.mapper.ItemMapper;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;

@Service
public class ItemService extends BaseService<Item>{
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final Logger LOG = Logger.getLogger(ItemService.class);
	@Autowired
	private RedisService redisService;
	@Autowired
	private ItemMapper itemMapper;
	
	@Autowired
	private ItemDescMapper itemDescMapper;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;	//rabbitmq和spring整合后会自动创建这个对象
	
	//商品列表
	public List<Item> queryItemList(){
		List<Item> itemList = itemMapper.queryItemList();
		return itemList;
	}
	
	//商品新增
	public void saveItem(Item item, String desc){
		//设置默认值
		item.setStatus(1);		//1正常2下架3删除
		item.setCreated(new Date());
		item.setUpdated(item.getCreated());
		
		itemMapper.insertSelective(item);
		
		//新增商品详情
		ItemDesc itemDesc = new ItemDesc();
		//有的，mybatis会回执新插入记录的自增主键值
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(item.getCreated());
		itemDesc.setUpdated(item.getCreated());
		
		itemDescMapper.insert(itemDesc);
		
		//商品详情写入缓存中
//		String ITEM_KEY = "ITEM_"+item.getId();
//		try {
//			redisService.set(ITEM_KEY, MAPPER.writeValueAsString(item), 60*60*24*10);
//		} catch (JsonProcessingException e) {
//			LOG.error(e.getMessage());
//			e.printStackTrace();
//		}
		
		//消息产生者
		//调用MQ，写什么到MQ中？写入MQ的内容越少越好，越少处理的越快。
		//发送消息
		String routingKey = "item.add";
		rabbitTemplate.convertAndSend(routingKey, item.getId());
	}
	
	//商品修改
	public void updateItem(Item item, String desc){
		item.setUpdated(new Date());
		
		itemMapper.updateByPrimaryKeySelective(item);
		
		ItemDesc itemDesc = new ItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(item.getUpdated());
		
		itemDescMapper.updateByPrimaryKeySelective(itemDesc);
		
		//把redis中的key删除
		String ITEM_KEY = "ITEM_"+item.getId();
		redisService.del(ITEM_KEY);
	}
	
	//商品批量删除
	public void deleteItem(Long[] ids){
		//先删除子表，再删除主表
		itemDescMapper.deleteByIDS(ids);
		itemMapper.deleteByIDS(ids);
	}
	
	//获取商品的描述
	public ItemDesc getItemDesc(Long itemId){
		return itemDescMapper.selectByPrimaryKey(itemId);
	}
}
