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
package org.myec3.socle.synchro.core.service.impl;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.impl.AbstractGenericServiceImpl;
import org.myec3.socle.synchro.core.domain.dao.SynchronizationFilterDao;
import org.myec3.socle.synchro.core.domain.model.SynchronizationFilter;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchronizationFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing methods specific to
 * {@link SynchronizationFilter} objects. used for filter a resource before sent
 * it to the distants applications.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Service("synchronizationFilterService")
public class SynchronizationFilterServiceImpl
		extends AbstractGenericServiceImpl<SynchronizationFilter, SynchronizationFilterDao>
		implements SynchronizationFilterService {

	/**
	 * Default logger used by this class
	 */
	private static final Logger logger = LogManager.getLogger(SynchronizationFilterServiceImpl.class);

	/**
	 * Business Service to perform operation on {@link Role} objects. The concrete
	 * service implementation is injected by Spring container
	 */
	@Autowired
	@Qualifier("roleService")
	private RoleService roleService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void filter(Resource resource, SynchronizationSubscription subscription) {
		logger.debug("Enterring in method filter...");

		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(subscription, "subscription is mandatory. null value is forbidden");

		if (AdminProfile.class.equals(resource.getClass())) {
			this.adminProfileFilter((AdminProfile) resource, subscription);
		}
		if (AgentProfile.class.equals(resource.getClass())) {
			this.agentProfileFilter((AgentProfile) resource, subscription);
		}
		if (EmployeeProfile.class.equals(resource.getClass())) {
			this.employeeProfileFilter((EmployeeProfile) resource, subscription);
		}
		if (Organism.class.equals(resource.getClass())) {
			this.organismFilter((Organism) resource, subscription);
		}
		if (Company.class.equals(resource.getClass())) {
			this.companyFilter((Company) resource, subscription);
		}
		if (OrganismDepartment.class.equals(resource.getClass())) {
			this.organismDepartmentFilter((OrganismDepartment) resource, subscription);
		}
		if (CompanyDepartment.class.equals(resource.getClass())) {
			this.companyDepartmentFilter((CompanyDepartment) resource, subscription);
		}
		if (Establishment.class.equals(resource.getClass())) {
			this.establishmentFilter((Establishment) resource, subscription);
		}
	}

	/**
	 * Filter used on {@link AgentProfile} objects in order to define the object's
	 * datas to send or not to the distants applications.
	 * 
	 * @param agentProfile : the agent profile to filter
	 * @param subscription : the subscription associated at the filter to use.
	 */
	public void adminProfileFilter(AdminProfile adminProfile, SynchronizationSubscription subscription) {
		logger.debug("Processing adminProfile filter...");
		Assert.notNull(adminProfile, "adminProfile is mandatory. null value is forbidden");

		// ADDITIONAL FILTER //
		if (!subscription.getSynchronizationFilter().isAllRolesDisplayed()) {
			// Get the list of roles correspondind at the profile and
			// application.
			// We populate the collection of roles of the admin with
			// only the same roles of the application
			adminProfile.setRoles(
					this.roleService.findAllRoleByProfileAndApplication(adminProfile, subscription.getApplication()));
		}
	}

	/**
	 * Filter used on {@link AgentProfile} objects in order to define the object's
	 * datas to send or not to the distants applications.
	 * 
	 * @param agentProfile : the agent profile to filter
	 * @param subscription : the subscription associated at the filter to use.
	 */
	public void agentProfileFilter(AgentProfile agentProfile, SynchronizationSubscription subscription) {
		logger.debug("Processing agentProfile filter...");
		Assert.notNull(agentProfile, "agentProfile is mandatory. null value is forbidden");

		// COMMONS FILTER //

		// We send only current department of agent with parent department. We
		// don't transmit the entire tree
		if (!agentProfile.getOrganismDepartment().isRootDepartment()) {
			agentProfile.getOrganismDepartment().getParentDepartment().setParentDepartment(null);
			agentProfile.getOrganismDepartment().setRootDepartment(Boolean.FALSE);

			// We don't send the list of applications of parentDepartment
			agentProfile.getOrganismDepartment().getParentDepartment().getOrganism()
					.setApplications(new ArrayList<Application>());
		}

		// We don't send the list of applications of an organism department
		agentProfile.getOrganismDepartment().getOrganism().setApplications(new ArrayList<Application>());

		// We don't send organism's structures
		agentProfile.getOrganismDepartment().getOrganism().setChildStructures(new ArrayList<Structure>());
		agentProfile.getOrganismDepartment().getOrganism().setParentStructures(new ArrayList<Structure>());

		// ADDITIONAL FILTER //
		if (!subscription.getSynchronizationFilter().isAllRolesDisplayed()) {
			// Get the list of roles correspondind at the profile and
			// application.
			// We populate the collection of roles of the agent with
			// only the same roles of the application
			agentProfile.setRoles(
					this.roleService.findAllRoleByProfileAndApplication(agentProfile, subscription.getApplication()));
		}
	}

	/**
	 * Filter used on {@link EmployeeProfile} objects in order to define the
	 * object's datas to send or not to the distants applications.
	 * 
	 * @param employeeProfile : the {@link EmployeeProfile} to filter
	 * @param subscription    : the subscription associated at the filter to use.
	 */
	public void employeeProfileFilter(EmployeeProfile employeeProfile, SynchronizationSubscription subscription) {
		logger.debug("Processing employeeProfile filter...");
		Assert.notNull(employeeProfile, "employeeProfile is mandatory. null value is forbidden");

		// COMMONS FILTERS //

		// We don't send the list of applications of the company
		employeeProfile.getCompanyDepartment().getCompany().setApplications(new ArrayList<Application>());

		// We don't send organism's structures
		employeeProfile.getCompanyDepartment().getCompany().setChildStructures(new ArrayList<Structure>());
		employeeProfile.getCompanyDepartment().getCompany().setParentStructures(new ArrayList<Structure>());

		// ADDITIONAL FILTER //
		if (!subscription.getSynchronizationFilter().isAllRolesDisplayed()) {
			// Get the list of roles correspondind at the profile and
			// application
			// We populate the collection of roles of the employee with
			// only the same roles of the application
			employeeProfile.setRoles(this.roleService.findAllRoleByProfileAndApplication(employeeProfile,
					subscription.getApplication()));
		}
	}

	/**
	 * Filter used on {@link Organism} objects in order to define the object's datas
	 * to send or not to the distants applications.
	 * 
	 * @param organism     : the {@link Organism} to filter
	 * @param subscription : the subscription associated at the filter to use.
	 */
	public void organismFilter(Organism organism, SynchronizationSubscription subscription) {
		logger.debug("Processing organism filter...");
		Assert.notNull(organism, "organism is mandatory. null value is forbidden");

		// ADDITIONAL FILTER //
		if (!subscription.getSynchronizationFilter().isAllApplicationsDisplayed()) {
			organism.setApplications(new ArrayList<Application>());
		}
	}

	/**
	 * Filter used on {@link Company} objects in order to define the object's datas
	 * to send or not to the distants applications.
	 * 
	 * @param company      : the {@link Company} to filter
	 * @param subscription : the subscription associated at the filter to use.
	 */
	public void companyFilter(Company company, SynchronizationSubscription subscription) {
		logger.debug("Processing company filter...");
		Assert.notNull(company, "company is mandatory. null value is forbidden");

		// ADDITIONAL FILTER //
		if (!subscription.getSynchronizationFilter().isAllApplicationsDisplayed()) {
			company.setApplications(new ArrayList<Application>());
		}
	}

	/**
	 * Filter used on {@link OrganismDepartment} objects in order to define the
	 * object's datas to send or not to the distants applications.
	 * 
	 * @param organismDepartment : the {@link OrganismDepartment} to filter
	 * @param subscription       : the subscription associated at the filter to use.
	 */
	public void organismDepartmentFilter(OrganismDepartment organismDepartment,
			SynchronizationSubscription subscription) {
		logger.debug("Processing organism department filter...");
		Assert.notNull(organismDepartment, "organismDepartment is mandatory. null value is forbidden");

		// COMMONS FILTERS //

		// We send only current department with parent department. We don't
		// transmit the entire tree
		if (!organismDepartment.isRootDepartment()) {
			organismDepartment.getParentDepartment().setParentDepartment(null);
			organismDepartment.setRootDepartment(Boolean.FALSE);

			// We don't send the list of applications of the organism contained
			// into the department submitted
			organismDepartment.getParentDepartment().getOrganism().setApplications(new ArrayList<Application>());
		}

		// We don't send the list of applications of the organism of department
		organismDepartment.getOrganism().setApplications(new ArrayList<Application>());

		// We don't send organisms structures
		organismDepartment.getOrganism().setChildStructures(new ArrayList<Structure>());
		organismDepartment.getOrganism().setParentStructures(new ArrayList<Structure>());
	}

	/**
	 * Filter used on {@link CompanyDepartment} objects in order to define the
	 * object's datas to send or not to the distants applications.
	 * 
	 * @param companyDepartment : the {@link CompanyDepartment} to filter
	 * @param subscription      : the subscription associated at the filter to use.
	 */
	public void companyDepartmentFilter(CompanyDepartment companyDepartment, SynchronizationSubscription subscription) {
		logger.debug("Processing company department filter...");
		Assert.notNull(companyDepartment, "companyDepartment is mandatory. null value is forbidden");

		// COMMONS FILTERS //

		// We don't send company's structures
		companyDepartment.getCompany().setChildStructures(new ArrayList<Structure>());
		companyDepartment.getCompany().setParentStructures(new ArrayList<Structure>());

		// We don't send the list of applications of the company contained into
		// the department submitted
		companyDepartment.getCompany().setApplications(new ArrayList<Application>());
	}

	/**
	 * Filter used on {@link Establishment} objects in order to define the object's
	 * datas to send or not to the distants applications.
	 * 
	 * @param establishment : the {@link Establishment} to filter
	 * @param subscription  : the subscription associated at the filter to use.
	 */
	public void establishmentFilter(Establishment establishment, SynchronizationSubscription subscription) {
		logger.debug("Processing establishment filter...");
		Assert.notNull(establishment, "establishment is mandatory. Null value is forbidden.");

		// COMMONS FILTERS //

		// We don't send company's structures
		establishment.getCompany().setChildStructures(new ArrayList<Structure>());
		establishment.getCompany().setParentStructures(new ArrayList<Structure>());

		// We don't send the list of applications of the company contained into
		// the establishment submitted
		establishment.getCompany().setApplications(new ArrayList<Application>());
	}

}
