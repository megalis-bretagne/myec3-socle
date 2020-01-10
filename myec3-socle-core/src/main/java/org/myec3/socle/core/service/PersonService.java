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
import org.myec3.socle.core.domain.model.Person;

/**
 * Interface defining Business Services methods and providing {@link Person}
 * specific operations. This interface extends the common
 * {@link ResourceService} interface by adding new specific methods
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface PersonService extends ResourceService<Person> {

	/**
	 * Retrieves all persons attached to a company
	 * 
	 * @param company
	 *            : company to search on
	 * @return the list of all matching persons, an empty list if there is no
	 *         result and null in case of technical error
	 * @throws IllegalArgumentException
	 *             if company is null
	 */
	List<Person> findAllPersonByCompany(Company company);
}
