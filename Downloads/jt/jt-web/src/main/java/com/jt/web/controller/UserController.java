package com.jt.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.util.CookieUtils;
import com.jt.common.vo.SysResult;
import com.jt.web.pojo.User;
import com.jt.web.service.UserService;
import com.jt.web.threadlocal.UserThreadlocal;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	//转向，用户注册页面 http://www.jt.com/user/register.html
	@RequestMapping("/register")
	public String register(){
		return "register";
	}
	
	//转向，用户登录页面 http://www.jt.com/user/login.html
	@RequestMapping("/login")
	public String login(){
		return "login";
	}
	
	//注册，/service/user/doRegister
	@RequestMapping("/doRegister")
	@ResponseBody
	public SysResult doRegister(User user) throws Exception{
		String username = userService.doRegister(user);
		return SysResult.oK(username);
	}
	
	//登录，/service/user/doLogin
	@RequestMapping("/doLogin")
	@ResponseBody
	public SysResult doLogin(User user, HttpServletRequest request, HttpServletResponse response) throws Exception{
		String ticket = userService.doLogin(user);
		if(StringUtils.isNotEmpty(ticket)){
			//写cookie
			String cookieName = "JT_TICKET";
			CookieUtils.setCookie(request, response, cookieName, ticket);
			//设置完成转向index.html首页，首页调用jt.js，发起ajax请求，按ticket去sso系统中查询
			return SysResult.oK();
		}else{
			return SysResult.build(201, "用户登录失败!");
		}
	}
	
	//登出，/user/logout.html
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response){
		//删除cookie
		String cookieName = "JT_TICKET";
		CookieUtils.deleteCookie(request, response, cookieName);
		
		//清除UserThreadlocal
		UserThreadlocal.set(null);
		
		return "index";
	}
}
