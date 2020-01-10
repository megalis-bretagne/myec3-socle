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

import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.OrganismStatus;

import java.util.List;
import java.util.Set;

/**
 * This interface define methods to perform specific queries on
 * {@link OrganismStatus} objects. It only provides new specific methods and
 * inherits methods from {@link NoResourceGenericDao}.
 */
public interface OrganismStatusDao extends NoResourceGenericDao<OrganismStatus>  {

	/**
	 * Find all {@link OrganismStatus} of a {@link Organism}.
	 *
	 * @param organism
	 *            : organism to search on
	 * @return the list of the {@link OrganismStatus} of this company.
	 *         return an empty list if no result or in case of error.
	 */
	Set<OrganismStatus> findAllOrganismStatusByOrganism(Organism organism);

}