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
package org.myec3.socle.synchro.scheduler.job;

import java.util.List;

import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Person;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.scheduler.manager.ResourceSynchronizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * This generic abstract job provide access to common services and methods that
 * will be used in each Synchronization job.
 * 
 * @see CollectionCreateSynchronizationJob
 * @see CollectionUpdateSynchronizationJob
 * @see CollectionRemoveSynchronizationJob
 * @see CreationSynchronizationJob
 * @see UpdateSynchronizationJob
 * @see DeletionSynchronizationJob
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis cucchietti <denis.cucchietti@atosorigin.com>
 */
public abstract class GenericSynchronizationJob extends QuartzJobBean {

	private static final Logger logger = LoggerFactory
			.getLogger(GenericSynchronizationJob.class);

	/**
	 * All these fields will be injected by Spring. The injection is made
	 * declaratively in application context file because of this class extends
	 * QuartzJobBean which is not a Spring service : lifecycle is managed by quartz.
	 */

	/**
	 * Resource to synchronize
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
	 * Service used to synchronize applications
	 */
	@Autowired
	@Qualifier("applicationSynchronizer")
	private ResourceSynchronizationManager<Application> applicationSynchronizer;

	/**
	 * Service used to synchronize users
	 */
	@Autowired
	@Qualifier("userSynchronizer")
	private ResourceSynchronizationManager<User> userSynchronizer;

	/**
	 * Service used to synchronize roles
	 */
	@Autowired
	@Qualifier("roleSynchronizer")
	private ResourceSynchronizationManager<Role> roleSynchronizer;

	/**
	 * Service used to synchronize admin
	 */
	@Autowired
	@Qualifier("adminSynchronizer")
	private ResourceSynchronizationManager<AdminProfile> adminSynchronizer;

	/**
	 * Service used to synchronize agents
	 */
	@Autowired
	@Qualifier("agentSynchronizer")
	private ResourceSynchronizationManager<AgentProfile> agentSynchronizer;

	/**
	 * Service used to synchronize employees
	 */
	@Autowired
	@Qualifier("employeeSynchronizer")
	private ResourceSynchronizationManager<EmployeeProfile> employeeSynchronizer;

	/**
	 * Service used to synchronize organisms
	 */
	@Autowired
	@Qualifier("organismSynchronizer")
	private ResourceSynchronizationManager<Organism> organismSynchronizer;

	/**
	 * Service used to synchronize companies
	 */
	@Autowired
	@Qualifier("companySynchronizer")
	private ResourceSynchronizationManager<Company> companySynchronizer;

	/**
	 * Service used to synchronize organism departments
	 */
	@Autowired
	@Qualifier("organismDepartmentSynchronizer")
	private ResourceSynchronizationManager<OrganismDepartment> organismDepartmentSynchronizer;

	/**
	 * Service used to synchronize company departements
	 */
	@Autowired
	@Qualifier("companyDepartmentSynchronizer")
	private ResourceSynchronizationManager<CompanyDepartment> companyDepartmentSynchronizer;

	/**
	 * Service used to synchronize establishments
	 */
	@Autowired
	@Qualifier("establishmentSynchronizer")
	private ResourceSynchronizationManager<Establishment> establishmentSynchronizer;

	/**
	 * Service used to synchronize person
	 */
	@Autowired
	@Qualifier("personSynchronizer")
	private ResourceSynchronizationManager<Person> personSynchronizer;

	/**
	 * Service used to synchronize customer
	 */
	@Autowired
	@Qualifier("customerSynchronizer")
	private ResourceSynchronizationManager<Customer> customerSynchronizer;

	/**
	 * List of applications to resynchronize
	 */
	private List<Long> listApplicationIdToResynchronize;

	/**
	 * Type of synchronization
	 */
	private SynchronizationType synchronizationType;

	/**
	 * Name of the application sending JMS
	 */
	private String sendingApplication;

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
	 * Setter used to perform injection
	 * 
	 * @param applicationSynchronizer : applicationSynchronizer to call
	 */
	public void setApplicationSynchronizer(
			ResourceSynchronizationManager<Application> applicationSynchronizer) {
		this.applicationSynchronizer = applicationSynchronizer;
	}

	/**
	 * @return the relationName
	 */
	public String getRelationName() {
		return relationName;
	}

	/**
	 * @param relationName the relationName to set
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
	 * @param updatedResources the updatedResources to set
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
	 * @param updatedResources the updatedResources to set
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
	 * @param addedResources the addedResources to set
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
	 * @param removedResources the removedResources to set
	 */
	public void setRemovedResources(List<Resource> removedResources) {
		this.removedResources = removedResources;
	}

	/**
	 * Setter used to perform injection
	 * 
	 * @param userSynchronizer : userSynchronizer to call
	 */
	public void setUserSynchronizer(
			ResourceSynchronizationManager<User> userSynchronizer) {
		this.userSynchronizer = userSynchronizer;
	}

	/**
	 * Setter used to perform injection
	 * 
	 * @param adminSynchronizer : adminSynchronizer to call
	 */
	public void setAdminSynchronizer(ResourceSynchronizationManager<AdminProfile> adminSynchronizer) {
		this.adminSynchronizer = adminSynchronizer;
	}

	/**
	 * Setter used to perform injection
	 * 
	 * @param agentSynchronizer : agentSynchronizer to call
	 */
	public void setAgentSynchronizer(
			ResourceSynchronizationManager<AgentProfile> agentSynchronizer) {
		this.agentSynchronizer = agentSynchronizer;
	}

