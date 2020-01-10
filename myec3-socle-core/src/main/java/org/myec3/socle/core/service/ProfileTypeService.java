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

import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;

/**
 * Interface defining Business Services methods and providing
 * {@link ProfileType} specific operations. This interface extends the common
 * {@link ResourceService} interface by adding new specific methods
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public interface ProfileTypeService extends IGenericService<ProfileType> {

	/**
	 * Find ProfileType by its value. @see {@link ProfileTypeValue}
	 * 
	 * @param profileTypeValue : profile type value to search on
	 * @return the {@link ProfileType} object with this value. return null in case
	 *         of error, if there is several {@link ProfileType} with this value or
	 *         if there is no result.
	 * @throws IllegalArgumentException if value is null.
	 */
	ProfileType findByValue(ProfileTypeValue profileTypeValue);

}
