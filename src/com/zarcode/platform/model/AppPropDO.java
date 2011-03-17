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

	public AppPropDO() {
	}
	
	public AppPropDO(String name) {
		this.name = name;
	}
	
	public void postCreation() {
	}
	
	public Long getPropId() {
		return propId;
	}
	
	public void setPropId(Long propId) {
		this.propId = propId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getDateValue() {
		return dateValue;
	}
	
	public void setDateValue(Date val) {
		dateValue = val;
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public void setStringValue(String val) {
		stringValue = val;
	}
	
	public Number getNumberValue() {
		return numberValue;
	}
	
	public void setNumberValue(Number val) {
		numberValue = val;
	}

}