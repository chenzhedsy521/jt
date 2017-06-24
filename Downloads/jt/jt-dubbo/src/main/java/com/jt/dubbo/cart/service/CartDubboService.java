package com.jt.dubbo.cart.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.jt.cart.pojo.Cart;
import com.jt.common.vo.SysResult;

@Path("cart")
@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
@Produces({ContentType.APPLICATION_JSON_UTF_8, ContentType.TEXT_XML_UTF_8})
public interface CartDubboService {
	//按RESTFul形式访问，参数不能接收对象，只能按对象属性接收参数
	
	@GET	//http://cart.jt.com/cart/query/{userId}
	@Path("query")
	public List<Cart> queryMyCart(
			@QueryParam(value = "userId") Long userId);
	
	@POST
	public SysResult saveCart(
			@QueryParam(value = "userId") Long userId,
			@QueryParam(value = "itemId") Long itemId,
			@QueryParam(value = "itemTitle") String itemTitle,
			@QueryParam(value = "itemImage") String itemImage,
			@QueryParam(value = "itemPrice") Long itemPrice,
			@QueryParam(value = "num") Integer num);

	@POST
	public SysResult updateNum(
			@QueryParam(value = "userId") Long userId, 
			@QueryParam(value = "itemId") Long itemId,
			@QueryParam(value = "num") Integer num);

	@GET
	public SysResult deleteCart(
			@QueryParam(value = "userId") Long userId, 
			@QueryParam(value = "itemId") Long itemId);
}
