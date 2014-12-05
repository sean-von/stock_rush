package com.smikevon.stock.dao.impl;

import com.smikevon.stock.dao.StockDao;
import com.smikevon.stock.entity.Stock;

import java.io.Serializable;

public class StockDaoImpl<E extends Serializable> extends BaseDaoImpl<Stock,String> implements StockDao {
	
}
