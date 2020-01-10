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

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;

/**
 * Interface defining Business Services methods and providing
 * {@link Application} specific operations. This interface extends the common
 * {@link ResourceService} interface by adding new specific methods
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface ApplicationService extends ResourceService<Application> {

	/**
	 * Retrieve all applications associated to the given structure. It is
	 * necassary to provide a concrete implementation of {@link Structure}
	 * 
	 * @param structure
	 *            : concrete implementation of {@link Structure} to search on
	 * 
	 * @return the list of all matching application or an empty list if there is
	 *         no result
	 * 
	 * @throws IllegalArgumentException
	 *             if structure is null
	 */
	List<Application> findAllApplicationByStructure(Structure structure);

	/**
	 * Retrieve all applications associated to a given {@link StructureType}
	 * that are also subscribable or not by end users, depending on the value of
	 * subscribable parameter
	 * 
	 * @param structureType
	 *            : {@link StructureType} to search on
	 * 
	 * @param subscribable
	 *            : {@link Boolean#TRUE} if we want to filter on subscribable
	 *            applications for this {@link StructureType}.
	 *            {@link Boolean#FALSE} if we want to filter on non subscribable
	 *            applications.
	 * 
	 * @return the list of all matching application or an empty list if there is
	 *         no result
	 * 
	 * @throws IllegalArgumentException
	 *             if structureType is null
	 * 
	 * @throws IllegalArgumentException
	 *             if subscribable is null
	 */
	List<Application> findAllApplicationSubscribableByStructureType(
			StructureType structureType, Boolean subscribable);

	/**
	 * Retrieve all default applications associated to a given
	 * {@link StructureType} that are not subscribable by end users.
	 * 
	 * @param structureType
	 *            : {@link StructureType} to search on
	 * 
	 * @return the list of all matching application or an empty list if there is
	 *         no result
	 * 
	 * @throws RuntimeException
	 *             in case of errors.
	 */
	List<Application> findAllDefaultApplicationsByStructureType(
			StructureType structureType);

	/**
	 * Retrieve all default applications associated to a given
	 * {@link StructureType} that are not subscribable by end users.
	 * 
	 * @param structureType
	 *            : {@link StructureType} to search on
	 * @param customer
	 *            : {@link Customer} to search on
	 * 
	 * @return the list of all matching application or an empty list if there is
	 *         no result
	 * 
	 * @throws RuntimeException
	 *             in case of errors.
	 */
	List<Application> findAllDefaultApplicationsByStructureTypeAndCustomer(
			StructureType structureType, Customer customer);

	/**
	 * This method allows to populate application's collections
	 * 
	 * @param application
	 *            : application to populate
	 * 
	 * @throws IllegalArgumentException
	 *             if application is null
	 */
	void populateCollections(Application application);

	/**
	 * This method allows to clean application's collections
	 * 
	 * @param application
	 *            : application to clean
	 * 
	 * @throws IllegalArgumentException
	 *             if application is null
	 */
	void cleanCollections(Application application);

	/**
	 * Retrieve all applications associated to a given {@link StructureType}
	 * that allow end users to have more than one role for the given
	 * application.
	 * 
	 * @param structureTypeValue
	 *            : {@link StructureType} to search on
	 * 
	 * @return the list of all matching application or an empty list if there is
	 *         no result
	 * 
	 * @throws RuntimeException
	 *             in case of errors.
	 */
	List<Application> findAllApplicationsAllowingMultipleRolesByStructureTypeValue(
			StructureTypeValue structureTypeValue);

	/**
	 * Retrieve all applications associated to a given {@link StructureType}
	 * that allow end users to manage roles for the given application.
	 * 
	 * @param structureTypeValue
	 *            : {@link StructureType} to search on
	 * 
	 * @return the list of all matching application or an empty list if there is
	 *         no result
	 * 
	 * @throws RuntimeException
	 *             in case of errors.
	 */
	List<Application> findAllApplicationsWithManageableRolesByStructureType(
			StructureType structureType);

	/**
	 * Retrieve all applications associated to given {@link StructureType} and
	 * {@link Customer} that allow end users to manage roles for the given
	 * application.
	 * 
	 * @param structureType
	 *            : {@link StructureType} to search on
	 * @param customer
	 *            : {@link Customer} to search on
	 * 
	 * @return the list of all matching application or an empty list if there is
	 *         no result
	 * 
	 * @throws RuntimeException
	 *             in case of errors.
	 */
	List<Application> findAllApplicationSubscribableByStructureTypeAndCustomer(
			StructureType structureType, Customer customer);

	/**
	 * Retrieve all applications associated to given {@link Customer}
	 * 
	 * @param customer
	 *            : the {@link Customer} to search on
	 * @return the list of all matching applications or an empty list if there
	 *         is no result
	 */
	List<Application> findAllApplicationsByCustomer(Customer customer);
	
	
	/**
	 * Retrieve all applications associated to given {@link StructureType}
	 * @param structureType
	 *            : {@link StructureType} to search on
	 * 
	 * @return the list of all matching Applications. Return an empty list if
	 *         there's no result. Return null in case of errors.
	 */
	
	List<Application> findAllApplicationByStructureType(StructureType structureType);
	
	/**
	 * Retrieve all structures depending on the filters defined in parameters.
	 * If all parameters are null, retrieve all Applications.
	 * 
	 * @param label
	 *            : Application label to find. If this parameter is not null,
	 *            filters on all structures with the same label. Case no
	 *            matters.
	 * @param customer
	 *            : Application associated structureType to find. Case no matters
	 * @return the list of all matching Application. Return an empty list if
	 *         there's no result. Return null in case of errors.
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<Application> findAllByCriteria(String label, StructureType structureType);
}
