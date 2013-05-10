package com.aimluck.model;

import java.io.Serializable;

import com.google.appengine.api.datastore.Key;
import java.util.Date;
import java.util.List;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

@Model(kind = "c", schemaVersion = 1, schemaVersionName = "sV")
public class DomainCheck implements Serializable {

	@Attribute(primaryKey = true, name = "k")
    private Key key;
    @Attribute(version = true, name = "v")
    private Long version;
    @Attribute(name = "n")
    private String name;
    @Attribute(name = "s")
    private String status;
    @Attribute(name = "dN")
    private String domainName;
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
		DomainCheck other = (DomainCheck) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		return true;
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
	public Long getPeriod() {
		return period;
	}

	/**
	 *
	 * @param period
	 */
	public void setPeriod(Long period) {
		this.period = period;
	}


	/**
	 *
	 * @return
	 */
	public Date getLimitDate() {
		return limitDate;
	}

	/**
	 *
	 * @param limitDate
	 */
	public void setLimitDate(Date limitDate) {
		this.limitDate = limitDate;
	}

	/**
	 *
	 * @return
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 *
	 * @param domainName
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
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
	public Boolean getActive() {
		return active;
	}

	/**
	 *
	 * @param active
	 */
	public void setActive(Boolean active) {
		this.active = active;
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


}
