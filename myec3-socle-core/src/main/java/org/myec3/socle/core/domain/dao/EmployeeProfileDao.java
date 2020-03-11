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

import org.myec3.socle.core.domain.model.*;

/**
 * This interface define methods to perform specific queries on
 * {@link EmployeeProfile} objects. It only defines new specific methods and
 * inherits methods from {@link GenericProfileDao}.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface EmployeeProfileDao extends GenericProfileDao<EmployeeProfile> {

	/**
	 * Find all {@link EmployeeProfile} attached directly to a given
	 * {@link CompanyDepartment}
	 * 
	 * @param companyDepartment
	 *            : the company department to filter on
	 * @return the list of employees attached directly to this organism
	 *         department. Return an empty list if no result found or null in
	 *         case of error.
	 */
	List<EmployeeProfile> findAllEmployeeByCompanyDepartment(
			CompanyDepartment companyDepartment);
	
	/**
	 * Find all {@link EmployeeProfile} attached directly to a given
	 * {@link Establishment}
	 * 
	 * @param establishment
	 *            : the establishment to filter on
	 * @return the list of employees attached directly to this establishment.
	 * 			Return an empty list if no result found or null in case of error.
	 */
	List<EmployeeProfile> findAllByEstablishment(Establishment establishment);

	/**
	 * Find {@link EmployeeProfile} which have administrator rights on GU
	 * application
	 * 
	 * @param companyId
	 *            : company UID
	 * @return List of EmployeeProfiles which have administrator rights on GU
	 *         application. Return an empty list if no result found or null in
	 *         case of error.
	 * 
	 * @throws IllegalArgumentException
	 *             when organismDepartment is null
	 */
	List<EmployeeProfile> findAllGuAdministratorByCompanyId(Long companyId);

	/**
	 * Find {@link EmployeeProfile} enabled which have administrator rights on
	 * GU application for a {@link Company}
	 * 
	 * @param companyId
	 *            : company UID
	 * @return List of EmployeeProfiles which have administrator rights on GU
	 *         application and which are enabled. Return an empty list if no
	 *         result found or null in case of error.
	 * 
	 * @throws IllegalArgumentException
	 *             when companyId is null
	 */
	List<EmployeeProfile> findAllGuAdministratorEnabledByCompanyId(
			Long companyId);

	EmployeeProfile findEmployeeProfileByIdSdm(long idSdm);
}