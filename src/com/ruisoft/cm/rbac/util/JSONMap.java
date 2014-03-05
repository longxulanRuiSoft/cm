package com.ruisoft.cm.rbac.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class JSONMap<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 7357392663542964897L;

	private Enum<?> t = null;
	
	public static enum TYPE {OBJECT, ARRAY};
	
	public void setType(Enum<?> type) {
		this.t = type;
	}
	
	public Enum<?> getType() {
		return t;
	}

	public JSONMap(Enum<?> type) {
		super();
		this.t = type;
	}
	
	private final static String LEFT_ARRAY = "[";
	private final static String RIGHT_ARRAY = "]";
	private final static String LEFT_OBJECT = "{";
	private final static String RIGHT_OBJECT = "}";
	
	@Override
	public String toString() {
		String left = LEFT_OBJECT, right = RIGHT_OBJECT;
		if (TYPE.ARRAY == t) {
			left = LEFT_ARRAY;
			right = RIGHT_ARRAY;
		}
		
        Iterator<Entry<K,V>> i = entrySet().iterator();
        if (! i.hasNext())
            return left + right;

        StringBuilder sb = new StringBuilder();
        sb.append(left);
        for (;;) {
            Entry<K,V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            
            if (!((value instanceof JSONMap) && ((JSONMap<?, ?>) value).getType() == TYPE.OBJECT)) {
	            sb.append(key == this ? "(this Map)" : "\"" + key + "\"");
	            sb.append(':');
            }
            String pre = "";
            if (value instanceof String && !((String) value).startsWith("{"))
            	pre = "\"";
            sb.append(pre).append(value == this ? "(this Map)" : value).append(pre);
            
            if (! i.hasNext())
                return sb.append(right).toString();
            sb.append(',');
        }
	}
}
