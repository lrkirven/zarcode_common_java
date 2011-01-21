package com.zarcode.platform.dao;

import java.util.List;

import javax.jdo.Query;

import com.zarcode.platform.model.AppPropDO;

public class AppDao extends BaseDao {
	
	
	public List<AppPropDO> getProps() {
		Query query = pm.newQuery(AppPropDO.class);
		List<AppPropDO> res = (List<AppPropDO>)query.execute();
		return res;
	}
	
	public void addTestProp() {
		AppPropDO prop = new AppPropDO("TEST");
		pm.makePersistent(prop);
	}
	
}
