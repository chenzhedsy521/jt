package com.jt.manage.mapper;

import java.util.List;

import com.jt.common.mapper.SysMapper;
import com.jt.manage.pojo.ItemCat;

public interface ItemCatMapper extends SysMapper<ItemCat>{
	//按商品分类某个分支
	public List<ItemCat> queryItemCatList(Integer parentId);
}
