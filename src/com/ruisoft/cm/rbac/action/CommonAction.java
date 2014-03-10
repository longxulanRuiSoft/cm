package com.ruisoft.cm.rbac.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ruisoft.cm.rbac.dao.BaseDAO;
import com.ruisoft.cm.rbac.util.JSONMap;
import com.ruisoft.cm.rbac.util.SysCache;

@Controller
@RequestMapping("/rbac/cm.do")
public class CommonAction extends BaseAction {
	private static final Logger LOG = Logger.getLogger(CommonAction.class);
	
	@Resource
	protected BaseDAO baseDAO = null;
	
	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}
	
	private static final String RESPONSE_STR = "{\"Rows\":{rows},\"Total\":\"{total}\"}";
	
	private JSONObject reqData = null;
	
	@RequestMapping(params = "m=c")
	public void dml() {
		try {
			reqData = getReqData();
			String type = reqData.getString("t");
			if ("q".equals(type))
				query();
			else if ("a".equals(type))
				add();
			else if ("u".equals(type))
				update();
			else if ("d".equals(type))
				delete();
		} catch (Exception e) {
			LOG.error("Common DML Error!", e);
		}
	}
	
	@RequestMapping(params = "m=q")
	public void query() {
		JSONMap<String, Object> returnJson = new JSONMap<String, Object>(JSONMap.TYPE.OBJECT);
		try {
			if (reqData == null)
				reqData = getReqData();
			// 查询实体名称
			String entityName = reqData.getString("en");
			if (entityName == null || SysCache.get(entityName) == null) {
				returnJson.put("status", "-1");
				returnJson.put("msg", "没有指定查询实体名称");
			} else {
				// l-list 线性结构，默认；t-tree 树型结构
				String returnType = reqData.getString("rt");
				// 是否分页
				boolean isPager = reqData.getBoolean("p");
				// 当前页码
				int currentPage = reqData.isNull("cp") ? 1 : reqData.getInt("cp");
				// 每页记录数
				int pageSize = reqData.isNull("ps") ? 20 : reqData.getInt("ps");
				
				List<JSONObject> result = null;
				String rows = null, total = "0";
				if (isPager) {
					result = baseDAO.queryForPage(entityName,
							reqData.getString("data"), currentPage, pageSize);
					total = result.get(0).getString("total");
					result.remove(0);
				} else {
					result = baseDAO.query(reqData.getString("data"),
							entityName);
				}
				
				if ("t".equalsIgnoreCase(returnType)) {
					rows = treeData(result);
				} else {
					rows = new JSONArray(result).toString();
				}
				
				returnJson.put("status", "1");
				returnJson.put("data",
						RESPONSE_STR.replaceFirst("\\{total\\}", total)
							.replaceFirst("\\{rows\\}", rows));
			}
		} catch (Exception e) {
			LOG.error("执行查询操作发生错误", e);
			returnJson.put("status", "-2");
			returnJson.put("msg", "执行查询操作发生错误");
		} finally {
			try {
				LOG.debug(returnJson);
				response(returnJson);
			} catch (IOException e) {
				LOG.error("查询返回信息时发生错误", e);;
			}
			reqData = null;
		}
	}
	
	protected String treeData(List<JSONObject> data) {
		return new JSONMap<String, String>(JSONMap.TYPE.ARRAY).toString();
	}
	
	@RequestMapping(params = "m=a")
	public void add() {
		JSONMap<String, Object> returnJson = new JSONMap<String, Object>(JSONMap.TYPE.OBJECT);
		
		try {
			if (reqData == null)
				reqData = getReqData();
			// 查询实体名称
			String entityName = reqData.getString("en");
			if (entityName == null || SysCache.get(entityName) == null) {
				returnJson.put("status", "-1");
				returnJson.put("msg", "没有指定插入实体名称");
			} else {
				JSONObject added = baseDAO.add(reqData.getString("data"), entityName);
				int r = 1;
				if (added.length() < 1) {
					r = -3;
					returnJson.put("msg", "未能成功添加数据");
				} else {
					returnJson.put("added", added);
				}
				returnJson.put("status", r);
			}
		} catch (Exception e) {
			LOG.error("执行插入操作发生错误", e);
			returnJson.put("status", "-2");
			returnJson.put("msg", "执行插入操作发生错误");
		} finally {
			try {
				response(returnJson);
			} catch (IOException e) {
				LOG.error("插入返回信息时发生错误", e);
			}
			reqData = null;
		}
	}
	
	@RequestMapping(params = "m=ba")
	public void batchAdd() {
		JSONMap<String, Object> returnJson = new JSONMap<String, Object>(JSONMap.TYPE.OBJECT);
		
		try {
			if (reqData == null)
				reqData = getReqData();
			// 查询实体名称
			String entityName = reqData.getString("en");
			if (entityName == null || SysCache.get(entityName) == null) {
				returnJson.put("status", "-1");
				returnJson.put("msg", "没有指定插入实体名称");
			} else {
				int r = 1;
				JSONObject[] added = baseDAO.batchAdd(reqData.getJSONArray("data"), entityName);
				if (added.length < reqData.getJSONArray("data").length()) {
					r = -3;
					returnJson.put("msg", "未能成功添加数据");
				} else {
					returnJson.put("added", new JSONArray(added));
				}
				returnJson.put("status", r);
			}
		} catch (Exception e) {
			LOG.error("执行插入操作发生错误", e);
			returnJson.put("status", "-2");
			returnJson.put("msg", "执行插入操作发生错误");
		} finally {
			try {
				response(returnJson);
			} catch (IOException e) {
				LOG.error("插入返回信息时发生错误", e);
			}
			reqData = null;
		}
	}
	
	@RequestMapping(params = "m=u")
	public void update() {
		JSONMap<String, Object> returnJson = new JSONMap<String, Object>(JSONMap.TYPE.OBJECT);
		
		try {
			if (reqData == null)
				reqData = getReqData();
			// 查询实体名称
			String entityName = reqData.getString("en");
			if (entityName == null || SysCache.get(entityName) == null) {
				returnJson.put("status", "-1");
				returnJson.put("msg", "没有指定更新实体名称");
			} else {
				int r = baseDAO.update(reqData.getString("data"), entityName);
				if (r < 1) {
					r = -3;
					returnJson.put("msg", "未能成功添加数据");
				} else if (r > 1) {
					r = 1;
				}
				returnJson.put("status", r);
			}
		} catch (Exception e) {
			LOG.error("执行更新操作发生错误", e);
			returnJson.put("status", "-2");
			returnJson.put("msg", "执行更新操作发生错误");
		} finally {
			try {
				response(returnJson);
			} catch (IOException e) {
				LOG.error("更新返回信息时发生错误", e);
			}
			reqData = null;
		}
	}
	
	@RequestMapping(params = "m=bu")
	public void batchUpdate() {
		JSONMap<String, Object> returnJson = new JSONMap<String, Object>(JSONMap.TYPE.OBJECT);
		
		try {
			if (reqData == null)
				reqData = getReqData();
			// 查询实体名称
			String entityName = reqData.getString("en");
			if (entityName == null || SysCache.get(entityName) == null) {
				returnJson.put("status", "-1");
				returnJson.put("msg", "没有指定更新实体名称");
			} else {
				int r = baseDAO.batchUpdate(reqData.getJSONArray("data"), entityName);
				if (r < reqData.getJSONArray("data").length()) {
					r = -3;
					returnJson.put("msg", "未能成功添加数据");
				} else {
					r = 1;
				}
				returnJson.put("status", r);
			}
		} catch (Exception e) {
			LOG.error("执行插入操作发生错误", e);
			returnJson.put("status", "-2");
			returnJson.put("msg", "执行插入操作发生错误");
		} finally {
			try {
				response(returnJson);
			} catch (IOException e) {
				LOG.error("插入返回信息时发生错误", e);
			}
			reqData = null;
		}
	}
	
	@RequestMapping(params = "m=d")
	public void delete() {
		JSONMap<String, Object> returnJson = new JSONMap<String, Object>(JSONMap.TYPE.OBJECT);
		
		try {
			if (reqData == null)
				reqData = getReqData();
			// 查询实体名称
			String entityName = reqData.getString("en");
			if (entityName == null || SysCache.get(entityName) == null) {
				returnJson.put("status", "-1");
				returnJson.put("msg", "没有指定删除实体名称");
			} else {
				int r = baseDAO.delete(reqData.getString("data"), entityName);
				if (r < 1) {
					r = -3;
					returnJson.put("msg", "未能成功删除数据");
				}
				returnJson.put("status", r);
			}
		} catch (Exception e) {
			LOG.error("执行删除操作发生错误", e);
			returnJson.put("status", "-2");
			returnJson.put("msg", "执行删除操作发生错误");
		} finally {
			try {
				response(returnJson);
			} catch (IOException e) {
				LOG.error("删除返回信息时发生错误", e);
			}
			reqData = null;
		}
	}
	
	@RequestMapping(params = "bd")
	public void batchDelete() {
		JSONMap<String, Object> returnJson = new JSONMap<String, Object>(JSONMap.TYPE.OBJECT);
		
		try {
			if (reqData == null)
				reqData = getReqData();
			// 查询实体名称
			String entityName = reqData.getString("en");
			if (entityName == null || SysCache.get(entityName) == null) {
				returnJson.put("status", "-1");
				returnJson.put("msg", "没有指定删除实体名称");
			} else {
				int r = baseDAO.batchDelete(reqData.getJSONArray("data"), entityName);
				if (r < reqData.getJSONArray("data").length()) {
					r = -3;
					returnJson.put("msg", "未能成功添加数据");
				}
				returnJson.put("status", r);
			}
		} catch (Exception e) {
			LOG.error("执行删除操作发生错误", e);
			returnJson.put("status", "-2");
			returnJson.put("msg", "执行删除操作发生错误");
		} finally {
			try {
				response(returnJson);
			} catch (IOException e) {
				LOG.error("删除返回信息时发生错误", e);
			}
			reqData = null;
		}
	}
}
