package com.zarcode.platform.gdata;

import java.util.logging.Logger;

import com.google.gdata.client.GoogleService;
import com.google.gdata.util.AuthenticationException;
import com.zarcode.common.ApplicationProps;
import com.zarcode.common.Util;
import com.zarcode.platform.model.AppPropDO;

public abstract class GDataClient {
	
	private Logger logger = Logger.getLogger(GDataClient.class.getName());
	
	protected GoogleService myService = null;
	
	protected AppPropDO prop = null;
	
	protected String username = null;
	
	protected String password = null;
	
	public GDataClient() {
	}
	
	public void initialize() throws Exception {
		try {
			prop = ApplicationProps.getInstance().getProp("GDATA_USERNAME");
			if (prop == null) {
				throw new Exception("Application properties are not provisioned");
			}
			username = prop.getStringValue();
			prop = ApplicationProps.getInstance().getProp("GDATA_PASSWORD");
			if (prop == null) {
				throw new Exception("Application properties are not provisioned");
			}
			password = prop.getStringValue();
			prop = ApplicationProps.getInstance().getProp("GMAP_DATA_USER_ID");
			if (prop == null) {
				throw new Exception("Application properties are not provisioned");
			}
		} 
		catch(AuthenticationException e) {
			logger.severe("CREDENTIALS -- [EXCEPTION]\n" + Util.getStackTrace(e));
			throw new Exception("Bad Credentials with Google Map Data Source");
		}
	}
	
}
