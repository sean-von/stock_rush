package com.smikevon.stock.crawl;

import com.smikevon.stock.common.Constants;
import com.smikevon.stock.crawl.helper.StockDataCrawlHelper;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * 抓取页面线程,抓取（startPage,endPage]页面
 * @author 冯枭 E-mail:fengxiao@xiaomi.com
 * @since 创建时间: 14-12-5 下午5:04
 */
public class CrawlPageWorker<E> implements Runnable {

    private static final Logger logger = Logger.getLogger(CrawlPageWorker.class);
    
    public static final String URL = "http://quotes.money.163.com/hs/realtimedata/service/dadan.php";
    public static final Map<String,Object> map = new HashMap<String, Object>();
    static{
        map.put("t", "0");
        map.put("query", "vol:300;");//大于100手的都抓下来
        map.put("fields", "RN,SYMBOL,NAME,DATE,PRICE,PRICE_PRE,PERCENT,TURNOVER_INC,VOLUME_INC,TRADE_TYPE,,CODE");
        map.put("sort", "DATE");
        map.put("order", "desc");
        map.put("type", "query");
        map.put("count", Constants.PAGE_SIZE+"");
    } 


    private ConcurrentLinkedQueue<E> queue;
    private String timestamp;
    private int startPage;
    private CountDownLatch latch;


    CrawlPageWorker(ConcurrentLinkedQueue<E> queue,int startPage,String timestamp,CountDownLatch latch){
        this.queue = queue;
        this.startPage = startPage;
        this.timestamp = timestamp;
        this.latch = latch;
    }


    @Override
    public void run() {
        int pageNo = startPage;
        int endPage = startPage + Constants.WORKER_CRAWL_PAGE_NUMBER;
        System.out.println("startPage : "+ startPage + " , endPage : " + endPage);
        try {
            do {
                map.put("page", pageNo+"");
                map.put("req", timestamp);
                String body = null;
                body = new String(StockDataCrawl.getResponse(URL, map));
                Map params = StockDataCrawlHelper.converBodyToMap(body);
                List tmpList = (List)params.get("list");
                queue.addAll(tmpList);
                pageNo++;
            } while (pageNo< endPage);
        } catch (Exception e) {
            //e.printStackTrace();
        }finally {
            logger.info(Thread.currentThread().getName()+"--listSize--："+queue.size());
            latch.countDown();
        }
    }


    public static int getPageCount(String timestamp){
        map.put("page", 0);
        map.put("req", timestamp);
        String body = null;
        logger.info("timestamp : "+timestamp);
        try {
            body = new String(StockDataCrawl.getResponse(URL, map));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map params = StockDataCrawlHelper.converBodyToMap(body);
        int pageCount = Integer.parseInt(String.valueOf(params.get("pagecount")));
        logger.info("pageCount:"+pageCount);
        return pageCount;
    }

}
