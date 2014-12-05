package com.smikevon.stock.lucene.searcher;

import java.util.List;

import com.smikevon.lucene.search.SearchParam;
import com.smikevon.lucene.search.SimpleQuery;
import org.apache.log4j.Logger;
import org.apache.lucene.search.SortField;

import com.smikevon.stock.common.Constants.IndexPath;

public class BigDealSearcher extends SimpleQuery {
	
	private static final Logger logger = Logger.getLogger(BigDealSearcher.class);
	
	public BigDealSearcher(){
		super(IndexPath.BIG_DEAL_INDEX);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BigDealSearcher searcher = new BigDealSearcher();
		logger.info(searcher.getTotalCount());
		SimpleQuery sq1 = searcher.rangeDouble("PRICE", 5.00, 15.00, SearchParam.LogicType.AND, true, true);
		search(sq1);
		
		searcher = new BigDealSearcher();
		SimpleQuery sq2 = searcher.analyzed("NAME", "海信", SearchParam.LogicType.AND);//.analyzed("NAME", "海信",true);
		search(sq2);
		
		SimpleQuery sq3 = new BigDealSearcher();
		sq3.addSortField(new SortField("PERCENT",SortField.Type.DOUBLE));
		search(sq3);
	}
	
	@SuppressWarnings("rawtypes")
	public static void search(SimpleQuery query){
		long maxMemory = Runtime.getRuntime().maxMemory();
		long beforeMemory=Runtime.getRuntime().totalMemory();
		long start = System.currentTimeMillis();
	
		
		
		List list = query.getAll().getResult();
		
		long afterMemory = Runtime.getRuntime().totalMemory();
		long remainMemeory = Runtime.getRuntime().freeMemory();
		long end = System.currentTimeMillis();
		
		System.out.println("最大堆内存："+maxMemory/1024);
		
		System.out.println("使用前内存:"+beforeMemory/1024+";使用后内存："+afterMemory/1024+";共消耗内存："+(afterMemory-remainMemeory)/1024);
		
		System.out.println("剩余内存:"+remainMemeory/1024);
		
		logger.info("共耗时:"+(end-start));
		
		//int size = searcher.getTotalCount();
		
		System.out.println(list.size());
	}

}
