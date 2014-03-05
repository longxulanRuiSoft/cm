package com.ruisoft.cm.rbac.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ruisoft.cm.rbac.entity.QueryEntity;
import com.ruisoft.cm.rbac.util.SysCache;

public class CmMenuDAO extends BaseDAO {
	private final static Logger LOG = Logger.getLogger(CmMenuDAO.class);
	
	public String getByUser(String userId) throws Exception {
		List<JSONObject> results = query("{\"userId\":\"" + userId + "\"}",
				((QueryEntity) SysCache.get("rbac.select.menu.query")));
		
		// 初始化菜单对象
		String menus = new JSONArray(results).toString();
		LOG.debug(menus);
		
		return menus;
	}
}
