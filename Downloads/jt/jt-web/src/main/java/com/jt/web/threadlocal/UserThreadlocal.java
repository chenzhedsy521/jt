package com.jt.web.threadlocal;

import com.jt.web.pojo.User;

//UserThreadLocal，方便在拦截器和controller之间共享数据，防止线程安全问题
public class UserThreadlocal {
	private static ThreadLocal<User> USER = new ThreadLocal<User>();

	public static User get() {
		return USER.get();
	}

	public static void set(User user) {
		USER.set(user);
	}
	
	//快速获取到userId
	public static Long getUserId(){
		Long userId = null;
		try{
			userId = USER.get().getId();
		}catch(Exception e){
			//todo
		}
		return userId;
	}
}
