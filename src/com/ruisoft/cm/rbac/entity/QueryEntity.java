package com.ruisoft.cm.rbac.entity;

import org.json.JSONException;
import org.json.JSONObject;


public class QueryEntity extends DMLEntity {
	public QueryEntity() {
		dmlType = QUERY_NORMAL;
	}
	
	private String order = null;
	
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
	private boolean desc = false;

	public boolean isDesc() {
		return desc;
	}

	public void setDesc(boolean desc) {
		this.desc = desc;
	}

	private String group = null;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getSql(JSONObject json) throws JSONException {
		String sql = super.getSql(json);
		
		if (order != null && !"".equals(order)) {
			sql = sql.concat(" ORDER BY ").concat(order);
			if (isDesc())
				sql = sql.concat(" DESC");
		}
		if (group != null && !"".equals(group))
			sql = sql.concat(" GROUP BY ").concat(group);
		
		return sql;
	}
}