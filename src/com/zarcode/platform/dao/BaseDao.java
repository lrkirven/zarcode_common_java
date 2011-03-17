package com.zarcode.platform.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public abstract class BaseDao {
	
	/**
	 * logger
	 */
	private static final Logger logger = Logger.getLogger(BaseDao.class.getName());
	
	protected PersistenceManager pm = null;
	
	public BaseDao() {
		pm = PMF.get().getPersistenceManager(); 
	}
	
	public void close() {
		pm.close();
	}
	
	public void addObject(Object dataObject) {
	}
	
	public long deleteAll(Class cls) {
		long rows = 0;
		Query q = pm.newQuery(cls);
		rows = q.deletePersistentAll();
		return rows;
	}
	
	protected String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String str = "\n" + sw.toString();
        return str;
	}

}
