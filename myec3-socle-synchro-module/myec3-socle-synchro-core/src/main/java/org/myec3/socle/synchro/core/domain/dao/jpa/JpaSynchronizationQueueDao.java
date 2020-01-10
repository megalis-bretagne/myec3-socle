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

import org.myec3.socle.synchro.core.domain.dao.SynchronizationQueueDao;
import org.myec3.socle.synchro.core.domain.model.SynchronizationQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * This implementation provide methods to perform specific queries on
 * {@link SynchronizationQueue} objects. It only provides new specific methods
 * and inherits methods from {@link JpaGenericSynchronizationDao}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Repository("synchronizationQueueDao")
public class JpaSynchronizationQueueDao extends JpaGenericSynchronizationDao<SynchronizationQueue>
		implements SynchronizationQueueDao {

	private final Logger log = LoggerFactory.getLogger(JpaSynchronizationQueueDao.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationQueue> findAll(int limit) {
		log.debug("Finding all synchronization queue with limit = " + limit);

		try {
			Query query = this.getEm()
					.createQuery("SELECT s from " + this.getDomainClass().getSimpleName() + " s ORDER BY id ASC");

			query.setMaxResults(limit);
			List<SynchronizationQueue> resultList = query.getResultList();
			log.debug("findAll successfull.");
			return resultList;
		} catch (NoResultException e) {
			// No result found, we return empty list instead of errors
			log.warn("findAll returned no result.");
			return new ArrayList<SynchronizationQueue>();
		} catch (RuntimeException re) {
			log.error("findAll failed.", re);
			throw re;
		}
	}

	@Override
	public Class<SynchronizationQueue> getType() {
		return SynchronizationQueue.class;
	}
}
