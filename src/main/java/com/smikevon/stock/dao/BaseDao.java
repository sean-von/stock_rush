package com.smikevon.stock.dao;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author fengxiao
 *
 * @param <E> 为对象类型
 * @param <K> 主键类型
 */
public interface BaseDao<E,K> {
	
	public Class<E> getEntityClass();
	
	public E getOneById(K key);
	
	public E getOneByParameters(Map<String, Object> params);
	
	public List<E> getListByParameters(Map<String, Object> params);
	
	public long getCountByParameters(Map<String, Object> params);
	
	public void saveOrUpdate(E entity);
	
	public void insert(E entity);
	
	public void insertList(List<E> list);
	
	public void deleteOne(E entity);
	
	public void deleteOneById(K key);
	
	public boolean deleteSomeByParameters(Map<String, Object> params);
	
	public boolean deleteSomeByIds(K... key);
	
	public boolean updateOne(E entity);
	
	public boolean updateOneByParameters(K key, Map<String, Object> params);
	
	public boolean updateList(List<E> list);
	
	public boolean updateSomeByIds(Map<String, Object> params, K... key);
	
}
