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

import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;

/**
 * Interface defining Business Services methods and providing
 * {@link OrganismDepartment} specific operations. This interface extends the
 * common {@link ResourceService} interface by adding new specific methods
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface OrganismDepartmentService extends
		ResourceService<OrganismDepartment> {

	/**
	 * Find all {@link OrganismDepartment} of a {@link Organism}.
	 * 
	 * @param organism
	 *            : organism to search on
	 * @return the list of the {@link OrganismDepartment} of this company.
	 *         return an empty list if no result or in case of error.
	 * @throws IllegalArgumentException
	 *             if organism is null.
	 */
	List<OrganismDepartment> findAllDepartmentByOrganism(Organism organism);

	/**
	 * Find the root {@link OrganismDepartment} of a {@link Organism}.
	 * 
	 * @param organism
	 *            : organism to search on
	 * @return the root {@link OrganismDepartment} of this organism. return null
	 *         if no result or in case of error.
	 * @throws IllegalArgumentException
	 *             if organism is null.
	 */
	OrganismDepartment findRootOrganismDepartment(Organism organism);

	/**
	 * Find all children department of an {@link OrganismDepartment}
	 * 
	 * @param department
	 *            : department to search on
	 * @return List of children {@link OrganismDepartment}. return an empty list
	 *         in case of errors
	 * @throws IllegalArgumentException
	 *             if department is null
	 */
	List<OrganismDepartment> findAllChildrenDepartment(
			OrganismDepartment department);

	/**
	 * This method allows to populate organismDepartment collections
	 * 
	 * @param organismDepartment
	 *            : organismDepartment object that we want to populate
	 * @throws IllegalArgumentException
	 *             if organismDepartment is null.
	 */
	void populateCollections(OrganismDepartment organismDepartment);

	/**
	 * This method allows to clean organismDepartment's collections (before
	 * marshalling object for send the xml into the JMS message)
	 * 
	 * @param organismDepartment
	 *            : organismDepartment to clean
	 * 
	 * @throws IllegalArgumentException
	 *             if organismDepartment is null
	 */
	void cleanCollections(OrganismDepartment organismDepartment);

	/**
	 * This method allows to find the first level of current organismDepartment
	 * 
	 * @param department
	 *            : the current department
	 * 
	 * @return OrgansimDepartment : return null is department is the
	 *         rootDepartment or the first level.
	 */
	OrganismDepartment findFirstLevelOfOrganismDepartment(
			OrganismDepartment department);

	/**
	 * This method return the level of current organismDepartment
	 * 
	 * @param department
	 *            : the current department
	 * 
	 * @return level of current organism department
	 */
	int findCurrentLevelOfDepartment(OrganismDepartment department);

	/**
	 * This method allows to know if the organismDepartment has sub departments
	 * 
	 * @param department
	 *            : the current department
	 * 
	 * @return TRUE if current department has sub departments else return FALSE
	 */
	Boolean hasSubDepartments(OrganismDepartment department);
}
