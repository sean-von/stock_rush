package com.smikevon.stock.lucene.indexer;

import com.smikevon.lucene.LuceneConfig;
import com.smikevon.lucene.index.AbstractLuceneScheduleIndex;
import com.smikevon.lucene.index.DocParam;
import com.smikevon.stock.common.Constants.IndexPath;
import com.smikevon.stock.task.LargeDealAyalysisTask;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 创建股票基本信息索引
 * @author fengxiao
 *
 */

public class StockInfoIndexer extends AbstractLuceneScheduleIndex<Map<String, Object>> {
	private static final Logger logger = Logger.getLogger(StockInfoIndexer.class);
	private static boolean isCrawledAll = false;

	protected StockInfoIndexer(String indexPath) {
		super(indexPath);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<Map<String, Object>> getList(int start, int size) throws Exception {
		if (isCrawledAll) {
			return new ArrayList();
		}
		LargeDealAyalysisTask.getAllStockInfo();
		LargeDealAyalysisTask.storeQuantityInfo();
		isCrawledAll = true;
		List list = new LinkedList(LargeDealAyalysisTask.stocks.values());
		return list;
	}

	public DocParam[] getDocParam(Map<String, Object> obj) {
		try{
			DocParam doc = new DocParam();
			logger.info("indexed stockId :"+String.valueOf(obj.get("symbol")).substring(1));
			Iterator<String> iterator = obj.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				doc.index(key, String.valueOf(obj.get(key)));
			}
			doc.index("url", "http://quotes.money.163.com/"+String.valueOf(obj.get("symbol"))+".html");
			return new DocParam[]{doc};
		}catch (Exception e) {
			logger.error(e.getMessage(),e.getCause());
		}
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			isCrawledAll = false;
			LuceneConfig.setSchedulePagenum(Integer.MAX_VALUE);//一次全部取出
			StockInfoIndexer p=new StockInfoIndexer(IndexPath.STOCK_INFO_INDEX);
			p.makeIndex();
		}catch (Exception e) {
			logger.error(e.getMessage(),e.getCause());
		}finally{
			System.exit(0);
		}
	}

}
