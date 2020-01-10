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
package org.myec3.socle.core.service;

import java.util.List;

import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;

/**
 * Interface defining Business Services methods and providing
 * {@link CompanyDepartment} specific operations. This interface extends the
 * common {@link ResourceService} interface by adding new specific methods
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface CompanyDepartmentService extends
		ResourceService<CompanyDepartment> {

	/**
	 * Find all {@link CompanyDepartment} of a {@link Company}.
	 * 
	 * @param company
	 *            : company to search on
	 * @return the list of the {@link CompanyDepartment} of this company. return
	 *         an empty list if no result or in case of error.
	 * @throws IllegalArgumentException
	 *             if company is null.
	 */
	List<CompanyDepartment> findAllDepartmentByCompany(Company company);

	/**
	 * Find the root {@link CompanyDepartment} of a {@link Company}.
	 * 
	 * @param company
	 *            : company to search on
	 * @return the root {@link CompanyDepartment} of this company. return null
	 *         if no result or in case of error.
	 * @throws IllegalArgumentException
	 *             if company is null.
	 */
	CompanyDepartment findRootCompanyDepartmentByCompany(Company company);

	/**
	 * This method allows to populate companyDepartment's collections
	 * 
	 * @param companyDepartment
	 *            : companyDepartment object that we want to populate
	 * @throws IllegalArgumentException
	 *             if companyDepartment is null.
	 */
	void populateCollections(CompanyDepartment companyDepartment);

	/**
	 * This method allows to clean companyDepartment's collections
	 * 
	 * @param companyDepartment
	 *            : companyDepartment to clean
	 * 
	 * @throws IllegalArgumentException
	 *             if companyDepartment is null
	 */
	void cleanCollections(CompanyDepartment companyDepartment);

}
