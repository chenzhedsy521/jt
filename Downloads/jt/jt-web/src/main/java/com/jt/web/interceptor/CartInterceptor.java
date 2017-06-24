package com.jt.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.common.util.CookieUtils;
import com.jt.web.pojo.User;
import com.jt.web.threadlocal.UserThreadlocal;

public class CartInterceptor implements HandlerInterceptor{
	@Autowired
	private HttpClientService httpClientService;
	private static final ObjectMapper MAPPER = new ObjectMapper();

	//在调用controller方法之前
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		/*
		 * 步骤：
		 * 1）从cookie中获取ticket
		 * 2）去访问sso系统获取redis中的user.json
		 * 3）user.json转成javaUser对象
		 * 4）User.userId
		 * 5）共享数据，UserThreadLocal
		 */
		String cookieName = "JT_TICKET";
		String ticket = CookieUtils.getCookieValue(request, cookieName);
		if(StringUtils.isNotEmpty(ticket)){
			String url = "http://sso.jt.com/user/query/"+ticket;
			String jsonData = httpClientService.doGet(url, "utf-8");	//SysResult
			if(StringUtils.isNotEmpty(jsonData)){
				JsonNode jsonNode = MAPPER.readTree(jsonData);
				String userJson = jsonNode.get("data").asText();
				
				//当前用户
				User curUser = MAPPER.readValue(userJson, User.class);
				UserThreadlocal.set(curUser);
				return true;	//true放行，false不放行
			}
		}
		
		//转向登录页面
		UserThreadlocal.set(null);
		response.sendRedirect("/user/login.html");
		return false;
	}

	//在调用controller方法之后
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	//在jsp也渲染之前render，在转向jsp之前，把model拿到，遍历map，setRequest.
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
