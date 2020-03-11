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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.domain.dao.OrganismDepartmentDao;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link OrganismDepartment} objects. These methods complete or override parent
 * methods from {@link ResourceServiceImpl}
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti<denis.cucchietti@atosorigin.com>
 */
@Service("organismDepartmentService")
public class OrganismDepartmentServiceImpl extends ResourceServiceImpl<OrganismDepartment, OrganismDepartmentDao>
		implements OrganismDepartmentService {

	private static final Log logger = LogFactory.getLog(OrganismDepartmentServiceImpl.class);

	/**
	 * Business service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OrganismDepartment> findAllDepartmentByOrganism(Organism organism) {
		Assert.notNull(organism, "organism is mandatory. null value is forbidden");
		return this.dao.findAllDepartmentByOrganism(organism);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrganismDepartment findRootOrganismDepartment(Organism organism) {
		Assert.notNull(organism, "organism is mandatory. null value is forbidden");
		return this.dao.findRootOrganismDepartment(organism);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OrganismDepartment> findAllChildrenDepartment(OrganismDepartment department) {
		Assert.notNull(department, "department is mandatory. null value is forbidden");
		return this.dao.findAllChildrenDepartment(department);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void create(OrganismDepartment organismDepartment) {
		Assert.notNull(organismDepartment, "organismDepartment is mandatory. null value is forbidden");
		// FIXME organismDepartment must not have an acronym
		organismDepartment.setAcronym("dep");

		// Set abbreviation to upper case
		if (organismDepartment.getAbbreviation() != null) {
			organismDepartment.setAbbreviation(organismDepartment.getAbbreviation().toUpperCase());
		}

		super.create(organismDepartment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public OrganismDepartment update(OrganismDepartment organismDepartment) {
		Assert.notNull(organismDepartment, "organismDepartment is mandatory. null value is forbidden");

		// Set abbreviation to upper case
		if (organismDepartment.getAbbreviation() != null) {
			organismDepartment.setAbbreviation(organismDepartment.getAbbreviation().toUpperCase());
		}
		return super.update(organismDepartment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void deleteById(Long id) {
		Assert.notNull(id, "id is mandatory. null value is forbidden");

		// Retrieve the organism department corresponding at the given UID
		OrganismDepartment organismDepartment = this.findOne(id);

		// Delete all agents in the department
		List<AgentProfile> agents = organismDepartment.getAgents();
		for (AgentProfile agentProfile : agents) {
			this.agentProfileService.deleteById(agentProfile.getId());
		}

		// delete all the departments in the department to delete
		List<OrganismDepartment> departments = this.findAllChildrenDepartment(organismDepartment);
		for (OrganismDepartment organismDepartmentLoop : departments) {
			this.deleteById(organismDepartmentLoop.getId());
		}

		super.deleteById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateCollections(OrganismDepartment organismDepartment) {
		Assert.notNull(organismDepartment, "organismDepartment is mandatory. null value is forbidden");
		logger.info("populate organismDepartment's collections");
		Organism clonedOrganism = null;
		try {
			clonedOrganism = (Organism) organismDepartment.getOrganism().clone();
			this.organismService.populateCollections(clonedOrganism);
		} catch (CloneNotSupportedException e) {
			logger.error("Cannot clone Organism. " + organismDepartment.getOrganism(), e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanCollections(OrganismDepartment organismDepartment) {
		Assert.notNull(organismDepartment, "organismDepartment is mandatory. null value is forbidden");
		logger.info("cleaning collections of organismDepartment");

		this.organismService.cleanCollections(organismDepartment.getOrganism());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrganismDepartment findFirstLevelOfOrganismDepartment(OrganismDepartment department) {
		Assert.notNull(department, "department is mandatory. null value is forbidden");
		logger.debug("finding first level of organism department " + department.getLabel());

		// Find root department of current organism
		OrganismDepartment rootDepartment = this.dao.findRootOrganismDepartment(department.getOrganism());

		if ((!department.equals(rootDepartment)) && (department.getParentDepartment() != null)
				&& (department.getParentDepartment().getId() != rootDepartment.getId())) {
			OrganismDepartment tmpDepartment = department;
			while (!(department.getParentDepartment().getId() == rootDepartment.getId())) {
				tmpDepartment = tmpDepartment.getParentDepartment();
			}
			return tmpDepartment;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int findCurrentLevelOfDepartment(OrganismDepartment department) {
		Assert.notNull(department, "department is mandatory. null value is forbidden");
		logger.debug("finding current level of organism department " + department.getLabel());

		// Root department level
		int level = 0;

		// Find root department of current organism
		OrganismDepartment rootDepartment = this.dao.findRootOrganismDepartment(department.getOrganism());

		if ((rootDepartment != null) && (department.getId() != rootDepartment.getId())) {
			OrganismDepartment tmpDepartment = department;
			// First level
			level++;
			while (tmpDepartment.getParentDepartment().getId() != rootDepartment.getId()) {
				level++;
				tmpDepartment = tmpDepartment.getParentDepartment();
			}
		}
		logger.debug("Current level of department : " + level);
		return level;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean hasSubDepartments(OrganismDepartment department) {
		Assert.notNull(department, "department is mandatory. null value is forbidden");
		if (null == department.getId()) {
			return Boolean.FALSE;
		} else if (this.dao.findAllChildrenDepartment(department).isEmpty()) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@Override
	public OrganismDepartment findOrganismDepartmentByIdSdm(long idSdm) {
		return this.dao.findOrganismDepartmentByIdSdm(idSdm);
	}
}
