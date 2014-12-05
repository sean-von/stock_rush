package com.smikevon.stock.common;

import com.smikevon.stock.util.Config;

public class Constants {

	public interface IndexPath {
		public static final String STOCK_INFO_INDEX = Config.init().gets("STOCK_INFO_INDEX");
		public static final String BIG_DEAL_INDEX = Config.init().gets("BIG_DEAL_INDEX");
	}
}
