/**
 * 
 */
package com.smikevon.stock.util;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wzxie <字段,�? 重新修改了一下，可以完全取代HashMap
 */
public class Row extends HashMap {
	private static final long serialVersionUID = 1L;

	protected List<Object> ordering = new ArrayList<Object>(); // <字段>

	protected Map<String, String> functionMap = null; // <字段,函数>

	/**
	 * 
	 */
	public Row() {
	}

	/** 直接拿一个Map过来当作�?��Row使用 */
	@SuppressWarnings("unchecked")
	public Row(Map map) {
		super(map);
    for(Object obj : map.keySet()){
      ordering.add(obj);
    }
	}


	/**
	 * 根据字段名返回字符串�?
	 * 
	 * @param name
	 * @return
	 */
	public String gets(Object name) {
		try {
			if (get(name) != null)
				return get(name).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据字段名返回字符串�?若为null侧返回默认�?;
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public String gets(Object name, String defaultValue) {
		try {
			if (get(name) != null)
				return get(name).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultValue;
	}
	
	/**
	 * 根据字段名返回用指定编码解码字符串�?,若为null侧返回默认�?;
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public String gets(Object name,String enc, String defaultValue) {
		try {
			if (get(name) != null)
				return URLDecoder.decode(get(name).toString(), enc) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	/**
	 * @param name
	 * @return
	 */
	public int getInt(Object name) {
		return getInt(name,-1);
	}

	/**
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public int getInt(Object name, int defaultValue) {
		Object o = get(name);
		if (o != null) {
			try {
				return StringUtil.getInt(o.toString(), defaultValue);
			} catch (Exception e) {
			}
		}
		return defaultValue;
	}

	/**
	 * @param which
	 * @param defaultValue
	 * @return
	 */
	public int getInt(int which, int defaultValue) {
		Object key = ordering.get(which);
		return getInt(key, defaultValue);
	}

	/**
	 * @param name
	 * @return
	 */
	public float getFloat(Object name) {
		return Float.valueOf(get(name).toString()).floatValue();
	}

	/**
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public float getFloat(Object name, float defaultValue) {
		Object o = get(name);
		if (o != null)
			try {
				return Float.valueOf(o.toString()).floatValue();
			} catch (Exception e) {
			}
		return defaultValue;
	}

	/**
	 * @param which
	 * @param defaultValue
	 * @return
	 */
	public float getFloat(int which, float defaultValue) {
		Object key = ordering.get(which);
		return getFloat(key, defaultValue);
	}

	/**
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public long getLong(Object name, long defaultValue) {
		Object o = get(name);
		if (o != null)
			try {
				return Long.valueOf(o.toString()).longValue();
			} catch (Exception e) {
			}
		return defaultValue;
	}

	/**
	 * @param which
	 * @return
	 */
	public Object get(int which) {
		Object key = ordering.get(which);
		return get(key);
	}

	/**
	 * @param which
	 * @return
	 */
	public Object getKey(int which) {
		Object key = ordering.get(which);
		return key;
	}

	public String[] getKeys() {
		Set keys = this.keySet();
		Iterator iter = keys.iterator();
		String[] strs = new String[keys.size()];
		int i = 0;
		while (iter.hasNext()) {
			strs[i] = iter.next().toString();
			i++;
		}
		return strs;
	}

	/**
	 * 
	 */
	public void dump() {
		for (Iterator e = keySet().iterator(); e.hasNext();) {
			String name = (String) e.next();
			Object value = get(name);
			System.out.println(name + "=" + value + ", ");
		}
		System.out.println("");
	}

	public String dumpToString() {
		StringBuffer sb = new StringBuffer();
		for (Iterator e = keySet().iterator(); e.hasNext();) {
			String name = (String) e.next();
			Object value = get(name);
			sb.append(value).append(",");
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc) 覆盖了父类的put方法
	 * 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object put(Object name, Object value) {
		if (!containsKey(name)) {
			ordering.add(name); // 将键保存起来
		}
		super.put(name, value);
		if (functionMap != null && functionMap.containsKey(name))
			functionMap.remove(name);
		return value;
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int putInt(Object name, int value) {
		super.put(name, new Integer(value));
		return value;
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public float putFloat(Object name, float value) {
		super.put(name, new Float(value));
		return value;
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	public String putFunction(String name, String value) {
		if (functionMap == null)
			functionMap = new HashMap<String, String>();
		if (this != null && this.containsKey(name)) {
			// ordering.remove(name);
			remove(name);
		}
		functionMap.put(name, value);
		return value;
	}

	/**
	 * @param name
	 * @return
	 */
	public String getFunction(String name) {
		return functionMap.get(name);
	}

	public Map getFunctionMap() {
		return this.functionMap;
	}

	/**
	 * @param fmap
	 */
	public void setFunctionMap(HashMap<String, String> fmap) {
		if (fmap != null && fmap.size() > 0) {
			if (functionMap == null)
				functionMap = new HashMap<String, String>();
			this.functionMap.putAll(fmap);
		}
	}

	/*
	 * (non-Javadoc) 覆盖了父类的putAll方法
	 * 
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public void putAll(Map otherMap) {
		Set keySet = otherMap.keySet();
		for (Object name : keySet)
			ordering.add(name);
		super.putAll(otherMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object name) {
		if (ordering.remove(name))
			return super.remove(name);
		return null;
	}

	/*
	 * (non-Javadoc) 覆盖了父类的clear方法
	 * 
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		super.clear();
		ordering = null;
	}

	/**
	 * 得到行对象中字段的个�?
	 * 
	 * @return
	 */
	public int length() {
		return size();
	}

}