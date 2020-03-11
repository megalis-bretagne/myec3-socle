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

import org.myec3.socle.core.domain.model.*;

/**
 * Interface defining Business Services methods and providing
 * {@link EmployeeProfile} specific operations. This interface extends the
 * common {@link GenericProfileService} interface by adding new specific methods
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 */
public interface EmployeeProfileService extends
		GenericProfileService<EmployeeProfile> {

	/**
	 * Find all employees attached directly to a given company department
	 * 
	 * @param companyDepartment
	 *            : the company department to filter on
	 * @return the list of employees attached directly to this organism
	 *         department. Returns an empty list if no result found. returns
	 *         null in case of errors
	 * 
	 * @throws IllegalArgumentException
	 *             when companyDepartment is null
	 */
	List<EmployeeProfile> findAllEmployeeProfilesByCompanyDepartment(
			CompanyDepartment companyDepartment);
	
	/**
	 * Find all employees attached directly to a given {@link Establishment}
	 * 
	 * @param establishment
	 *            : the {@link Establishment} to filter on
	 * @return the list of employees attached directly to this {@link Establishment}. 
	 * 				Returns an empty list if no result found. 
	 * 				Returns null in case of errors
	 * 
	 * @throws IllegalArgumentException
	 *             when establishment is null
	 */
	List<EmployeeProfile> findAllEmployeeProfilesByEstablishment(
			Establishment establishment);

	/**
	 * This method allows to populate employee's collections
	 * 
	 * @param employeeProfile
	 *            : companyDepartment object that we want to populate
	 * @throws IllegalArgumentException
	 *             if employeeProfile is null.
	 */
	void populateCollections(EmployeeProfile employeeProfile);

	/**
	 * This method allows to clean employee's collections (before marshalling
	 * object for send the xml into the JMS message)
	 * 
	 * @param employeeProfile
	 *            : employeeProfile to clean
	 * 
	 * @throws IllegalArgumentException
	 *             if employeeProfile is null
	 */
	void cleanCollections(EmployeeProfile employeeProfile);

	/**
	 * Find employee profiles which have administrator rights on GU application
	 * for a company
	 * 
	 * @param companyId
	 *            : company UID
	 * @return List of EmployeeProfiles which have administrator rights on GU
	 *         application. returns null in case of errors
	 * 
	 * @throws IllegalArgumentException
	 *             when companyId is null
	 */
	List<EmployeeProfile> findAllGuAdministratorByCompanyId(Long companyId);

	/**
	 * Find employee profiles enabled which have administrator rights on GU
	 * application for a company
	 * 
	 * @param companyId
	 *            : company UID
	 * @return List of EmployeeProfiles which have administrator rights on GU
	 *         application and which are enabled. returns null in case of errors
	 * 
	 * @throws IllegalArgumentException
	 *             when companyId is null
	 */
	List<EmployeeProfile> findAllGuAdministratorEnabledByCompanyId(
			Long companyId);


	EmployeeProfile findEmployeeProfileByIdSdm(long idSdm);
}
