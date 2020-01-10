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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.domain.dao.ApplicationDao;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link Application} objects. These methods complete or override parent
 * methods from {@link ResourceServiceImpl}
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Service("applicationService")
public class ApplicationServiceImpl extends ResourceServiceImpl<Application, ApplicationDao>
		implements ApplicationService {

	private static final Log logger = LogFactory.getLog(ApplicationServiceImpl.class);

	/**
	 * Business service providing methods and specifics operations on
	 * {@link Structure} objects. The concrete service implementation is injected by
	 * Spring container
	 */
	@Autowired
	@Qualifier("structureService")
	private StructureService structureService;

	/**
	 * Business service providing methods and specifics operations on {@link Role}
	 * objects. The concrete service implementation is injected by Spring container
	 */
	@Autowired
	@Qualifier("roleService")
	private RoleService roleService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Application> findAllApplicationByStructure(Structure structure) {
		Assert.notNull(structure, "structure is mandatory. null value is forbidden");

		return this.dao.findAllApplicationByStructure(structure);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Application> findAllDefaultApplicationsByStructureType(StructureType structureType) {
		Assert.notNull(structureType, "structureType is mandatory. null value is forbidden");

		return this.dao.findAllDefaultApplicationsByStructureType(structureType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Application> findAllDefaultApplicationsByStructureTypeAndCustomer(StructureType structureType,
			Customer customer) {
		Assert.notNull(structureType, "structureType is mandatory. null value is forbidden");
		Assert.notNull(customer, "customer is mandatory. null value is forbidden");

		return this.dao.findAllDefaultApplicationsByStructureTypeAndCustomer(structureType, customer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Application> findAllApplicationSubscribableByStructureType(StructureType structureType,
			Boolean subscribable) {
		Assert.notNull(structureType, "structureType is mandatory. null value is forbidden");
		Assert.notNull(subscribable, "subscribable is mandatory. null value is forbidden");

		return this.dao.findAllApplicationSubscribableByStructureType(structureType, subscribable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateCollections(Application application) {
		Assert.notNull(application, "application is mandatory. null value is forbidden");

		logger.info("populate Application's collections");
		List<Structure> listStruct = this.structureService.findAllStructureByApplication(application);
		List<Role> listRoles = this.roleService.findAllRoleByApplication(application);

		application.setStructures(listStruct);
		application.setRoles(listRoles);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanCollections(Application application) {
		Assert.notNull(application, "application is mandatory. null value is forbidden");

		logger.info("cleaning collections of application");

		application.setStructures(new ArrayList<Structure>());
		application.setRoles(new ArrayList<Role>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Application> findAllApplicationsAllowingMultipleRolesByStructureTypeValue(
			StructureTypeValue structureTypeValue) {
		return this.dao.findAllApplicationsAllowingMultipleRolesByStructureTypeValue(structureTypeValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Application> findAllApplicationsWithManageableRolesByStructureType(StructureType structureType) {
		return this.dao.findAllApplicationsWithManageableRolesByStructureType(structureType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Application> findAllApplicationSubscribableByStructureTypeAndCustomer(StructureType structureType,
			Customer customer) {
		Assert.notNull(structureType, "structureType is mandatory. null value is forbidden");
		Assert.notNull(customer, "customer is mandatory. null value is forbidden");
		return this.dao.findAllApplicationSubscribableByStructureTypeAndCustomer(structureType, customer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Application> findAllApplicationsByCustomer(Customer customer) {
		Assert.notNull(customer, "customer is mandatory. null value is forbidden");
		return this.dao.findAllApplicationsByCustomer(customer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Application> findAllApplicationByStructureType(StructureType structureType) {
		return this.dao.findAllApplicationByStructureType(structureType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Application> findAllByCriteria(String label, StructureType structureType) {
		return this.dao.findAllByCriteria(label, structureType);
	}

}
