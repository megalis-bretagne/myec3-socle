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
package org.myec3.socle.core.domain.dao;

import java.util.List;

import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;

/**
 * DAO interface for {@link CompanyDepartment} objects. This interface defines
 * global methods that could be called on {@link CompanyDepartment}
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface CompanyDepartmentDao extends ResourceDao<CompanyDepartment> {

	/**
	 * Find all {@link CompanyDepartment} of a {@link Company}.
	 * 
	 * @param company
	 *            : company to search on
	 * @return the list of the {@link CompanyDepartment} of this company. Return
	 *         an empty list if no result or in case of error.
	 */
	List<CompanyDepartment> findAllDepartmentByCompany(Company company);

	/**
	 * Find the root {@link CompanyDepartment} of a {@link Company}.
	 * 
	 * @param company
	 *            : company to search on
	 * @return the root {@link CompanyDepartment} of this company. Return null
	 *         if no result or in case of error.
	 */
	CompanyDepartment findRootCompanyDepartmentByCompany(Company company);
}
