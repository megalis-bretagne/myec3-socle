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

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.synchro.core.domain.dao.SynchronizationErrorDao;
import org.myec3.socle.synchro.core.domain.model.SynchronizationError;
import org.springframework.stereotype.Repository;

/**
 * This implementation provide methods to perform specific queries on
 * {@link SynchronizationError} objects. It only provides new specific methods
 * and inherits methods from {@link JpaGenericSynchronizationDao}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Repository("synchronizationErrorDao")
public class JpaSynchronizationErrorDao extends JpaGenericSynchronizationDao<SynchronizationError>
		implements SynchronizationErrorDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationError> findAllByResourceId(Long resourceId) {
		this.getLog().debug("Finding all SynchronizationError by resource id: " + resourceId);
		try {
			Query query = this.getEm().createQuery(
					"select s from " + this.getDomainClass().getSimpleName() + " s where s.resourceId = :resourceId");
			query.setParameter("resourceId", resourceId);
			List<SynchronizationError> synchronizationErrorList = query.getResultList();
			this.getLog().debug("findAllByResourceId successfull.");
			return synchronizationErrorList;
		} catch (NoResultException e) {
			this.getLog().info("findAllByResourceId returned no result");
			return null;
		} catch (RuntimeException re) {
			this.getLog().error("findAllByResourceId failed.", re);
			throw re;
		}
	}

	@Override
	public Class<SynchronizationError> getType() {
		return SynchronizationError.class;
	}
}
