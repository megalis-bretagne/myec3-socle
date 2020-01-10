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
package org.myec3.socle.synchro.api;

import java.util.List;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.synchro.api.constants.SynchronizationType;

/**
 * This class represents a specific configuration that can be used during the
 * synchronization process.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class SynchronizationConfiguration {

	private List<Long> listApplicationIdToResynchronize;
	private SynchronizationType synchronizationType;
	private String sendingApplication;

	/**
	 * Default constructor. Do nothing.
	 */
	public SynchronizationConfiguration() {
	}

	/**
	 * Constructor. Initialize the list of application to synchronize, the type
	 * of synchronization and the name of the application sending the
	 * synchronization.
	 * 
	 * @param listApplicationIdToResynchronize
	 *            : the list of {@link Application} to synchronize
	 * @param synchronizationType
	 *            : the {@link SynchronizationType} to use
	 * @param sendingApplication
	 *            : the name of the application sending the synchronization
	 */
	public SynchronizationConfiguration(
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.listApplicationIdToResynchronize = listApplicationIdToResynchronize;
		this.synchronizationType = synchronizationType;
		this.sendingApplication = sendingApplication;
	}

	/**
	 * @return the list of {@link Application} to synchronize
	 */
	public List<Long> getListApplicationIdToResynchronize() {
		return listApplicationIdToResynchronize;
	}

	public void setListApplicationIdToResynchronize(
			List<Long> listApplicationIdToResynchronize) {
		this.listApplicationIdToResynchronize = listApplicationIdToResynchronize;
	}

	/**
	 * @return the {@link SynchronizationType} to use
	 */
	public SynchronizationType getSynchronizationType() {
		return synchronizationType;
	}

	public void setSynchronizationType(SynchronizationType synchronizationType) {
		this.synchronizationType = synchronizationType;
	}

	/**
	 * @return the name of the application sending the synchronization
	 */
	public String getSendingApplication() {
		return sendingApplication;
	}

	public void setSendingApplication(String sendingApplication) {
		this.sendingApplication = sendingApplication;
	}
}
