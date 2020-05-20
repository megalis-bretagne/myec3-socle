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
package org.myec3.socle.synchro.api.quartz.job;

import java.util.List;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.synchro.api.JMSSynchronizationServiceImpl;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * This generic abstract job provide access to common services and methods that
 * will be used in each synchronization job.
 * 
 * @see PropagateCCSynchronizationJob
 * @see PropagateCUDSynchronizationJob
 * @see PropagateCUSynchronizationJob
 * 
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public abstract class GenericSynchronizationJob extends QuartzJobBean {

	@Autowired
	@Qualifier("synchronizationCoreService")
	private JMSSynchronizationServiceImpl synchronizationService;

	/**
	 * All these fields will be injected by Spring. The injection is made
	 * declaratively in application context file because of this class extends
	 * QuartzJobBean which is not a Spring service : lifecycle is managed by quartz.
	 */

	/**
	 * The {@link Resource} to synchronize
	 */
	private Resource resource;

	/**
	 * Relation name during collection update/Create
	 */
	private String relationName;

	/**
	 * Updated resources during collection create
	 */
	private List<Resource> createdResources;

	/**
	 * Updated resources during collection update
	 */
	private List<Resource> updatedResources;

	/**
	 * Added resources during collection update
	 */
	private List<Resource> addedResources;

	/**
	 * Removed resources during collection update
	 */
	private List<Resource> removedResources;

	/**
	 * List of applications to resynchronize
	 */
	private List<Long> listApplicationIdToResynchronize;

	/**
	 * Type of synchronization
	 */
	private SynchronizationType synchronizationType;

	/**
	 * Type of job synchronization
	 */
	private SynchronizationJobType synchronizationJobType;

	/**
	 * Name of the application sending JMS
	 */
	private String sendingApplication;

	/**
	 * Number of attempts for retrying synchronization jms job
	 */
	private int nbAttempts;

	/**
	 * @return the synchronizationType
	 */
	public SynchronizationType getSynchronizationType() {
		return synchronizationType;
	}

	/**
	 * @param synchronizationType the synchronizationType to set
	 */
	public void setSynchronizationType(SynchronizationType synchronizationType) {
		this.synchronizationType = synchronizationType;
	}

	/**
	 * @return the sendingApplication
	 */
	public String getSendingApplication() {
		return sendingApplication;
	}

	/**
	 * @param sendingApplication the name of the application sending JMS
	 */
	public void setSendingApplication(String sendingApplication) {
		this.sendingApplication = sendingApplication;
	}

	/**
	 * @return the list of Application Id to resynchronize
	 */
	public List<Long> getListApplicationIdToResynchronize() {
		return listApplicationIdToResynchronize;
	}

	/**
	 * @param listApplicationIdToResynchronize the listApplicationIdToResynchronize
	 *                                         to set
	 */
	public void setListApplicationIdToResynchronize(
			List<Long> listApplicationIdToResynchronize) {
		this.listApplicationIdToResynchronize = listApplicationIdToResynchronize;
	}

	/**
	 * Setter used to perform injection
	 * 
	 * @param resource : resource object to synchronize
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Provides acces to private resource object injected
	 * 
	 * @return the resource object to synchronize
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * @return the relationName
	 */
	public String getRelationName() {
		return relationName;
	}

	/**
	 * @param relationName : the relationName to set
	 */
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	/**
	 * @return the updatedResources
	 */
	public List<Resource> getCreatedResources() {
		return createdResources;
	}

	/**
	 *
	 * @param createdResources
	 */
	public void setCreatedResources(List<Resource> createdResources) {
		this.createdResources = createdResources;
	}

	/**
	 * @return the updatedResources
	 */
	public List<Resource> getUpdatedResources() {
		return updatedResources;
	}

	/**
	 * @param updatedResources : the updatedResources to set
	 */
	public void setUpdatedResources(List<Resource> updatedResources) {
		this.updatedResources = updatedResources;
	}

	/**
	 * @return the addedResources
	 */
	public List<Resource> getAddedResources() {
		return addedResources;
	}

	/**
	 * @param addedResources : the addedResources to set
	 */
	public void setAddedResources(List<Resource> addedResources) {
		this.addedResources = addedResources;
	}

	/**
	 * @return the removedResources
	 */
	public List<Resource> getRemovedResources() {
		return removedResources;
	}

	/**
	 * @param removedResources : the removedResources to set
	 */
	public void setRemovedResources(List<Resource> removedResources) {
		this.removedResources = removedResources;
	}

	/**
	 * @return the synchronizationJobType
	 */
	public SynchronizationJobType getSynchronizationJobType() {
		return synchronizationJobType;
	}

	/**
	 * @param synchronizationJobType : the synchronizationJobType to set
	 */
	public void setSynchronizationJobType(
			SynchronizationJobType synchronizationJobType) {
		this.synchronizationJobType = synchronizationJobType;
	}

	/**
	 * @return the synchronizationService
	 */
	public JMSSynchronizationServiceImpl getSynchronizationService() {
		return synchronizationService;
	}

	/**
	 * @param synchronizationService : the synchronizationService to set
	 */
	public void setSynchronizationService(
			JMSSynchronizationServiceImpl synchronizationService) {
		this.synchronizationService = synchronizationService;
	}

	/**
	 * @return the nbAttempts of the synchronization
	 */
	public int getNbAttempts() {
		return nbAttempts;
	}

	/**
	 * @param nbAttempts : the nbAttempts to set
	 */
	public void setNbAttempts(int nbAttempts) {
		this.nbAttempts = nbAttempts;
	}

}
