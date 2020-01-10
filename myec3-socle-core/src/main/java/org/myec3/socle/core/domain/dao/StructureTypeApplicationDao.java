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

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.meta.StructureTypeApplication;

/**
 * DAO interface for {@link StructureTypeApplication} objects. This interface
 * defines global methods that could be called on
 * {@link StructureTypeApplication}
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public interface StructureTypeApplicationDao extends
		NoResourceGenericDao<StructureTypeApplication> {

	/**
	 * Retrieve all {@link StructureTypeApplication} from its
	 * {@link Application} instance
	 * 
	 * @param application
	 *            : {@link Application} to search on
	 * @return the list of matching {@link StructureTypeApplication}. returns an
	 *         empty list if there is no result or in case of errors
	 */
	List<StructureTypeApplication> findAllStructureTypeApplicationByApplication(
			Application application);
}