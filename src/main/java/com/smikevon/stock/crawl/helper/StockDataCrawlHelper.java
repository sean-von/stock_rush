package com.smikevon.stock.crawl.helper;

import com.smikevon.stock.util.JsonUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * 抓取数据帮助类，数据处理
 * @author fengxiao
 *
 */
public class StockDataCrawlHelper {
	
	private static final Logger logger = Logger.getLogger(StockDataCrawlHelper.class);
	
	/**
	 * 将返回数据转换成map格式
	 * @param json
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map converBodyToMap(String json){
		if (json == null || json.length() == 0){
			return new HashMap();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		//classMap.put("list", Map.class);
        //(Map)JsonUtils.json2obj(json,Map.class.getClass());
		//JSONObject jsonObj = JSONObject.fromObject(json);
        //System.out.println("Map"+json);
        try {
            return (Map)JsonUtils.json2obj(json, map.getClass());
        } catch (IOException e) {
            logger.error("can not convert json to message " + e.getMessage());
        }
        return null;
    }
	
	/**
	 * 对大单交易进行统计
	 * @param codeMap
	 * @param list
	 */
	@SuppressWarnings("rawtypes")
	public static void dealListWithQuantity(Map<String,Double> codeMap,List list){
		for(int i=0;i<list.size();i++){
			Map map = (Map)list.get(i);
			String code = (String)map.get("CODE");
			Integer size = (Integer)map.get("VOLUME_INC");
			Integer type = (Integer)(map.get("TRADE_TYPE"));
			Double value = Double.parseDouble( (map.get(code)==null?"0":map.get(code)+""));
			if (type == 1 ) {
				value = value+size;
				codeMap.put(code, value);
			}else {
				value = value-size;
				codeMap.put(code, value);
			}
		}
	}
	
	/**
	 * 对大单交易的交易额进行统计
	 * @param amountMap
	 * @param list
	 */
	@SuppressWarnings("rawtypes")
	public static void dealListWithAount(Map<String, Double> amountMap,List list){
		for(int i=0;i<list.size();i++){
			Map map = (Map)list.get(i);
			String code = (String)map.get("CODE");
			Integer size = (Integer)map.get("VOLUME_INC");
			Integer type = (Integer)(map.get("TRADE_TYPE"));
			Double price = Double.parseDouble(map.get("PRICE_PRE").toString());
			Double value = Double.parseDouble((map.get(code)==null?"0":map.get(code)+""));
			if (type == 1 ) {
				value = value+size*price;
				amountMap.put(code, value);
			}else {
				value = value-size*price;
				amountMap.put(code, value);
			}
		}
	}
	
	public static void dealListWithStocks(Map<String, Double> bilvMap, List list, Map<String, Map<String, String>> stocks) {
		try{
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				String code = (String)map.get("CODE");
				BigDecimal size = BigDecimal.valueOf((Integer)map.get("VOLUME_INC"));
				Integer type = (Integer)(map.get("TRADE_TYPE"));
				BigDecimal value = BigDecimal.valueOf(Double.parseDouble((map.get(code)==null?"0":map.get(code)+"")));
				if (stocks.get(code.substring(1))!=null && stocks.get(code.substring(1)).get("size")!=null) {
					if (type == 1 ) {
						value = value.add(size.divide(BigDecimal.valueOf(Double.valueOf(stocks.get(code.substring(1)).get("size"))*100),10,RoundingMode.DOWN));
					}else {
						value = value.subtract(size.divide(BigDecimal.valueOf(Double.valueOf(stocks.get(code.substring(1)).get("size"))*100),10,RoundingMode.DOWN));
					}
					bilvMap.put(code, value.doubleValue());
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 对结果进行排序并打印
	 * @param codeMap
	 */
	public static List<Entry<String, Double>> sortMapByVale(Map<String,Double> codeMap){
		List<Entry<String, Double>> list = new LinkedList<Entry<String,Double>>(codeMap.entrySet());
		Collections.sort(list, new Comparator<Entry<String,Double>>(){

			@Override
			public int compare(Entry<String, Double> o1,Entry<String, Double> o2) {
				if(o1.getValue()>o2.getValue()){
					return 1;
				}else if(o1.getValue()<o2.getValue()){
					return -1;
				}else {
					return 0;
				}
			}
			
		});
		Collections.reverse(list);
		return list;
	}
	
	/**
	 * 打印排序后数据
	 * @param list
	 * @param size
	 */
	public static void printTop(List<Entry<String, Double>> list,int size){
		int length = list.size()>size?size:list.size();
		for (int i = 0; i < length; i++) {
			logger.info(list.get(i).getKey()+"||"+list.get(i).getValue());
		}
	}


}
