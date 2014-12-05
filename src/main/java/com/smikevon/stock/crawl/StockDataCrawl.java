package com.smikevon.stock.crawl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.smikevon.stock.task.LargeDealAyalysisTask;
import com.smikevon.stock.util.JsonUtil;

public class StockDataCrawl {
	
	private static final Logger logger = Logger.getLogger(StockDataCrawl.class);
	
	/**
	 * 获取大单交易的交易数据
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static byte[] getResponse(String url,Map params) throws Exception{
		byte[] body = null;
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(url);
		
		NameValuePair[] data = new NameValuePair[params.keySet().size()];
		Iterator iterator = params.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Object key = iterator.next();
			Object value = params.get(key);
			data[i] = new NameValuePair(key.toString(),value.toString());
			i++;
		}
		getMethod.setQueryString(data);
		int statusCode = client.executeMethod(getMethod);
		//logger.info(statusCode);
		if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {  
			   Header locationHeader = getMethod.getResponseHeader("location");  
			   String location = null;  
			   if (locationHeader != null) { 
				   location = locationHeader.getValue(); 
				   System.out.println("The page was redirected to:" + location);  
			   }else {  
				   System.err.println("Location field value is null.");  
			   } 
		}
		body = getMethod.getResponseBody();
		return body;
	}
	
	/**
	 * 抓取股票url数据，通过搜索接口抓取
	 */
	@SuppressWarnings("unchecked")
	public static void CrawlStockInfo(String type,String keyword,Map<String, Map<String, String>> stocks) throws Exception{
		byte[] body = null;
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod("http://quotes.money.163.com/stocksearch/json.do");
		NameValuePair[] param = new NameValuePair[4];
		param[0] = new NameValuePair("type", type);
		param[1] = new NameValuePair("count", Integer.MAX_VALUE+"");
		param[2] = new NameValuePair("word",keyword);
		param[3] = new NameValuePair("time","0.23699882766231894");//不知道干什么用的，可能是最近时间的数据？
		getMethod.setQueryString(param);
		int statusCode = client.executeMethod(getMethod);
		//logger.info(statusCode);
		if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {  
			   Header locationHeader = getMethod.getResponseHeader("location");  
			   String location = null;  
			   if (locationHeader != null) { 
				   location = locationHeader.getValue(); 
				   System.out.println("The page was redirected to:" + location);  
			   }else {  
				   System.err.println("Location field value is null.");  
			   } 
		}
		body = getMethod.getResponseBody();
		String result = new String(body,"GBK");
		result = result.replaceAll("_ntes_stocksearch_callback\\((.*)\\)$", "$1");
		//Map map = JsonUtil.strToMap(result);
		List<Map<String,String>> list = JsonUtil.strToList(result);
		for(int i=0;i<list.size();i++){
			String symbol = list.get(i).get("symbol").toString();
			stocks.put(symbol, list.get(i));
		}
	}
	
	/**
	 * 抓取股票的基本信息，主要是总股本
	 * 例如：http://quotes.money.163.com/f10/gdfx_000885.html
	 * @throws java.io.IOException
	 */
	@SuppressWarnings("unchecked")
	public static void crawlCapitalization(String symbol,Map<String, Map<String, String>> stocks) throws IOException{
		String url = "http://quotes.money.163.com/f10/gdfx_"+symbol+".html";
		Document doc = Jsoup.connect(url).get();
		Elements elements = doc.select("table.table_bg001.border_box");
		String size = elements.get(1).select("tr.dbrow").get(0).child(1).html();//流通股总量，A股
		Elements eles = doc.select("td.price");
		stocks.get(symbol).put("size", size.replaceAll(",|，", ""));
		//logger.info(symbol+"||"+size);
	}
	
	/**
	 * http://img1.quotes.ws.126.net/flash/hs/kline/day/now/1002606.json?timeStamp=1372323692509
	 * @param args
	 */
	
	public static void main(String[] args){
		try{
			//CrawlStockInfo("HS","d",LargeDealAyalysisTask.stocks);
			crawlCapitalization("000885",LargeDealAyalysisTask.stocks);
		}catch (Exception e) {
			logger.error(e.getMessage());
		}finally{
			System.exit(0);
		}
		
	}

	

}
