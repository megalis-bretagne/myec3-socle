/**
 * Copyright (c) 2011 Atos Bourgogne
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.synchro.core.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.domain.model.enums.ResourceType;

/**
 * This class represents a synchronization subscription used to know what are
 * the applications to synchronize and for what type of resource.<br />
 * 
 * If you want to synchronize a new application you must create a new
 * synchronization subscription.<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
public class SynchronizationSubscription implements Serializable, PE {

	private static final long serialVersionUID = 9093666066816224604L;

	private Long id;
	private Application application;
	private ResourceType resourceLabel;
	private String uri;
	private Boolean https;
	private String certificateUri;
	private String certificatePassword;

	// private Customer customer;

	private SynchronizationFilter synchronizationFilter;

	/**
	 * Default constructor. Do nothing.
	 */
	public SynchronizationSubscription() {
	}

	/**
	 * Contructor. Initialize the application, the resourcetype and the URI.
	 * 
	 * @param application   : the distant {@link Application} to synchronize.
	 * @param resourceLabel : the {@link ResourceType} to synchronize.
	 * @param uri           : the distant application URI to call during the
	 *                      synchronization process.
	 */
	public SynchronizationSubscription(Application application, ResourceType resourceLabel, String uri) {
		this.application = application;
		this.resourceLabel = resourceLabel;
		this.uri = uri;
	}

	/**
	 * Technical id of the SynchronizationLog
	 * 
	 * @return the id for this SynchronizationLog
	 */
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the {@link Application} subscribed at the synchronization process.
	 */
	@ManyToOne
	@JoinColumn(nullable = false,name = "")
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	/**
	 * @return the {@link ResourceType} subscribed at the synchronization process.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ResourceType getResourceLabel() {
		return resourceLabel;
	}

	public void setResourceLabel(ResourceType resourceLabel) {
		this.resourceLabel = resourceLabel;
	}

	/**
	 * @return the URI of the distant application to call during the synchronization
	 *         process.
	 */
	@Column(nullable = false)
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	// /**
	// *
	// * This field is not mandatory because companies don't have a customer.
	// You
	// * must fill this field only for Organism and OrganismDepartment.
	// *
	// * @return the {@link Customer} associated with this subscription.
	// */
	// @ManyToOne
	// @JoinColumn(nullable = true)
	// public Customer getCustomer() {
	// return customer;
	// }
	//
	// public void setCustomer(Customer customer) {
	// this.customer = customer;
	// }

	/**
	 * @return the {@link SynchronizationFilter} to use during the synchronization
	 *         process.
	 */
	@ManyToOne
	@JoinColumn(nullable = false)
	public SynchronizationFilter getSynchronizationFilter() {
		return synchronizationFilter;
	}

	public void setSynchronizationFilter(SynchronizationFilter synchronizationFilter) {
		this.synchronizationFilter = synchronizationFilter;
	}

	@Column(columnDefinition = "BIT default 0")
	public Boolean getHttps() {
		return https;
	}

	public void setHttps(Boolean https) {
		this.https = https;
	}

	@Column(nullable = true)
	public String getCertificateUri() {
		return certificateUri;
	}

	public void setCertificateUri(String certificateUri) {
		this.certificateUri = certificateUri;
	}

	@Column(nullable = true)
	public String getCertificatePassword() {
		return certificatePassword;
	}

	public void setCertificatePassword(String certificatePassword) {
		this.certificatePassword = certificatePassword;
	}

}
