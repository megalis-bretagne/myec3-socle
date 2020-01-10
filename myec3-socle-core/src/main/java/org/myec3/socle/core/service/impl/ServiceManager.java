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
package org.myec3.socle.core.service.impl;

import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.AdminProfileService;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.CompanyDepartmentService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.CustomerService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("serviceManager")
public class ServiceManager implements org.myec3.socle.core.service.ServiceManager {

	@Autowired
	@Qualifier("adminProfileService")
	private AdminProfileService adminProfileService;

	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	@Autowired
	@Qualifier("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	@Autowired
	@Qualifier("customerService")
	private CustomerService customerService;

	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;

	@Autowired
	@Qualifier("companyService")
	private CompanyService companyService;

	@Autowired
	@Qualifier("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@Autowired
	@Qualifier("companyDepartmentService")
	private CompanyDepartmentService companyDepartmentService;

	@Autowired
	@Qualifier("establishmentService")
	private EstablishmentService establishmentService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResourceService<?> getResourceServiceByResourceType(ResourceType resourceType) {
		Assert.notNull(resourceType, "resourceType is mandatory and canot be null");

		switch (resourceType) {
		case ADMIN_PROFILE:
			return this.adminProfileService;
		case AGENT_PROFILE:
			return this.agentProfileService;
		case EMPLOYEE_PROFILE:
			return this.employeeProfileService;
		case CUSTOMER:
			return this.customerService;
		case ORGANISM:
			return this.organismService;
		case ORGANISM_DEPARTMENT:
			return this.organismDepartmentService;
		case COMPANY:
			return this.companyService;
		case COMPANY_DEPARTMENT:
			return this.companyDepartmentService;
		case ESTABLISHMENT:
			return this.establishmentService;
		}
		return null;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResourceService<?> getResourceServiceByClassName(Resource resource) {
		Assert.notNull(resource, "resource is mandatory and canot be null");

		if (AdminProfile.class.equals(resource.getClass())) {
			return this.adminProfileService;
		}
		if (AgentProfile.class.equals(resource.getClass())) {
			return this.agentProfileService;
		}
		if (EmployeeProfile.class.equals(resource.getClass())) {
			return this.employeeProfileService;
		}
		if (Customer.class.equals(resource.getClass())) {
			return this.customerService;
		}
		if (Organism.class.equals(resource.getClass())) {
			return this.organismService;
		}
		if (OrganismDepartment.class.equals(resource.getClass())) {
			return this.organismDepartmentService;
		}
		if (Company.class.equals(resource.getClass())) {
			return this.companyService;
		}
		if (CompanyDepartment.class.equals(resource.getClass())) {
			return this.companyDepartmentService;
		}
		if (Establishment.class.equals(resource.getClass())) {
			return this.establishmentService;
		}
		return null;
	}
}
