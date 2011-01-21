package com.zarcode.platform.dao;

import javax.jdo.Query;

import com.zarcode.platform.loader.AbstractLoaderDao;

public class LoaderDao extends BaseDao implements AbstractLoaderDao {
	
	public void addObject(Object dataObject) {
		if (dataObject != null) {
  	      	pm.makePersistent(dataObject); 
		}
	}
	
	public void loadObject(Object dataObject) {
		addObject(dataObject);
	}
	
	public long deleteAll(Class cls) {
		long rows = 0;
		Query q = pm.newQuery(cls);
		rows = q.deletePersistentAll();
		return rows;
	}
	
}
