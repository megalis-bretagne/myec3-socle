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
import org.myec3.socle.core.constants.MyEc3MpsUpdateConstants;
import org.myec3.socle.core.domain.dao.CompanyDao;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.MpsUpdateTypeValue;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.myec3.socle.core.service.*;
import org.myec3.socle.core.service.exceptions.CompanyCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Concrete Service implementation providing specific methods to {@link Company}
 * objects. These methods complete or override parent methods from
 * {@link GenericStructureServiceImpl}
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Matthieu Dubromez <matthieu.dubromez@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 */
@Service("companyService")
public class CompanyServiceImpl extends GenericStructureServiceImpl<Company, CompanyDao> implements CompanyService {

	private static final Log logger = LogFactory.getLog(CompanyServiceImpl.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Structure} objects. The concrete service implementation is injected by
	 * Spring container
	 */
	@Autowired
	@Qualifier("structureService")
	private StructureService structureService;

	/**
	 * Business Service providing methods and specifics operations on {@link Person}
	 * objects. The concrete service implementation is injected by Spring container
	 */
	@Autowired
	@Qualifier("personService")
	private PersonService personService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects. The concrete service implementation is injected
	 * by Spring container
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link StructureType} objects. The concrete service implementation is
	 * injected by Spring container
	 */
	@Autowired
	@Qualifier("structureTypeService")
	private StructureTypeService structureTypeService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link CompanyDepartment} objects. The concrete service implementation is
	 * injected by Spring container
	 */
	@Autowired
	@Qualifier("companyDepartmentService")
	private CompanyDepartmentService companyDepartmentService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Establishment} objects. The concrete service implementation is
	 * injected by Spring container
	 */
	@Autowired
	@Qualifier("establishmentService")
	private EstablishmentService establishmentService;

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void create(Company company) throws CompanyCreationException {
		Assert.notNull(company, "company is mandatory. null value is forbidden");

		// Adding structuretype
		StructureType structureType = this.structureTypeService.findByValue(StructureTypeValue.COMPANY);
		company.setStructureType(structureType);

		try {
			super.create(company);

			// We must create the root company department of the company
			// We must test if there is no department root already in object
			if (companyDepartmentService.findRootCompanyDepartmentByCompany(company) == null) {
				CompanyDepartment companyDepartment = new CompanyDepartment(MyEc3Constants.ROOT_DEPARTMENT_NAME,
						MyEc3Constants.ROOT_DEPARTMENT_LABEL);
				companyDepartment.setExternalId(Long.valueOf(0));
				companyDepartment.setAddress(company.getAddress());
				companyDepartment.setEmail(company.getEmail());
				companyDepartment.setNic(company.getNic());
				companyDepartment.setAcronym(company.getAcronym());
				companyDepartment.setCompany(company);
				this.companyDepartmentService.create(companyDepartment);
			}

			// create alfTenantId
			if (company.getTenantIdentifier() == null) {
				company.setTenantIdentifier();
				company = super.update(company);
			}

		} catch (RuntimeException re) {
			throw new CompanyCreationException(re.getMessage(), re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void  populateCollections(Company company) {
		Assert.notNull(company, "company is mandatory. null value is forbidden");

		logger.debug("populate collections of company");

		List<Person> listPerson = this.personService.findAllPersonByCompany(company);
		List<Application> listApplication = this.applicationService.findAllApplicationByStructure(company);
		List<CompanyDepartment> listDepartment = this.companyDepartmentService.findAllDepartmentByCompany(company);
		List<Establishment> listEstablishment = this.establishmentService.findAllEstablishmentsByCompany(company);
		List<Structure> listChildStructures = this.structureService.findAllChildStructuresByStructure(company);
		List<Structure> listParentStructures = this.structureService.findAllParentStructuresByStructure(company);

		company.setResponsibles(listPerson);
		company.setApplications(listApplication);
		company.setDepartments(listDepartment);
		company.setEstablishments(listEstablishment);
		company.setChildStructures(listChildStructures);
		company.setParentStructures(listParentStructures);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanCollections(Company company) {
		Assert.notNull(company, "company is mandatory. null value is forbidden");

		logger.info("cleaning collections of company");

		company.getResponsibles().clear();
		company.getApplications().clear();
		company.getDepartments().clear();
		company.getChildStructures().clear();
		company.getParentStructures().clear();
	}

	public List<MpsUpdateJob> getCompanyToUpdate() {

		// object to return
		List<MpsUpdateJob> updateQueueToReturn = new ArrayList<MpsUpdateJob>();

		List<Long> companyListToUpdate = new ArrayList<Long>();

		// get all the companies that need update
		companyListToUpdate = this.dao.getCompanyToUpdate();

		if (!(companyListToUpdate.isEmpty())) {
			logger.info("Numbers of MpsUpdateJob to populate : " + companyListToUpdate.size());
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, Integer.parseInt(MyEc3MpsUpdateConstants.getMonth()));
			SimpleDateFormat formatForBdd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			formatForBdd.setCalendar(cal);

			for (Long findCompanyEstablishment : companyListToUpdate) {
				logger.debug("Company : " + findCompanyEstablishment.toString());
				MpsUpdateJob tmpUpdateQueue = new MpsUpdateJob();
				tmpUpdateQueue.setId(findCompanyEstablishment);
				tmpUpdateQueue.setType(ResourceType.COMPANY.getLabel());
				tmpUpdateQueue.setPriority(MpsUpdateTypeValue.AUTOMATIC.getLabel());
				logger.info("getCompanyToUpdate Create (but not persist) MpsUpdateJob : " + tmpUpdateQueue.toString());
				updateQueueToReturn.add(tmpUpdateQueue);
				Company searchCompany = this.findOne(findCompanyEstablishment);
				//this.populateCollections(searchCompany);
				updateQueueToReturn.addAll(this.establishmentService.getEstablishmentToUpdateByCompany(searchCompany,
						MpsUpdateTypeValue.AUTOMATIC));

			}
		}
		logger.debug("getCompanyToUpdate Success !");
		return updateQueueToReturn;
	}

	public void findAllPersonByCompany(Company company) {

		Assert.notNull(company, "company is mandatory. null value is forbidden");

		logger.info("populate Person collections of company");

		List<Person> listPerson = this.personService.findAllPersonByCompany(company);

		company.setResponsibles(listPerson);
	}

	public List<Company> findAllByCriteria(String label, String acronym, String siren, String postalCode, String city) {
		return this.dao.findAllByCriteria(label, acronym, siren, postalCode, city);
	}

	@Override
	public Company findCompanyByIdSdm(long idSdm) {
		return this.dao.findCompanyByIdSdm(idSdm);
	}
}
