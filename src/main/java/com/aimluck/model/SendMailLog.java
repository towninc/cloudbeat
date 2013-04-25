package com.aimluck.model;

import java.io.Serializable;
import java.util.Date;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

@Model(kind = "sml", schemaVersion = 1, schemaVersionName = "sV")
public class SendMailLog implements Serializable {

	private static final long serialVersionUID = 1L;
	@Attribute(primaryKey = true, name = "k")
	private Key key;
	@Attribute(version = true, name = "v")
	private Long version;
	@Attribute(name = "t")
	private String title;
	@Attribute(name = "a")
	private String address;
	@Attribute(name = "b", lob = true)
	private String body;
	@Attribute(name = "rC")
	private Integer retryCount;
	@Attribute(name = "cLK")
	private Key checkLogKey;
	@Attribute(name = "cK")
	private Key checkKey;
	@Attribute(name = "cD")
	private Date createDate;
	@Attribute(name = "uD")
	private Date updateDate;
	@Attribute(name = "eM", lob = true)
	private String exceptionMessage;
	@Attribute(name = "iS")
	private Boolean isSuccessed;
	
	

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 * 
	 * @param key
	 *            the key
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * Returns the version.
	 * 
	 * @return the version
	 */
	public Long getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 * 
	 * @param version
	 *            the version
	 */
	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SendMailLog other = (SendMailLog) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		return true;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public Key getCheckLogKey() {
		return checkLogKey;
	}

	public void setCheckLogKey(Key checkLogKey) {
		this.checkLogKey = checkLogKey;
	}

	public Key getCheckKey() {
		return checkKey;
	}

	public void setCheckKey(Key checkKey) {
		this.checkKey = checkKey;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public Boolean getIsSuccessed() {
		return isSuccessed;
	}

	public void setIsSuccessed(Boolean isSuccessed) {
		this.isSuccessed = isSuccessed;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
