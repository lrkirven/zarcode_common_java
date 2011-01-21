package com.zarcode.platform.loader;

import javax.jdo.Query;

public interface AbstractLoaderDao {

	public void loadObject(Object dataObject);
	public long deleteAll(Class cls);

}