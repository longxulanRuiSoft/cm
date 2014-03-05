package com.ruisoft.cm.rbac.action;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ruisoft.cm.rbac.dao.CmMenuDAO;
import com.ruisoft.cm.rbac.util.SysConstants;

@Controller
@RequestMapping("/rbac/cmMenu.do")
public class CmMenuAction extends BaseAction {
	private final static Logger LOG = Logger.getLogger(CmMenuAction.class);
	
	@Resource
	private CmMenuDAO cmMenuDAO = null;
	
	public void setCmMenuDAO(CmMenuDAO cmMenuDAO) {
		this.cmMenuDAO = cmMenuDAO;
	}
	
	@RequestMapping(params = "method=init")
	public String init() {
		HttpSession session = request.getSession();
		
		try {
			String userId = ((JSONObject) session
					.getAttribute(SysConstants.USER_INFO.toString()))
					.getString("uid");
			response(cmMenuDAO.getByUser(userId));
		} catch (Exception e) {
			LOG.error("获取用户操作菜单发生错误", e);
		}
		return null;
	}
}
