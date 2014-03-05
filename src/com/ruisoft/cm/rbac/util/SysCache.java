package com.ruisoft.cm.rbac.util;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;

public class SysCache {
	private static final Logger LOG = Logger.getLogger(SysCache.class);
	
	public static Map<String, Object> SYS_CACHE = null;
	
	public void setSYS_CACHE(Map<String, Object> cache) {
		SYS_CACHE = cache;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static synchronized void clear(String key) {
		LOG.info("清除缓存[".concat(key).concat("]"));

		String[] ks = key.split(".");
		Map<String, Object> map = SYS_CACHE;
		String k = null;
		for (int i = 0; i < ks.length; i++) {
			k = ks[i];

			if (i + 1 == ks.length) {
				map.remove(k);
				break;
			}

			if (map instanceof Map) {
				map = (Map) (map.get(k));
				if (map == null)
					break;
			} else {
				throw new IllegalArgumentException("错误的缓存对象[".concat(key)
						.concat("]"));
			}
		}
	}
	
	public static void cleraAll() {
		SYS_CACHE.clear();
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static Object get(final String key) {
		String k = key, k_a = null;
		Object cacheObj = SYS_CACHE;
		String getter = null;
		Method m = null;
		Class<?>[] paramClass = null;
		Object[] params = null;
		do {
			if (cacheObj instanceof Map)
				if (((Map) cacheObj).containsKey(k))
					return ((Map) cacheObj).get(k);
			
			int idx = k.indexOf(".");
			if (idx < 0) {
				getter = "get".concat(k.substring(0, 1).toUpperCase()).concat(k.substring(1));
				try {
					m = cacheObj.getClass().getMethod(getter, paramClass);
					return m.invoke(cacheObj, params);
				} catch (Exception e) {
					LOG.debug("", e);
					LOG.error("没有找到缓存[".concat(key).concat("]"));
					return null;
				}
			} else {
				k_a = k.substring(idx + 1);
				k = k.substring(0, idx);
				
				if (cacheObj instanceof Map) {
					if (((Map) cacheObj).containsKey(k))
						cacheObj = ((Map) cacheObj).get(k);
				} else {
					getter = "get".concat(k.substring(0, 1).toUpperCase().concat(k.substring(1)));
					try {
						m = cacheObj.getClass().getMethod(getter, paramClass);
						cacheObj = m.invoke(cacheObj, params);
					} catch (Exception e) {
						LOG.debug("", e);
						return null;
					}
				}
				
				k = k_a;
			}
			
		} while (true);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(final String key, T type) {
		return (T) get(key);
	}
}
