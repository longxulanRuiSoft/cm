package com.ruisoft.cm.rbac.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.ruisoft.cm.rbac.dao.BaseDAO;
import com.ruisoft.cm.rbac.entity.KeyEntity;

public class KeyGenerator {
	private static final Logger LOG = Logger.getLogger(KeyGenerator.class);
	
	private static final String KEYGEN_QUERY = "rbac.select.key.generator.query";
	
	private static final String KEYGEN_ADD = "rbac.add.key.generator.add";
	
	private static final String KEYGEN_RESET = "rbac.update.key.generator.reset";
	
	private static final String KEYGEN_UPDATE_MAX = "rbac.update.key.generator.update_max";

	@Resource
	private BaseDAO baseDAO = null;
	
	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}
	
	@Resource
	private HttpServletRequest request = null;

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	private static Map<String, KeyEntity> keyConfig = null;
	
	public void setKeyConfig(Map<String, KeyEntity> keyConfig) {
		KeyGenerator.keyConfig = keyConfig;
	}
	
	public static synchronized final String getUUID() {
		return UUID.randomUUID().toString();
	}
	
	public static synchronized final String get32UUID() {
		return getUUID().replaceAll("-", "");
	}
	
	public String getKeyByRule(String keyId, JSONObject values)
			throws Exception {
		if (!keyConfig.containsKey(keyId))
			throw new IllegalArgumentException("KEYID[".concat(keyId).concat(
					"]不存在"));
		
		KeyEntity entity = keyConfig.get(keyId);
		char[] rule = entity.getRule().toCharArray();
		StringBuffer sign = new StringBuffer();
		StringBuffer key = new StringBuffer();
		// 单引号的优先级高于{}
		String type = null;
		for (char r : rule) {
			if (r == '\'') {
				if ("text".equals(type)) {
					// 文本结束
					key.append(sign);
					type = null;
					sign.delete(0, sign.length());
				} else {
					// 文本起始
					type = "text";
				}
			} else if (r == '{' && type == null) {
				// 变量起始
				type = "arg";
			} else if (r == '}' && "arg".equals(type)) {
				// 变量结束
				key.append(invokeArg(sign.toString(), keyId, values));
				type = null;
				sign.delete(0, sign.length());
			} else {
				sign.append(r);
			}
		}
		
		return key.toString();
	}
	
	private String invokeArg(String arg, String keyId, JSONObject values)
			throws Exception {
		LOG.debug("arg = " + arg);
		
		if (arg.startsWith("date")) {
			String format = "yyyyMMdd";
			if (arg.contains("("))
				format = arg.replaceAll("date\\s*\\(\\s*|\\s*\\)\\s*", "");
			return getDate(format);
		} else if ("year".equals(arg)) {
			return getDate("yyyy");
		} else if ("month".equals(arg)) {
			return getDate("MM");
		} else if ("day".equals(arg)) {
			return getDate("dd");
		} else if ("org".equals(arg)) {
			return getOrg();
		} else if (arg.startsWith("seq")) {
			return getSeq(arg, keyId);
		} else if ("uuid".equals(arg)) {
			return getUUID();
		} else if ("uuid32".equals(arg)) {
			return get32UUID();
		} else if ("num".equals(arg)) {
			return getSeq("seq", keyId);
		} else if (arg.startsWith("$")) {
			return values.getString(arg.substring(1));
		}
		
		return null;
	}
	
	private String getDate(String format) {
		SimpleDateFormat date = new SimpleDateFormat(format); 
		return date.format(new Date());
	}
	
	private String getOrg() {
		try {
			return ((JSONObject) request.getSession().getAttribute(
					SysConstants.USER_INFO.toString())).getString("oid");
		} catch (JSONException e) {
			LOG.error("获取当前机构发生错误", e);
		}
		return null;
	}
	
	private String getSeq(String seq, String keyId) {
		int len = 0;
		if (seq.contains("(")) {
			len = Integer.valueOf(seq.replaceAll("seq\\s*\\(\\s*|\\s*\\)\\s*", ""));
		}
		
		try {
			JSONObject json = new JSONObject();
			json.put("keyid", keyId);
			
			KeyEntity entity = keyConfig.get(keyId);
			String orgId = "";
			if (entity.isGroup()) {
				orgId = getOrg();
			}
			json.put("orgid", orgId);
			
			List<JSONObject> keyDef = baseDAO.query(json, KEYGEN_QUERY);
			
			String val = null;
			String circle = entity.getCircle();
			String c = getCircleVal(circle);
			if (keyDef.isEmpty()) {
				// 首次获取，添加记录
				val = String.valueOf(entity.getInit());
				addNewKey(keyId, c);
			} else {
				json = keyDef.get(0);
				String cCircle = json.getString("circle");
				
				if (!"none".equals(circle)) {
					// 判断是否需要重新计数
					boolean isReset = false;
					if (("year".equals(circle) || "month".equals(circle) || "day"
							.equals(circle)) && !c.equals(cCircle)) {
						isReset = true;
					} else if ("def".equals(circle)) {
						// 自定义计数
						
					}
					if (isReset) {
						resetCircle(keyId, orgId, c);
						val = String.valueOf(entity.getInit());
					}
				}
				if (val == null) {
					val = String.valueOf(json.getInt("max_val") + entity.getStep());
					updateMaxVal(keyId, orgId, entity.getStep());
				}
			}
			return fillStr(val, entity.getFillChar(), len);
		} catch (Exception e) {
			LOG.error("生成序号发生错误", e);
		}
		return null;
	}
	
	private String getCircleVal(String circle) {
		if ("year".equals(circle))
			return getDate("yyyy");
		else if ("month".equals(circle))
			return getDate("MM");
		else if ("day".equals(circle))
			return getDate("dd");
		return "";
	}
	
	private synchronized boolean resetCircle(String keyId, String orgId,
			String newCircle) {
		try {
			JSONObject values = new JSONObject();
			values.put("maxval", keyConfig.get(keyId).getInit());
			values.put("new_circle", newCircle);
			values.put("keyid", keyId);
			values.put("orgid", orgId);
			return (baseDAO.update(values, KEYGEN_RESET) > 0);
		} catch (Exception e) {
			LOG.error("重置计数周期发生错误", e);
		}
		
		return false;
	}
	
	private String fillStr(String seq, String filler, int len) {
		int oLen = seq.length();
		if (oLen >= len)
			return seq;
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < len - oLen; i++) {
			str.append(filler);
		}
		
		return str.append(seq).toString();
	}
	
	private synchronized boolean addNewKey(String keyId, String circleVal) {
		KeyEntity entity = keyConfig.get(keyId);
		
		try {
			JSONObject values = new JSONObject();
			values.put("keyid", keyId);
			values.put("maxval", entity.getInit());
			values.put("circle", circleVal);
			if (entity.isGroup())
				values.put("orgid", getOrg());
			else
				values.put("orgid", "");
			values.put("desc", entity.getDescription());
			
			return (baseDAO.add(values, KEYGEN_ADD).length() > 0);
		} catch (Exception e) {
			LOG.error("新增主键生成器配置发生错误", e);
		}
		return false;
	}
	
	private synchronized boolean updateMaxVal(String keyId, String orgId,
			int step) {
		try {
			JSONObject values = new JSONObject();
			values.put("incr", step);
			values.put("keyid", keyId);
			values.put("orgid", orgId);
			return (baseDAO.update(values, KEYGEN_UPDATE_MAX) > 0);
		} catch (Exception e) {
			LOG.error("更新主键最大值发生错误", e);
		}
		
		return false;
	}
}
