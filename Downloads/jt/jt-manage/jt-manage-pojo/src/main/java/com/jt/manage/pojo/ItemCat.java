package com.jt.manage.pojo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//JPA映射
//类和表的映射，属性和表的字段的映射
@Table(name="tb_item_cat")	//类和表的映射
@JsonIgnoreProperties(ignoreUnknown = true)		//忽略掉不认识的属性
public class ItemCat extends BasePojo{
	@Id	//代表这是一个主键
	@GeneratedValue(strategy=GenerationType.IDENTITY)	//自增主键
	private Long id;
	
	@Column(name="parent_id")	//把下面的属性和数据库表中的字段对应起来
	private Long parentId;
	private String name;
	private Integer status;
	private Integer sortOrder;
	private Boolean isParent;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	public Boolean getIsParent() {
		return isParent;
	}
	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}
	
	//为EasyUI.tree新增方法
	public String getText() {
		return this.getName();
	}
	public String getState() {
		return this.getIsParent()?"closed":"open";	//默认树枝关闭，方便形成异步加载
	}
	
}
