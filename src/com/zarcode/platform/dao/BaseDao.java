package com.zarcode.platform.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

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
	
	protected String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String str = "\n" + sw.toString();
        return str;
	}

}
