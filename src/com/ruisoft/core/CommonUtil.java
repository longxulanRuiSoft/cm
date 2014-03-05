package com.ruisoft.core;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;


public final class CommonUtil {
	private static final Logger LOG = Logger.getLogger(CommonUtil.class);
	
	/**
	 * <p>输出POJO类中的所有属性及其值</p>
	 * 输出格式<code>[attr=value,...]</code>
	 * <p>POJO类属性值通过对应的<code>getter</code>方法获取，
	 * boolean型值也可以通过形如<code>isXXX</code>的方法获取</p>
	 * 
	 * @param pojo POJO类对象
	 * 
	 * @return 格式化后的属性值字符串
	 */
	public static <T> String pojoString(T pojo) {
		if (pojo == null)
			return "";
		
		String mName = null;
		StringBuffer str = new StringBuffer("[");
		Object[] params = null;
		
		for (Method md : pojo.getClass().getMethods()) {
			mName = md.getName();
			if (mName.startsWith("get") || mName.startsWith("is")) {
				if (mName.equals("getClass"))
					continue;
				if (md.getParameterTypes().length > 0)
					continue;
				
				try {
					str.append(mName.substring(3, 4).toLowerCase().concat(mName.substring(4)))
						.append('=').append(md.invoke(pojo, params));
				} catch (Exception e) {
					LOG.info(e);
				} finally {
					str.append(',');
				}
			}
		}
		if (str.length() > 1) {
			str.deleteCharAt(str.length() - 1);
		}
		str.append(']');
		
		return str.toString();
	}
}
