package com.zarcode.common;

import java.util.HashMap;
import java.util.List;

import com.zarcode.platform.dao.AppPropDao;
import com.zarcode.platform.model.AppPropDO;

public class ApplicationProps {
	
	private static ApplicationProps _instance = null;
	
	
	public static final String GDATA_USERNAME = "GDATE_USERNAME";
	public static final String GDATA_PASSWORD = "GDATE_PASSWORD";

	private HashMap<String, AppPropDO> map = null;
	private boolean loaded = false;

	private ApplicationProps() {
	}
	
	public static ApplicationProps getInstance() {
		if (_instance == null) {
			_instance = new ApplicationProps();
		}
		return _instance;
	}
	
	private void load() {
		int i = 0;
		AppPropDO prop = null;
	
		if (!loaded) {
			map = new HashMap<String, AppPropDO>();
			AppPropDao dao = new AppPropDao();
			List<AppPropDO> props = dao.getProps();
			
			if (props != null && props.size() > 0) {
				for (i=0; i<props.size(); i++) {
					prop = props.get(i);
					map.put(prop.getName(), prop);
				}
			}
			loaded = true;
		}
	}
	

	public AppPropDO getProp(String name) {
		if (!loaded) {
			load();
		}
		AppPropDO prop = null;
		if (map != null) {
			prop = map.get(name);
		}
		if (prop == null) {
			AppPropDao dao = new AppPropDao();
			dao.addTestProp();
		}
		return prop;
	}
	
}
