package com.smikevon.stock.lucene.indexer;

import com.smikevon.lucene.index.DocParam;
import com.smikevon.lucene.index.SimpleIndex;
import com.smikevon.stock.common.Constants.IndexPath;
import com.smikevon.stock.crawl.StockDataCrawl;
import com.smikevon.stock.crawl.helper.StockDataCrawlHelper;
import com.smikevon.stock.util.StringUtil;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class BigDealIndexer extends SimpleIndex {
	private static final Logger logger = Logger.getLogger(BigDealIndexer.class);
	private static int pagesize = 10000;
	
	private static int pages = 100;
	
	private static String url = "http://quotes.money.163.com/hs/realtimedata/service/dadan.php";
	
	@SuppressWarnings({ "serial" })
	private static Map<String,String> map = new HashMap<String,String>(){{
		put("t", "0");
		put("query", "vol:1;");//大于50手的都抓下来
		put("fields", "RN,SYMBOL,NAME,DATE,PRICE,PRICE_PRE,PERCENT,TURNOVER_INC,VOLUME_INC,TRADE_TYPE,,CODE");
		put("sort", "DATE");
		put("order", "desc");
		put("type", "query");
	}};

	BigDealIndexer(String indexPath) {
		super(indexPath);
	}


	/**
	 * 创建索引，通过分页抓取创建，避免一次响应时间过长
	 * @throws Exception
	 */
	private void executeAdd() throws Exception {
		String timestamp = getDateInfo();
		int pageCount = getPageCount(timestamp);
		DocParam[] docs = getDealList(0,timestamp);
		long start = System.currentTimeMillis();
		for (int i = 0; i <pages; i++) {
			if(i==0){
				logger.info(docs[0].getDocument());
			}
			batchAdd(docs);
		}
		long end = System.currentTimeMillis();
		logger.info("共耗时："+(end-start)+" 秒");
	}
	
	/**
	 * 获取数据页数
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private int getPageCount(String timestamp) throws Exception {
		map.put("page", 0+"");
		map.put("count", pagesize+"");
		map.put("req", timestamp);
		String body = new String(StockDataCrawl.getResponse(url, map));
		Map params = StockDataCrawlHelper.converBodyToMap(body);
		return Integer.parseInt(String.valueOf(params.get("pagecount")));
	}
	
	/**
	 * 获取指定页的大单数据详情
	 * @param pageNo
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private DocParam[] getDealList(int pageNo,String timestamp) throws Exception {
		map.put("page", pageNo+"");
		map.put("count", pagesize+"");
		map.put("req", timestamp);
		String body = new String(StockDataCrawl.getResponse(url, map));
		Map params = StockDataCrawlHelper.converBodyToMap(body);
		List<Map<String, Object>> list = (List<Map<String,Object>>)params.get("list");
		//saveToDisk(list,pages);
		Set<String> set = new HashSet<String>();
		if (list.size()>0) {
			set = list.get(0).keySet();
		}
		List<DocParam> docs = new LinkedList<DocParam>();
		for (int i = 0; i < list.size(); i++) {
			DocParam doc = new DocParam();
			Iterator<String> iterator = set.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				//doc.index(key , StringUtil.isNum(String.valueOf(list.get(i).get(key)))?Double.parseDouble(String.valueOf(list.get(i).get(key))):String.valueOf(list.get(i).get(key)));
				if(StringUtil.isNum(String.valueOf(list.get(i).get(key)))){
					doc.add(key, Double.parseDouble(String.valueOf(list.get(i).get(key))));
				}else if(!key.equals("NAME")){
					doc.add(key, String.valueOf(list.get(i).get(key)));
				}else{
					doc.add("NAME", String.valueOf(list.get(i).get(key)),true);
				}
				
			}
			docs.add(doc);
		}
		return docs.toArray(new DocParam[0]);
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
	
	public static void saveToDisk(List<Map<String,Object>> list,int pageNo){
		File file = new File("D:/workspace/stock_rush/json.txt");
		if(file.exists()){
			file.delete();
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File("D:/workspace/stock_rush/json.txt"),true));
			for(int j=0;j<pageNo;j++){
				for (int i = 0; i < list.size(); i++) {
					bw.write(list.get(i).toString());
				}
				bw.flush();
				bw = new BufferedWriter(new FileWriter(new File("D:/workspace/stock_rush/json.txt"),true));
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			long maxMemory = Runtime.getRuntime().maxMemory();
			BigDealIndexer indexer = new BigDealIndexer(IndexPath.BIG_DEAL_INDEX);
			indexer.batchDeleteAll();//删除已有的
			long beforeMemory=Runtime.getRuntime().totalMemory();
			indexer.executeAdd();
			long afterMemory = Runtime.getRuntime().totalMemory();
			long remainMemeory = Runtime.getRuntime().freeMemory();
			
			System.out.println("最大堆内存："+maxMemory/1024);
			
			System.out.println("使用前内存:"+beforeMemory/1024+";使用后内存："+afterMemory/1024+";共消耗内存："+(afterMemory-remainMemeory)/1024);
			
			System.out.println("剩余内存:"+remainMemeory/1024);
			indexer.closeBatch();
		}catch (Exception e) {
			logger.error(e.getMessage(),e.getCause());
		}finally{
			System.exit(0);
		}
	}


	

}
