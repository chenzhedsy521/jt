package com.jt.sso.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.RedisService;
import com.jt.sso.mapper.UserMapper;
import com.jt.sso.pojo.User;

@Service
public class UserService extends BaseService<User>{
	@Autowired
	private RedisService redisService;
	@Autowired
	private UserMapper userMapper;
	public static final ObjectMapper MAPPER = new ObjectMapper();
	
	public Boolean check(String param, Integer type){
		Map<String,String> map = new HashMap<String,String>();
		map.put("val", param);
		if(type==1){
			map.put("name", "username");
		}else if(type==2){
			map.put("name", "phone");
		}else if(type==3){
			map.put("name", "email");
		}
		Integer i = userMapper.check(map);
		if(i==0){
			return false;		//用户不存在
		}else{
			return true;		//用户已存在
		}
	}
	
	//注册
	public String register(User user){
		//按md5加密
		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		user.setEmail(user.getPhone());		//对邮件唯一性校验不做，实际中通过代码实现
		
		userMapper.insertSelective(user);
		return user.getUsername();
	}
	
	//登录
	public String login(String username, String password){
		//通用Mapper传where条件，值是否将来拼接到where条件中，这个属性是否为null
		User _user = new User();
		_user.setUsername(username);
		
		//1.按用户名到数据库查询user对象，因为username唯一索引，只有一个对象返回
		User curUser = super.queryByWhere(_user);
		if(null!=curUser){
			String newPassword = DigestUtils.md5Hex(password);	//加密密码
			if(newPassword.equals(curUser.getPassword())){
				//系统用户，写redis
				try {
					String userJson = MAPPER.writeValueAsString(curUser);
					//ticket 唯一性，动态性
					String ticket = DigestUtils.md5Hex("JT_TICKET_"+System.currentTimeMillis()+username);
					
					//设置用户ticket过期时间，一般7天
					redisService.set(ticket, userJson, 60*60*24*7);
					return ticket;
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	//查询当前用户信息
	public String queryByTicket(String ticket){
		return redisService.get(ticket);	//从redis中查询
	}
}
