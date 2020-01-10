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
package org.myec3.socle.synchro.api.constants;

import org.myec3.socle.core.domain.model.Application;

/**
 * Enumeration used to identify what are the parameters sent into the JMS during
 * the synchronization of a {@link Resource}
 * 
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * 
 */
public enum SynchronizationParametersType {

	/**
	 * The {@link Resource} object to synchronize
	 */
	RESOURCE("resource"),

	/**
	 * The List of {@link Application} to synchronize
	 */
	LIST_APPLICATION_ID("listApplicationIdToResynchronize"),

	/**
	 * The Type of synchronization. Used to know what service is responsible for
	 * the current synchronization
	 * 
	 * @see SynchronizationType.class
	 */
	SYNCHRONIZATION_TYPE("synchronizationType"),

	/**
	 * The Type of synchronization job.
	 * 
	 * @see SynchronizationJobType.class
	 */
	SYNCHRONIZATION_JOB_TYPE("synchronizationJobType"),

	/**
	 * The name of the relation updated.
	 */
	RELATION_NAME("relationName"),

	/**
	 * The list of created resources in the collection updated.
	 */
	CREATED_RESOURCES("createdResources"),

	/**
	 * The list of updated resources in the collection updated.
	 */
	UPDATED_RESOURCES("updatedResources"),

	/**
	 * The list of added resources in the collection updated.
	 */
	ADDED_RESOURCES("addedResources"),

	/**
	 * The list of removed resources in the collection updated.
	 */
	REMOVED_RESOURCES("removedResources"),

	/**
	 * The name of the application which send the jms
	 */
	SENDING_APPLICATION("sendingApplication"),

	/**
	 * Number of attempts to reschedule jms sending
	 */
	NB_ATTEMPTS("nbAttempts");

	private final String value;

	private SynchronizationParametersType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
