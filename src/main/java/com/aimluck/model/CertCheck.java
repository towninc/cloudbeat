package com.aimluck.model;

import java.io.Serializable;

import com.google.appengine.api.datastore.Key;
import java.util.Date;
import java.util.List;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

@Model(kind = "c", schemaVersion = 1, schemaVersionName = "sV")
public class CertCheck implements Serializable {

	private static final long serialVersionUID = 1L;
	@Attribute(primaryKey = true, name = "k")
	private Key key;
	@Attribute(version = true, name = "v")
	private Long version;
	@Attribute(name = "n")
	private String name;
	@Attribute(name = "s")
	private String status;
	@Attribute(name = "u")
	private String url;
	@Attribute(name = "pU")
	private String preloadUrl;
	@Attribute(name = "fP")
	private String formParams;
	@Attribute(name = "aT")
	private String assertText;
	@Attribute(name = "xP")
	private String xPath;
	@Attribute(name = "tO")
	private int timeOut;
	@Attribute(name = "a")
	private Boolean active = true;


	@Attribute(name="lD")
    private Date limitDate;
    @Attribute(name="p")
    private Long period;
	
	@Attribute(name = "d", lob = true)
	private String description;
	@Attribute(name = "eM", lob = true)
	private String errorMessage;
	@Attribute(unindexed = true, name = "r")
	private List<String> recipients;
	@Attribute(name = "uDR")
	private ModelRef<UserData> userDataRef = new ModelRef<UserData>(
			UserData.class);
	@Attribute(name = "cA")
	private Date createdAt;
	@Attribute(name = "uA")
	private Date updatedAt;
	@Attribute(name = "fC")
	private int failCount;
	@Attribute(name = "fT")
	private int failThreshold;
	@Attribute(name="l")
	private Boolean login = false;
	@Attribute(name="cSSL")
	private Boolean checkSSL = false;
	@Attribute(name="cDom")
	private Boolean checkDomain = false;
	
	
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
		CertCheck other = (CertCheck) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		return true;
	}

	public Date getLimitDate() {
		return limitDate;
	}

	public void setLimitDate(Date limitDate) {
		this.limitDate = limitDate;
	}
	
	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 
	 * @return
	 */
	public String getAssertText() {
		return assertText;
	}

	/**
	 * 
	 * @param assertText
	 */
	public void setAssertText(String assertText) {
		this.assertText = assertText;
	}

	/**
	 * 
	 * @return
	 */
	public String getXPath() {
		return xPath;
	}

	/**
	 * 
	 * @param xPath
	 */
	public void setXPath(String xPath) {
		this.xPath = xPath;
	}

	/**
	 * 
	 * @return
	 */
	public int getTimeOut() {
		return timeOut;
	}

	/**
	 * 
	 * @param timeOut
	 */
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @return
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * 
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * 
	 * @return
	 */
	public ModelRef<UserData> getUserDataRef() {
		return userDataRef;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getRecipients() {
		return recipients;
	}

	/**
	 * 
	 * @param recipients
	 */
	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	/**
	 * 
	 * @return
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * 
	 * @param createdAt
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * 
	 * @return
	 */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * 
	 * @param updatedAt
	 */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public int getFailCount() {
		return failCount;
	}

	public void setFailThreshold(int failThreshold) {
		this.failThreshold = failThreshold;
	}

	public int getFailThreshold() {
		return failThreshold;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getPreloadUrl() {
		return preloadUrl;
	}

	public void setPreloadUrl(String preloadUrl) {
		this.preloadUrl = preloadUrl;
	}

	public String getFormParams() {
		return formParams;
	}

	public void setFormParams(String formParams) {
		this.formParams = formParams;
	}

	public String getUserId() {
		if (this.userDataRef != null) {
			return Long.toString(this.userDataRef.getKey().getId());
		}
		return null;
	}

	public Boolean getLogin() {
		return login;
	}

	public void setLogin(Boolean login) {
		this.login = login;
	}

	public Boolean getCheckSSL() {
		return checkSSL;
	}

	public void setCheckSSL(Boolean checkSSL) {
		this.checkSSL = checkSSL;
	}

	public Boolean getCheckDomain() {
		return checkDomain;
	}

	public void setCheckDomain(Boolean checkDomain) {
		this.checkDomain = checkDomain;
	}

	
}
