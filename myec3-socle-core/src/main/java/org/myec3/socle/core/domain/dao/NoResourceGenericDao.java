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

import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.domain.model.Resource;

/**
 * Generic DAO interface for non-{@link Resource} objects. This interface
 * defines global methods that could be called on any class that don't inherit
 * from {@link Resource}
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 * @param <T> Concrete Object class
 * 
 */
public interface NoResourceGenericDao<T extends PE> extends IGenericDao<T> {

}
