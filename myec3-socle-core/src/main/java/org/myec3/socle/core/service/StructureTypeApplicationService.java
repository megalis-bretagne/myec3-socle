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
import org.myec3.socle.core.domain.model.meta.StructureTypeApplication;

/**
 * Interface defining Business Services methods and providing
 * {@link StructureTypeApplication} specific operations. This interface extends
 * the common {@link ResourceService} interface by adding new specific methods
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public interface StructureTypeApplicationService extends IGenericService<StructureTypeApplication> {

	/**
	 * Retrieve all {@link StructureTypeApplication} from its {@link Application}
	 * instance
	 * 
	 * @param application : {@link Application} to search on
	 * @return the list of matching {@link StructureTypeApplication}. returns an
	 *         empty list if there is no result or in case of errors
	 * @throws IllegalArgumentException if application is null
	 */
	List<StructureTypeApplication> findAllStructureTypeApplicationByApplication(Application application);
}