	/**
	 * Setter used to perform injection
	 * 
	 * @param agentSynchronizer : employeeSynchronizer to call
	 */
	public void setEmployeeSynchronizer(
			ResourceSynchronizationManager<EmployeeProfile> employeeSynchronizer) {
		this.employeeSynchronizer = employeeSynchronizer;
	}

	/**
	 * Setter used to perform injection
	 * 
	 * @param agentSynchronizer : organismSynchronizer to call
	 */
	public void setOrganismSynchronizer(
			ResourceSynchronizationManager<Organism> organismSynchronizer) {
		this.organismSynchronizer = organismSynchronizer;
	}

	/**
	 * Setter used to perform injection
	 * 
	 * @param agentSynchronizer : companySynchronizer to call
	 */
	public void setCompanySynchronizer(
			ResourceSynchronizationManager<Company> companySynchronizer) {
		this.companySynchronizer = companySynchronizer;
	}

	/**
	 * Setter used to perform injection
	 * 
	 * @param agentSynchronizer : organismDepartmentSynchronizer to call
	 */
	public void setOrganismDepartmentSynchronizer(
			ResourceSynchronizationManager<OrganismDepartment> organismDepartmentSynchronizer) {
		this.organismDepartmentSynchronizer = organismDepartmentSynchronizer;
	}

	/**
	 * Setter used to perform injection
	 * 
	 * @param agentSynchronizer : companyDepartmentSynchronizer to call
	 */
	public void setCompanyDepartmentSynchronizer(
			ResourceSynchronizationManager<CompanyDepartment> companyDepartmentSynchronizer) {
		this.companyDepartmentSynchronizer = companyDepartmentSynchronizer;
	}

	/**
	 * Setter used to perform injection
	 * 
	 * @param establishmentSynchronizer : establishmentSynchronizer to call
	 */
	public void setEstablishmentSynchronizer(
			ResourceSynchronizationManager<Establishment> establishmentSynchronizer) {
		this.establishmentSynchronizer = establishmentSynchronizer;
	}

	/**
	 * @param personSynchronizer the personSynchronizer to set
	 */
	public void setPersonSynchronizer(
			ResourceSynchronizationManager<Person> personSynchronizer) {
		this.personSynchronizer = personSynchronizer;
	}

	/**
	 * @param roleSynchronizer the roleSynchronizer to set
	 */
	public void setRoleSynchronizer(
			ResourceSynchronizationManager<Role> roleSynchronizer) {
		this.roleSynchronizer = roleSynchronizer;
	}

	/**
	 * @param customerSynchronizer the customerSynchronizer to set
	 */
	public void setCustomerSynchronizer(
			ResourceSynchronizationManager<Customer> customerSynchronizer) {
		this.customerSynchronizer = customerSynchronizer;
	}

	/**
	 * This methods provides the correct service to call to perform synchronization
	 * depending on the concrete type of the resource object.
	 * 
	 * This method is design to be called by implementing classes.
	 * 
	 * @return the corresponding service implementation
	 */
	protected ResourceSynchronizationManager getSynchronizationManager() {

		if (Application.class.equals(this.resource.getClass())) {
			return this.applicationSynchronizer;
		}
		if (User.class.equals(this.resource.getClass())) {
			return this.userSynchronizer;
		}
		if (Role.class.equals(this.resource.getClass())) {
			return this.roleSynchronizer;
		}
		if (AdminProfile.class.equals(this.resource.getClass())) {
			return this.adminSynchronizer;
		}
		if (AgentProfile.class.equals(this.resource.getClass())) {
			return this.agentSynchronizer;
		}
		if (EmployeeProfile.class.equals(this.resource.getClass())) {
			return this.employeeSynchronizer;
		}
		if (Organism.class.equals(this.resource.getClass())) {
			return this.organismSynchronizer;
		}
		if (OrganismDepartment.class.equals(this.resource.getClass())) {
			return this.organismDepartmentSynchronizer;
		}
		if (Company.class.equals(this.resource.getClass())) {
			return this.companySynchronizer;
		}
		if (CompanyDepartment.class.equals(this.resource.getClass())) {
			return this.companyDepartmentSynchronizer;
		}
		if (Establishment.class.equals(this.resource.getClass())) {
			return this.establishmentSynchronizer;
		}
		if (Role.class.equals(this.resource.getClass())) {
			return this.roleSynchronizer;
		}
		if (Person.class.equals(this.resource.getClass())) {
			return this.personSynchronizer;
		}
		if (Customer.class.equals(this.resource.getClass())) {
			return this.customerSynchronizer;
		}

		// If the resource concrete type is not one of those described behind,
		// We don't really know how to synchronize it, so we throw an Exception
		logger.error(getErrorMessage(this.resource.getClass().toString()));
		throw new NoClassDefFoundError(getErrorMessage(this.resource.getClass()
				.toString()));
	}

	/**
	 * Private method used to build the error message depending of the resource
	 * concrete class
	 * 
	 * @param className : name of the resource concrete class
	 * @return the complete error message
	 */
	private String getErrorMessage(String className) {
		StringBuilder builder = new StringBuilder();
		builder.append("No matching manager found for class '")
				.append(className)
				.append("' !! Resource cannot be synchronized. Please consider adding a corresponding manager.");
		return builder.toString();
	}

}
