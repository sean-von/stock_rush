package com.smikevon.stock.dao.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.smikevon.stock.dao.BaseDao;

public class BaseDaoImpl<E,F> implements BaseDao<E,F>{
	
	private MongoTemplate mongoTemplate;


	public E getOneById(F key) {
		return mongoTemplate.findOne(new Query(where("_Id").is(key)), getEntityClass());
	}

	/***
	 * 按条件查询返回单个实体
	 */
	public E getOneByParameters(Map<String, Object> params) {
		List<E> list = getListByParameters(params);
		return list.size() == 0? null:list.get(0);
	}
	
	public List<E> getListByParameters(Map<String,Object> params) {
		Criteria criteria = new Criteria();
		Iterator<String> iterator = params.keySet().iterator();
		int position = 0;
		while(iterator.hasNext()){
			String key = iterator.next();
			if(position==0){
				criteria = where(key).is(params.get(key));
			}else{
				criteria = criteria.and(key).is(params.get(key));
			}
			position++ ;
		}
		return mongoTemplate.find(new Query(criteria), getEntityClass());
	}

	public long getCountByParameters(Map<String, Object> params) {
		return mongoTemplate.count(new Query(), getEntityClass());
	}

	public void saveOrUpdate(E entity) {
		mongoTemplate.save(entity);
	}

	public void insert(E entity) {
		mongoTemplate.insert(entity);
	}

	public void insertList(List<E> list) {
		mongoTemplate.insert(list);
	}

	public void deleteOne(E entity) {
		mongoTemplate.remove(entity);
	}

	public void deleteOneById(F key) {
		mongoTemplate.remove(new Query(),getEntityClass());
	}

	public boolean deleteSomeByParameters(Map<String, Object> params) {
		return false;
	}

	public boolean deleteSomeByIds(F... key) {
		return false;
	}

	public boolean updateOne(E entity) {
		return false;
	}

	public boolean updateOneByParameters(F key, Map<String, Object> params) {
		return false;
	}

	public boolean updateList(List<E> list) {
		return false;
	}

	public boolean updateSomeByIds(Map<String, Object> params, F... key) {
		return false;
	}

	public Class<E> getEntityClass() {
		return null;
	}
	
}
