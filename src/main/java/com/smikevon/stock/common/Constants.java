package com.smikevon.stock.common;

import com.smikevon.stock.util.Config;

public class Constants {

	public interface IndexPath {
		public static final String STOCK_INFO_INDEX = Config.init().gets("STOCK_INFO_INDEX");
		public static final String BIG_DEAL_INDEX = Config.init().gets("BIG_DEAL_INDEX");
	}

    public static final int PAGE_SIZE = 1000;
    
    public static final int WORKER_CRAWL_PAGE_NUMBER = 4;
}
