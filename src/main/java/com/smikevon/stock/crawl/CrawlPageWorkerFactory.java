package com.smikevon.stock.crawl;

import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 冯枭 E-mail:fengxiao@xiaomi.com
 * @since 创建时间: 14-12-5 下午5:31
 */
public class CrawlPageWorkerFactory {

    private static final Logger logger = Logger.getLogger(CrawlPageWorker.class);

    private ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
    private ExecutorService executorService = Executors.newCachedThreadPool();
}
