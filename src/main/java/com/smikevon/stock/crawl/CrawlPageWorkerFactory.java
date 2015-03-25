package com.smikevon.stock.crawl;

import com.smikevon.stock.common.Constants;
import com.smikevon.stock.crawl.helper.StockDataCrawlHelper;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 冯枭 E-mail:fengxiao@xiaomi.com
 * @since 创建时间: 14-12-5 下午5:31
 */
public class CrawlPageWorkerFactory {

    private static final Logger logger = Logger.getLogger(CrawlPageWorker.class);

    private static ConcurrentLinkedQueue<Map> queue = new ConcurrentLinkedQueue<Map>();
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    
    
    public static List crawl(String timestamp){
        //int pageCount = CrawlPageWorker.getPageCount(timestamp);
        int workerNumber = 10 ;//pageCount / Constants.WORKER_CRAWL_PAGE_NUMBER;
        System.out.println("thread count : " + workerNumber);
        CountDownLatch latch = new CountDownLatch(workerNumber);
        for (int i=0;i<workerNumber;i++){
            executorService.submit(new CrawlPageWorker<Map>(queue,i*Constants.WORKER_CRAWL_PAGE_NUMBER,timestamp,latch));
        }
        try {
            latch.await();
            logger.info("共抓取数据:"+queue.size());
            return Arrays.asList(queue.toArray(new HashMap[0]));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    


}
