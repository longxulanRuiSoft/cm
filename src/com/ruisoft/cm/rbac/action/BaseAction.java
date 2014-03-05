package com.ruisoft.cm.rbac.action;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ruisoft.cm.rbac.util.JSONMap;
import com.ruisoft.cm.rbac.util.JSONUtil;

public abstract class BaseAction {
	private static final Logger LOG = Logger.getLogger(BaseAction.class);
	
	protected HttpServletRequest request = null;
	
	protected HttpSession session = null;
	
	protected HttpServletResponse response = null;

	@ModelAttribute
	protected void setContext(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.session = request.getSession();
	}
	
	protected final void response(String json)
			throws IOException {
		new JSONUtil().outputJSON(response, json);
	}
	
	protected final void response(JSONMap<? extends Object, ? extends Object> json)
			throws IOException {
		new JSONUtil().outputJSON(response, json);
	}

	public static final Pattern PAT_PARAM = Pattern.compile("^(?:[^=&]+=[^=&]*&)*(?:[^=&]+=[^=&]*)$");
	
	protected JSONObject getReqData() throws Exception {
		String info = request.getReader().readLine();
		LOG.debug("ÇëÇó²ÎÊý£º" + info);
		
		if (PAT_PARAM.matcher(info).matches()) {
			String[] param = info.split("&");
			String k = null, v = null;
			JSONObject json = new JSONObject();
			for (String p : param) {
				try {
					k = p.split("=")[0];
					v = p.split("=")[1];
				} catch (ArrayIndexOutOfBoundsException e) {
					v = "";
				} finally {
					json.put(k, v);
				}
			}
			return json;
		}

		return new JSONObject(info);
	}
}
