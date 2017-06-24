package com.jt.manage.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

//前台要求商品分类的返回json串格式类
public class ItemCatResult {
	@JsonProperty(value="data")	//jackson在转换java对象到json串时，把这个字段就重命名了
	private List<?> itemCatDataList;

	public List<?> getItemCatDataList() {
		return itemCatDataList;
	}

	public void setItemCatDataList(List<?> itemCatDataList) {
		this.itemCatDataList = itemCatDataList;
	}


}
