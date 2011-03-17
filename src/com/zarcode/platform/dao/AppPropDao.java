package com.zarcode.platform.dao;

import java.util.List;

import javax.jdo.Query;

import com.zarcode.platform.loader.AbstractLoaderDao;
import com.zarcode.platform.model.AppPropDO;

public class AppPropDao extends BaseDao implements AbstractLoaderDao  {
	
	
	public List<AppPropDO> getProps() {
		Query query = pm.newQuery(AppPropDO.class);
		List<AppPropDO> res = (List<AppPropDO>)query.execute();
		return res;
	}
	
	public void loadObject(Object dataObject) {
		addProp((AppPropDO)dataObject);
	}
	
	public void addTestProp() {
		AppPropDO prop = new AppPropDO("TEST");
		pm.makePersistent(prop);
	}
	
	public void addProp(AppPropDO p) {
		pm.makePersistent(p);
	}
	
	public void addAppVersion(String version) {
		AppPropDO prop = new AppPropDO("APP_VERSION");
		prop.setStringValue(version);
		pm.makePersistent(prop);
	}
	
}
