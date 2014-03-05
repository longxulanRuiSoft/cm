package com.ruisoft.cm.rbac.util;

import java.util.Map;

import com.ruisoft.cm.rbac.entity.AddEntity;
import com.ruisoft.cm.rbac.entity.DeleteEntity;
import com.ruisoft.cm.rbac.entity.QueryEntity;
import com.ruisoft.cm.rbac.entity.UpdateEntity;

public class DMLConfig {
	public Map<String, QueryEntity> select = null;

	public Map<String, QueryEntity> getSelect() {
		return select;
	}

	public void setSelect(Map<String, QueryEntity> select) {
		this.select = select;
	}
	
	public Map<String, AddEntity> add = null;

	public Map<String, AddEntity> getAdd() {
		return add;
	}

	public void setAdd(Map<String, AddEntity> add) {
		this.add = add;
	}
	
	public Map<String, UpdateEntity> update = null;

	public Map<String, UpdateEntity> getUpdate() {
		return update;
	}

	public void setUpdate(Map<String, UpdateEntity> update) {
		this.update = update;
	}
	
	public Map<String, DeleteEntity> delete = null;

	public Map<String, DeleteEntity> getDelete() {
		return delete;
	}

	public void setDelete(Map<String, DeleteEntity> delete) {
		this.delete = delete;
	}
}
