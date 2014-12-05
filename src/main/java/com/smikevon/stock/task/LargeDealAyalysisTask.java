package com.smikevon.stock.task;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.smikevon.stock.crawl.StockDataCrawl;
import com.smikevon.stock.crawl.helper.StockDataCrawlHelper;

/**
 * 对大单数据进行分析
 * @author fengxiao
 *
 */
public class LargeDealAyalysisTask {
	
	private static final Logger logger = Logger.getLogger(LargeDealAyalysisTask.class);
	private static int pagesize = 2000;
	
	
	//存储所有的股票
	public static Map<String, Map<String, String>> stocks = new HashMap<String, Map<String,String>>();
	private static Map<String,Double> quantityMap = new HashMap<String, Double>(); //成交量，即买盘-卖盘 
	private static Map<String,Double> amountMap = new HashMap<String, Double>(); //成交额，即 买盘*成交价 - 卖盘*成交价 
	private static Map<String,Double> bilvMap = new HashMap<String, Double>(); //成交额，即 （买盘- 卖盘）/流通股数 

	/**
	 * ;
	 * @param args
	 * t=0&page=1&query=vol:300;
	 * &fields=RN,SYMBOL,NAME,DATE,PRICE,PRICE_PRE,PERCENT,TURNOVER_INC,VOLUME_INC,TRADE_TYPE,,CODE
	 * &sort=DATE&order=desc
	 * &count=25
	 * &type=query
	 * &callback=callback_1193232208
	 * &req=31411
	 */
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		try{
			
			List list = getDealList(getDateInfo());
			/*BigDealSearcher searcher = new BigDealSearcher();
			List list = searcher.get(0, 100000).getResult();*/
			
			getAllStockInfo();
			storeQuantityInfo();
			
			// 获取成交量 ，买盘-卖盘 最高的前十支股票
			logger.info("买盘-卖盘Top10：");
			StockDataCrawlHelper.dealListWithQuantity(quantityMap, list);
			List<Map.Entry<String, Double>> listEntry = StockDataCrawlHelper.sortMapByVale(quantityMap);
			StockDataCrawlHelper.printTop(listEntry, 10);
			
			// 获取成交额， 买盘*成交价 - 卖盘*成交价 最高的前十支股票
			logger.info("(买盘-卖盘)*成交价Top10：");
			StockDataCrawlHelper.dealListWithAount(amountMap, list);
			List<Map.Entry<String, Double>> listEntry2 = StockDataCrawlHelper.sortMapByVale(amountMap);
			StockDataCrawlHelper.printTop(listEntry2, 10);
			
			logger.info("(买盘-卖盘)/总股本Top10：");
			StockDataCrawlHelper.dealListWithStocks(bilvMap, list,stocks);
			logger.info("start");
			List<Map.Entry<String, Double>> listEntry3 = StockDataCrawlHelper.sortMapByVale(bilvMap);
			logger.info("start2"+"||"+listEntry3.size());
			StockDataCrawlHelper.printTop(listEntry3, 10);
		}catch (Exception e) {
			logger.error(e.getMessage());
		}finally{
			System.exit(0);
		}
	}
	
	/**
	 * 获取某个时间段的所有大单数据
	 * @param timestamp 时间点
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List getDealList(String timestamp){
		List list = new LinkedList();
		try{
			int pageNo = 0;
			int pageCount = 0;
			String url = "http://quotes.money.163.com/hs/realtimedata/service/dadan.php";
			Map<String,String> map = new HashMap<String, String>();
			map.put("t", "0");
			map.put("query", "vol:300;");//大于100手的都抓下来
			map.put("fields", "RN,SYMBOL,NAME,DATE,PRICE,PRICE_PRE,PERCENT,TURNOVER_INC,VOLUME_INC,TRADE_TYPE,,CODE");
			map.put("sort", "DATE");
			map.put("order", "desc");
			map.put("type", "query");
			do {
				map.put("page", pageNo+"");
				map.put("count", pagesize+"");
				map.put("req", timestamp);
				String body = new String(StockDataCrawl.getResponse(url, map));
				Map params = StockDataCrawlHelper.converBodyToMap(body);
				List tmplist = (List)params.get("list");
				list.addAll(tmplist);
				logger.info("大小："+list.size());
				if(pageNo==0){
					pageCount = Integer.parseInt(String.valueOf(params.get("pagecount")));
					logger.info("pageCount:"+pageCount);
					pageCount = 5;
				}
				pageNo++;
			} while (pageNo<pageCount);
			
		}catch (Exception e) {
			logger.error(e.getMessage(),e.getCause());
		}
		logger.info("大单总数："+list.size());
		return list;
	}
		
	
	/** 
	 * 代码 
	 * 股票 HS
	 * 基金FN
	 * 期货FU
	 * 港股HK
	 * 美股US
	 * 外汇FX
	 */
	public static void getAllStockInfo() throws Exception{
		String[] keyword = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","0","1","2","3","4","5","6","7","8","9"};
		String type = "HS";
		for (int i = 0; i < keyword.length; i++) {
			StockDataCrawl.CrawlStockInfo(type, keyword[i],stocks);
		}
		logger.info(stocks.size());
	}
	
	/**
	 * 存储一下总股本
	 */
	public static void storeQuantityInfo(){
		try{
			Set<String> set = stocks.keySet();
			Iterator<String> iterator = set.iterator();
			while (iterator.hasNext()) {
				StockDataCrawl.crawlCapitalization(iterator.next(), stocks);
			}
		}catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * 获取时间参数串
	 * @return
	 */
	public static String getDateInfo(){
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		return ""+week+hour+minute;
	}
	
	/**
	 * 6开头的 是  上海证券交易所 上市股票
	 * 0开头的 是  深圳证券交易所 上市股票
	 * 3开头是创业板股票 也属于深交所
	 */

}
