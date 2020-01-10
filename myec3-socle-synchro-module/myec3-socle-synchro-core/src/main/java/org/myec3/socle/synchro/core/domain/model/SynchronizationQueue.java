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

import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;

/**
 * This class is used to create a queue of synchronizations to perform with a
 * delay.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
public class SynchronizationQueue implements Serializable, PE {

	private static final long serialVersionUID = 4443168409531724239L;

	private Long id;
	private Long resourceId;
	private ResourceType resourceType;
	private SynchronizationJobType synchronizationJobType;
	private String sendingApplication;

	/**
	 * Default constructor
	 */
	public SynchronizationQueue() {
	}

	public SynchronizationQueue(Long resourceId, ResourceType resourceType,
			SynchronizationJobType synchronizationJobType, String sendingApplication) {
		this.resourceId = resourceId;
		this.resourceType = resourceType;
		this.synchronizationJobType = synchronizationJobType;
		this.sendingApplication = sendingApplication;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = false)
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public SynchronizationJobType getSynchronizationJobType() {
		return synchronizationJobType;
	}

	public void setSynchronizationJobType(SynchronizationJobType synchronizationJobType) {
		this.synchronizationJobType = synchronizationJobType;
	}

	@Column(nullable = true)
	public String getSendingApplication() {
		return sendingApplication;
	}

	public void setSendingApplication(String sendingApplication) {
		this.sendingApplication = sendingApplication;
	}
}
