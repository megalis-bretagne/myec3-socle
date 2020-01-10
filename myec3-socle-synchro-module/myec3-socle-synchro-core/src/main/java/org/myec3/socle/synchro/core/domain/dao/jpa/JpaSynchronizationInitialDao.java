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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.synchro.core.domain.dao.SynchronizationInitialDao;
import org.myec3.socle.synchro.core.domain.model.SynchronizationInitial;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;
import org.springframework.stereotype.Repository;

/**
 * This implementation provide methods to perform specific queries on
 * {@link SynchronizationInitial} objects. It only provides new specific methods
 * and inherits methods from {@link JpaGenericSynchronizationDao}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Repository("synchronizationInitialDao")
public class JpaSynchronizationInitialDao extends JpaGenericSynchronizationDao<SynchronizationInitial>
		implements SynchronizationInitialDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SynchronizationInitial findBySynchronizationLog(SynchronizationLog synchronizationLog) {
		this.getLog().debug("Finding initial synchronization with synchronizationlog : " + synchronizationLog.getId());
		try {
			Query query = this.getEm().createQuery("select s from " + this.getDomainClass().getSimpleName()
					+ " s WHERE synchronizationLog =:synchronizationLog");
			query.setParameter("synchronizationLog", synchronizationLog);
			SynchronizationInitial result = (SynchronizationInitial) query.getSingleResult();
			this.getLog().debug("findBySynchronizationLog successfull.");
			return result;
		} catch (NoResultException e) {
			this.getLog().info("findBySynchronizationLog returned no result");
			return null;
		} catch (RuntimeException re) {
			this.getLog().error("findBySynchronizationLog failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationLog> findAllSynchronizationLogByInitialSynchronizationId(Long initialSynchronizationId) {
		this.getLog()
				.debug("Finding all synchronization log with initial synchronization id : " + initialSynchronizationId);
		try {
			Query query = this.getEm()
					.createQuery("select s.synchronizationLog from " + this.getDomainClass().getSimpleName()
							+ " s WHERE initialSynchronizationId =:initialSynchronizationId");
			query.setParameter("initialSynchronizationId", initialSynchronizationId);
			List<SynchronizationLog> result = query.getResultList();
			this.getLog().debug("findAllSynchronizationLogByInitialSynchronizationId successfull.");
			return result;
		} catch (NoResultException e) {
			this.getLog().warn("findAllSynchronizationLogByInitialSynchronizationId returned no result");
			return new ArrayList<SynchronizationLog>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllSynchronizationLogByInitialSynchronizationId failed.", re);
			throw re;
		}
	}

	@Override
	public Class<SynchronizationInitial> getType() {
		return SynchronizationInitial.class;
	}
}
