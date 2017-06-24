package com.jt.sso.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.SysResult;
import com.jt.sso.pojo.User;
import com.jt.sso.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	//用户检查 http://sso.jt.com/user/check/{param}/{type}
	@RequestMapping("/check/{param}/{type}")	
	@ResponseBody
	public SysResult check(@PathVariable String param,@PathVariable Integer type){
		try{
			Boolean b = userService.check(param, type);
			return SysResult.oK(b);
		}catch(Exception e){
			//todo
			return SysResult.build(201, e.getMessage());
		}
	}
	
	//用户注册 http://sso.jt.com/user/register
	@RequestMapping("/register")	
	@ResponseBody
	public SysResult register(User user){
		String username = userService.register(user);
		return SysResult.oK(username);
	}
	
	//用户登录 http://sso.jt.com/user/login
	@RequestMapping("/login")	
	@ResponseBody
	public SysResult login(String u, String p){
		String ticket = userService.login(u, p);
		if(StringUtils.isNotEmpty(ticket)){
			return SysResult.oK(ticket);
		}else{
			return SysResult.build(201, "用户登录失败!");
		}
	}
	
	//查询当前用户信息 http://sso.jt.com/user/query/{ticket}
	@RequestMapping("/query/{ticket}")	
	@ResponseBody
	public SysResult queryByTicket(@PathVariable String ticket){
		String userJson = userService.queryByTicket(ticket);
		return SysResult.oK(userJson);
	}
}
