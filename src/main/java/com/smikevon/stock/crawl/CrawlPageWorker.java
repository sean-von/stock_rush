package com.smikevon.stock.crawl;

import com.smikevon.stock.common.Constants;
import com.smikevon.stock.crawl.helper.StockDataCrawlHelper;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 抓取页面线程,抓取（startPage,endPage]页面
 * @author 冯枭 E-mail:fengxiao@xiaomi.com
 * @since 创建时间: 14-12-5 下午5:04
 */
public class CrawlPageWorker<E> implements Callable {

    private static final Logger logger = Logger.getLogger(CrawlPageWorker.class);

    private ConcurrentLinkedQueue<E> queue;
    private String timestamp;
    private int startPage;
    private int endPage;


    CrawlPageWorker(ConcurrentLinkedQueue<E> queue,int startPage,int endPage,String timestamp){
        this.queue = queue;
        this.startPage = startPage;
        this.endPage = endPage;
        this.timestamp = timestamp;
    }


    @Override
    public Object call() throws Exception {
        int pageNo = startPage;
        String url = "http://quotes.money.163.com/hs/realtimedata/service/dadan.php";
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("t", "0");
        map.put("query", "vol:300;");//大于100手的都抓下来
        map.put("fields", "RN,SYMBOL,NAME,DATE,PRICE,PRICE_PRE,PERCENT,TURNOVER_INC,VOLUME_INC,TRADE_TYPE,,CODE");
        map.put("sort", "DATE");
        map.put("order", "desc");
        map.put("type", "query");
        do {
            map.put("page", pageNo+"");
            map.put("count", Constants.PAGE_SIZE+"");
            map.put("req", timestamp);
            String body = new String(StockDataCrawl.getResponse(url, map));
            Map params = StockDataCrawlHelper.converBodyToMap(body);
            List tmplist = (List)params.get("list");
            queue.addAll(tmplist);
            logger.info("大小："+queue.size());
            pageNo++;
        } while (pageNo<endPage);
        return null;
    }

}
