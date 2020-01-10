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
import org.myec3.socle.core.domain.dao.CompanyDepartmentDao;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.service.CompanyDepartmentService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link CompanyDepartment} objects. These methods complete or override parent
 * methods from {@link ResourceService} and {@link ResourceServiceImpl}
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Service("companyDepartmentService")
public class CompanyDepartmentServiceImpl extends ResourceServiceImpl<CompanyDepartment, CompanyDepartmentDao>
		implements CompanyDepartmentService {

	private static final Log logger = LogFactory.getLog(CompanyDepartmentServiceImpl.class);

	/**
	 * Business service providing methods and specifics operations on
	 * {@link Company} objects. The concrete service implementation is injected by
	 * Spring container
	 */
	@Autowired
	@Qualifier("companyService")
	private CompanyService companyService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CompanyDepartment> findAllDepartmentByCompany(Company company) {
		Assert.notNull(company, "company is mandatory. null value is forbidden");

		return this.dao.findAllDepartmentByCompany(company);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompanyDepartment findRootCompanyDepartmentByCompany(Company company) {
		Assert.notNull(company, "company is mandatory. null value is forbidden");

		return this.dao.findRootCompanyDepartmentByCompany(company);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void create(CompanyDepartment companyDepartment) {
		Assert.notNull(companyDepartment, "companyDepartment is mandatory. null value is forbidden");

		// FIXME companyDepartment must not have an acronym
		companyDepartment.setAcronym("dep");
		super.create(companyDepartment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateCollections(CompanyDepartment companyDepartment) {
		Assert.notNull(companyDepartment, "companyDepartment is null. null value is forbiden");

		logger.info("populate CompanyDepartment's collections");
		Company clonedCompany = null;

		try {
			clonedCompany = (Company) companyDepartment.getCompany().clone();
			this.companyService.populateCollections(clonedCompany);
		} catch (CloneNotSupportedException e) {
			logger.error("Cannot clone Company. " + companyDepartment.getCompany(), e);
			// we try to perfom operation directly on the object.
			// FIXME : is the clone operation still usefull ?
			this.companyService.populateCollections(clonedCompany);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanCollections(CompanyDepartment companyDepartment) {
		Assert.notNull(companyDepartment, "companyDepartment is null. null value is forbiden");
		logger.info("cleaning collections of companyDepartment");

		this.companyService.cleanCollections(companyDepartment.getCompany());
	}
}
