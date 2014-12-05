package com.smikevon.stock.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.PropertyFilter;

/**
 * @author syyang
 * json处理工具�?
 */
public class JsonUtil {
	
	/**
	 * 把json格式的字符串转化成Map
	 * 
	 * @param json
	 * @return
	 */
	public static Map strToMap(String json) {
		if (json == null || json.length() == 0)
			return new HashMap();
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (Map) JSONObject.toBean(jsonObj, Map.class);
	}

	/**
	 * 把json格式的字符串转化成Row
	 * 
	 * @param json
	 * @return
	 */
	public static Row strToRow(String json) {
		if (json==null || json.length()==0) return new Row();
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (Row)JSONObject.toBean(jsonObj, Row.class);
	}
	
	/**
	 * 把Row转化成json格式的字符串
	 * @param row
	 * @return
	 */
	public static String rowToJson(Row row) {
		if (row==null || row.size()==0) return "{}";
		JSONObject json = JSONObject.fromObject(row);
		return json.toString().replaceAll("'", "\\\\'");
	}

	/**
	 * 把datainfo字段添加到row
	 * @param row
	 * @return
	 */
	public static Row readDataInfo(Row row) {
		return readDataInfoBase(row,"datainfo");
	}
  /**
   * 把指定类型的JSON字段串拆成Map
   * @param row
   * @param infoName
   * @return
   */
  public static Row readDataInfoBase(Row row,String infoName) {
    if(row == null) return new Row();
    Row jsonRow = strToRow(row.gets(infoName));
    for (int i = 0; i < jsonRow.length(); i++) {
      if (!row.containsKey(jsonRow.getKey(i))) {
        row.put(jsonRow.getKey(i), jsonRow.get(i));
      }
    }
    if(row.containsKey(infoName)){
      row.remove(infoName);
    }
    return row;
  }
  
  public static Row mergeRow(Row resource, Row dest){
      if(dest == null){
          return null;
      }
      
      if(resource == null){
          return dest;
      }
      
      String[] keys = resource.getKeys();
      for(String key : keys){
          dest.put(key, resource.get(key));
      }
      
      return dest;
  }
  /**
   * 把对象转化为JSON对象
   * @param o
   * @return
   */
  public static JSON toJson(Object o) {
    return JSONObject.fromObject(o);
  }
  /**
   * 把指定的属�?存入datainfo字段中去,返回row可以进行链式编程
   * @param row
   * @return
   */
  public static Row writeDataInfo(Row row,String[] props){
    Row dataInfo = new Row();
    //把指定的属�?读出来，并存入dataInfo
    for(String prop : props){
      if(row.get(prop)!=null){
        dataInfo.put(prop, row.get(prop));
        row.remove(prop);
      }
    }
    row.put("datainfo", toJson(dataInfo).toString()); 
    return row;
  }
 /**
  * 把一个JSON对象数组描述字符转化为List
  * @param str
  * @return
  */
 public static List strToList(String str) {
   JSONArray json = JSONArray.fromObject(str);
   List listData = (List) JSONArray.toList(json, HashMap.class);
   return listData;
 }
 /**
  * 把list转化为JSON数组字符串描�?
  * @param list
  * @return
  */
 public static String listToStr(List list){
	 if (list==null || list.size()==0) return "[]";
   JSONArray jsons = JSONArray.fromObject(list,filterConfig());
   return jsons.toString();
 }
  /**
   * 转换对象到json
   * 
   * @param o
   * @param excludeProperty
   *            ,要排除的属�?
   * @return
   */
  public static JSON jsonExclude(Object o, String[] excludeProperty) {
    return baseJson(o,excludeProperty,true);
  }
  /**
   * 转换对象到JOSN
   * 
   * @param o
   * @param includeProperty
   *            ,要包含的属�?
   * @return
   */
  public static JSON jsonInclude(Object o, String[] includePropertys) {
    return baseJson(o,includePropertys,false);
  }
  /**
   * 转换对象到JOSN
   * 
   * @param o
   * @param includeProperty
   *            ,要包含的属�?
   * @return
   */
  public static JSON jsonListInclude(List list, String[] includePropertys) {
    return baseListJson(list,includePropertys,false);
  }
  /**
   * 转换对象到json
   * 
   * @param o
   * @param excludeProperty
   *            ,要排除的属�?
   * @return
   */
  public static JSON jsonListExclude(List list, String[] excludeProperty) {
    return baseListJson(list,excludeProperty,true);
  }
  /**
   * 按指定方式过虑JSON数据字段
   * @param o
   * @param excludeProperty
   * @param exclude
   * @return
   */
  private static JSON baseJson(Object o, String[] excludeProperty,boolean exclude){
    JSON json = JSONSerializer.toJSON(o, filterConfig(excludeProperty,exclude));
    return json;
  }
  /**
   * 按指定的方式过虑数组并转化成JSON对象
   * @param list
   * @param includePropertys
   * @param exclude
   * @return
   */
  private static JSON baseListJson(List list, String[] includePropertys,boolean exclude){
    JSON json = JSONArray.fromObject(list, filterConfig(includePropertys,exclude));    
    return json;
  }
  /**
   * 返回过虑器对�?
   * @param propertys
   * @param exclude
   * @return
   */
  private static JsonConfig filterConfig(String[] propertys,boolean exclude){
    JsonConfig config = new JsonConfig();
    // config.setExcludes(excludeProperty);
    NamedPropertyFilter filter = new NamedPropertyFilter(propertys);
    filter.setExclude(exclude);
    // config.setJavaPropertyFilter(filter);
    config.setJsonPropertyFilter(filter);
    return config;
  }
 private static JsonConfig filterConfig() {
   JsonConfig config = new JsonConfig();
   JsonValueProcessor jp = new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss");
   config.registerJsonValueProcessor(java.sql.Timestamp.class,jp);  
   return config;
 }
  // 按名称过滤属�?
  private static class NamedPropertyFilter implements PropertyFilter {
    private String[] names;
    private boolean exclude = true;

    public void setExclude(boolean exclude) {
      this.exclude = exclude;
    }

    public NamedPropertyFilter(String[] names) {
      this.names = names;
    }

    public NamedPropertyFilter(String[] names, boolean exclude) {
      this.names = names;
      this.exclude = exclude;
    }

    public boolean apply(Object source, String property, Object value) {
      if (names == null || names.length < 1) {
        return !exclude;
      }
      for (String name : names) {
        if (name.equals(property)) {
          return exclude;
        }
      }
      return !exclude;
    }
  }
  private static class DateJsonValueProcessor implements JsonValueProcessor {  
        public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";  
        private DateFormat dateFormat;        
        /** 
         * 构�?方法. 
         * 
         * @param datePattern 日期格式 
         */  
        public DateJsonValueProcessor(String datePattern) {  
            try {  
                dateFormat = new SimpleDateFormat(datePattern);  
            } catch (Exception ex) {  
                dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);  
            }  
        }  
      
        public Object processArrayValue(Object value, JsonConfig jsonConfig) {  
            return process(value);  
        }  
      
        public Object processObjectValue(String key, Object value,  
            JsonConfig jsonConfig) {  
            return process(value);  
        }  
      
        private Object process(Object value) {  
            return dateFormat.format((java.sql.Timestamp) value);  
        }  
    }  
}
