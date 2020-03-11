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

import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;

/**
 * DAO interface for {@link OrganismDepartment} objects. This interface defines
 * global methods that could be called on {@link OrganismDepartment}
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface OrganismDepartmentDao extends ResourceDao<OrganismDepartment> {

	/**
	 * Find all {@link OrganismDepartment} of a {@link Organism}.
	 * 
	 * @param organism
	 *            : organism to search on
	 * @return the list of the {@link OrganismDepartment} of this company.
	 *         return an empty list if no result or in case of error.
	 */
	List<OrganismDepartment> findAllDepartmentByOrganism(Organism organism);

	/**
	 * Find the root {@link OrganismDepartment} of a {@link Organism}.
	 * 
	 * @param organism
	 *            : organism to search on
	 * @return the root {@link OrganismDepartment} of this organism. return null
	 *         if no result or in case of error.
	 */
	OrganismDepartment findRootOrganismDepartment(Organism organism);

	/**
	 * Find all children department of an {@link OrganismDepartment}
	 * 
	 * @param department
	 *            : department to search on
	 * @return List of children {@link OrganismDepartment}. return an empty list
	 *         in case of errors
	 */
	List<OrganismDepartment> findAllChildrenDepartment(
			OrganismDepartment department);

	OrganismDepartment findOrganismDepartmentByIdSdm(long idSdm);

}