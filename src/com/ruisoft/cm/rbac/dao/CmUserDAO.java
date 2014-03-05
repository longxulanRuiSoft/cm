package com.ruisoft.cm.rbac.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class CmUserDAO extends BaseDAO {
	private final static Logger LOG = Logger.getLogger(CmUserDAO.class);
	
	public JSONObject login(JSONObject json) throws Exception {
		LOG.debug("µÇÂ¼²Ù×÷£º" + json.getString("uname"));
		
		List<JSONObject> results = query(json, "rbac.select.user.login");
		
		if (results.isEmpty())
			return null;
		
		return results.get(0);
	}
}
