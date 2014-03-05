package com.ruisoft.core.param;

import com.ruisoft.cm.rbac.entity.QueryEntity;

public class SQLParamEntity implements ParamEntity {
	/** 查询SQL */
	private QueryEntity sql = null;

	public QueryEntity getSql() {
		return sql;
	}

	public void setSql(QueryEntity sql) {
		this.sql = sql;
	}
	
	/** 构造树型结构的属性名称  */
	private String treeAttr = "";

	public String getTreeAttr() {
		return treeAttr;
	}

	public void setTreeAttr(String treeAttr) {
		this.treeAttr = treeAttr;
	}
	
	/** 是否需要缓存 */
	private boolean cache = false;

	public boolean isCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}
}
