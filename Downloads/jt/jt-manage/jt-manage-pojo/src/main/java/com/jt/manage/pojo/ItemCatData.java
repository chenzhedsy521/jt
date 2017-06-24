package com.jt.manage.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

//代表商品分类的每条菜单对象
public class ItemCatData {
	@JsonProperty(value="u")	//解压网络资源，减少传递数据量
	private String url;
	@JsonProperty(value="n")
	private String name;
	@JsonProperty(value="i")
	private List<?> items;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<?> getItems() {
		return items;
	}
	public void setItems(List<?> items) {
		this.items = items;
	}
}
