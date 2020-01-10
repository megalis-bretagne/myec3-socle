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
package org.myec3.socle.synchro.core.domain.dao.jpa;

import org.myec3.socle.core.domain.dao.jpa.AbstractJpaDao;
import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.synchro.core.domain.dao.GenericSynchronizationDao;

/**
 * This implementation provide methods to perform generic queries on objects
 * used by synchronization. It only provides new specific methods and inherits
 * methods from {@link GenericJpaDao} defined by RESThub.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public abstract class JpaGenericSynchronizationDao<T extends PE> extends AbstractJpaDao<T>
		implements GenericSynchronizationDao<T> {

}
