package com.zarcode.platform.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class AppPropDO extends AbstractLoaderDO implements Serializable {

	@PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long propId = null; 
	
	@Persistent
	private String name = null;

	@Persistent
	private String stringValue = null;
	
	@Persistent
	private Number numberValue = null;
	
	@Persistent
	private Date dateValue = null;

	
	public AppPropDO(String name) {
		this.name = name;
		this.stringValue = "test";
	}
	
	public void postCreation() {
	}
	
	public Long getPropId() {
		return propId;
	}
	
	public String getName() {
		return name;
	}
	
	public Date getDateValue() {
		return dateValue;
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public Number getNumberValue() {
		return numberValue;
	}

}