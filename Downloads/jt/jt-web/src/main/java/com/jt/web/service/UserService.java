package com.jt.web.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.common.util.CookieUtils;
import com.jt.web.pojo.User;

@Service
public class UserService {
	@Autowired
	private HttpClientService httpClientService;
	public static final ObjectMapper MAPPER = new ObjectMapper();
	
	//注册
	public String doRegister(User user) throws Exception{
		String url = "http://sso.jt.com/user/register";
		
		//HttpClient传参方式，它是要求通过map结构
		Map<String,String> params = new HashMap<String,String>();
		params.put("username", user.getUsername());
		params.put("password", user.getPassword());
		params.put("phone", user.getPhone());
		params.put("email", user.getEmail());
		
		String jsonData = httpClientService.doPost(url, params, "utf-8");
		//从中获取某个属性
		JsonNode jsonNode = MAPPER.readTree(jsonData);
		//获取data属性，强转成字符串
		String username = jsonNode.get("data").asText();
		return username;
	}
	
	//登录
	public String doLogin(User user) throws Exception{
		String url = "http://sso.jt.com/user/login";
		Map<String,String> params = new HashMap<String,String>();
		params.put("u", user.getUsername());
		params.put("p", user.getPassword());
		
		String jsonData = httpClientService.doPost(url, params, "utf-8");
		JsonNode jsonNode = MAPPER.readTree(jsonData);
		String ticket = jsonNode.get("data").asText();
		return ticket;
	}
}
