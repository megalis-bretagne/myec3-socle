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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.constants.MyEc3Constants;
import org.myec3.socle.core.domain.dao.OrganismDao;
import org.myec3.socle.core.domain.dto.OrganismLightDTO;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.myec3.socle.core.service.*;
import org.myec3.socle.core.service.exceptions.AllAcronymsUsedException;
import org.myec3.socle.core.service.exceptions.OrganismCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Concrete Service implementation providing specific methods to
 * {@link Organism} objects. These methods complete or override parent methods
 * from {@link GenericStructureServiceImpl}
 *
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Service("organismService")
public class OrganismServiceImpl extends GenericStructureServiceImpl<Organism, OrganismDao> implements OrganismService {

	private static final Log logger = LogFactory.getLog(OrganismServiceImpl.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link StructureType} objects
	 */
	@Autowired
	@Qualifier("structureTypeService")
	private StructureTypeService structureTypeService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link OrganismDepartment} objects
	 */
	@Autowired
	@Qualifier("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link OrganismStatus} objects
	 */
	@Autowired
	@Qualifier("organismStatusService")
	private OrganismStatusService organismStatusService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Structure} objects
	 */
	@Autowired
	@Qualifier("structureService")
	private StructureService structureService;

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void create(Organism organism) throws AllAcronymsUsedException, OrganismCreationException {

		Assert.notNull(organism, "organism is mandatory. null value is forbidden");

		// Adding acronym if new organism
/*		AcronymsList freeAcronym;
		if (null == organism.getAcronym()) {
			freeAcronym = this.acronymsListService.findOneAvailableAcronym();
		} else {
			freeAcronym = this.acronymsListService.findByValue(organism.getAcronym());
		}
		if (null == freeAcronym) {
			throw new AllAcronymsUsedException();
		}*/
/*		organism.setAcronym(freeAcronym.getValue());
		freeAcronym.setAvailable(Boolean.FALSE);*/

		organism.setAcronym("AAA");

		// Adding structuretype
		StructureType structureType = this.structureTypeService.findByValue(StructureTypeValue.ORGANISM);
		organism.setStructureType(structureType);

		// We set default applications of the organism
		if ((organism.getApplications() == null) || (organism.getApplications().isEmpty())) {
			List<Application> defaultApplications = this.applicationService
					.findAllDefaultApplicationsByStructureTypeAndCustomer(organism.getStructureType(),
							organism.getCustomer());
			organism.setApplications(defaultApplications);
		}

		try {

			super.create(organism);

			// Create tenantIdentifier
			if (organism.getTenantIdentifier() == null) {
				organism.setTenantIdentifier();
				organism = super.update(organism);
			}

			// We must create the root organism department of the organism
			// We must test if there is no department root already in object
			if (organismDepartmentService.findRootOrganismDepartment(organism) == null) {
				OrganismDepartment organismDepartment = new OrganismDepartment(MyEc3Constants.ROOT_DEPARTMENT_NAME,
						MyEc3Constants.ROOT_DEPARTMENT_LABEL);
				organismDepartment.setExternalId(Long.valueOf(0));
				organismDepartment.setAddress(organism.getAddress());
				organismDepartment.setEmail(organism.getEmail());
				organismDepartment.setOrganism(organism);
				this.organismDepartmentService.create(organismDepartment);
			}

			// If the organism contains parentStructures we must set the
			// organism as an childStruture for each parentStructure
			if (organism.getParentStructures() != null) {
				if (organism.getParentStructures().size() > 0) {
					for (Structure parentStructure : organism.getParentStructures()) {
						parentStructure.addChildStructure(organism);
					}
				}
			}

		} catch (RuntimeException re) {
			throw new OrganismCreationException(re.getMessage(), re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Organism> findAllByCriteria(String label, String siren, String postalCode, String city,
			Customer customer) {
		return this.dao.findAllByCriteria(label, siren, postalCode, city, customer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateCollections(Organism organism) {
		// validate parameters
		Assert.notNull(organism, "Cannot populate collections of the given organism : organism cannot be null");

		logger.info("populate Organism's collections");
		List<Application> listApplication = this.applicationService.findAllApplicationByStructure(organism);
		List<OrganismDepartment> listDepartment = this.organismDepartmentService.findAllDepartmentByOrganism(organism);
		List<Structure> listChildStructures = this.structureService.findAllChildStructuresByStructure(organism);
		List<Structure> listParentStructures = this.structureService.findAllParentStructuresByStructure(organism);
		Set<OrganismStatus> listOrganismStatus = this.organismStatusService.findAllOrganismStatusByOrganism(organism);

		organism.clearOrganismStatus();
		organism.getOrganismStatus().addAll(listOrganismStatus);

		organism.setApplications(listApplication);
		organism.setDepartments(listDepartment);
		organism.setChildStructures(listChildStructures);
		organism.setParentStructures(listParentStructures);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanCollections(Organism organism) {
		// validate parameters
		Assert.notNull(organism, "Cannot clean collections of organism : organism cannot be null");
		logger.info("cleaning collections of organism");

		organism.setApplications(new ArrayList<Application>());
		organism.setDepartments(new ArrayList<OrganismDepartment>());
		organism.setChildStructures(new ArrayList<Structure>());
		organism.setParentStructures(new ArrayList<Structure>());

	}

	@Override
	public Organism findOrganismByIdSdm(long idSdm) {
		return this.dao.findOrganismByIdSdm(idSdm);
	}

	@Override
	public List<OrganismLightDTO> findOrganismLightByApplication(Application application) {
		// validate parameters
		Assert.notNull(application, "Application should not be null");
		return this.dao.findOrganismLightByApplicationId(application.getId());
	}
}
