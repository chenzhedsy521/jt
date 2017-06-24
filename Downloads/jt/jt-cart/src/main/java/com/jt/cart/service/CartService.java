package com.jt.cart.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.cart.mapper.CartMapper;
import com.jt.cart.pojo.Cart;

@Service
public class CartService extends BaseService<Cart>{
	@Autowired
	private CartMapper cartMapper;
	
	public List<Cart> queryMyCart(Long userId){
		return cartMapper.queryMyCart(userId);
	}
	
	//保存购物车
	public void saveCart(Cart cart){
		/*
		 * 步骤：
		 * 1）判断此用户的此商品是否在购物车中
		 * 2）如果不存在，新增
		 * 2）如果存在，修改其数量，在旧的商品的数量上+新增的数量
		 */
		
		//通用Mapper拼接where通过对象属性是否为null
		Cart param = new Cart();
		param.setUserId(cart.getUserId());
		param.setItemId(cart.getItemId());
		
		Cart oldCart = super.queryByWhere(param);
		if(null==oldCart){
			cart.setCreated(new Date());
			cart.setUpdated(cart.getCreated());
			
			cartMapper.insertSelective(cart);
		}else{
			//修改数量= 旧的数量+新的页面数量
			oldCart.setNum(oldCart.getNum() + cart.getNum());
			oldCart.setUpdated(new Date());
			
			cartMapper.updateByPrimaryKeySelective(oldCart);
		}
		
	}
	
	//商品更新数量
	public void updateNum(Cart cart){
		cartMapper.updateNum(cart);
	}
}
