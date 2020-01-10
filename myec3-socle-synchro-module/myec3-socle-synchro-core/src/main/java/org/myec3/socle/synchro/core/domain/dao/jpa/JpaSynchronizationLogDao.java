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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.dao.SynchronizationLogDao;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;
import org.springframework.stereotype.Repository;

/**
 * This implementation provide methods to perform specific queries on
 * {@link SynchronizationLog} objects. It only provides new specific methods and
 * inherits methods from {@link JpaGenericSynchronizationDao}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Repository("synchronizationLogDao")
public class JpaSynchronizationLogDao extends JpaGenericSynchronizationDao<SynchronizationLog>
		implements SynchronizationLogDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationLog> findAllSynchronizationLogByResourceId(Long resourceId) {
		this.getLog().debug("Finding all SynchronizationLog by resource id: " + resourceId);
		try {
			Query query = this.getEm().createQuery(
					"select s from " + this.getDomainClass().getSimpleName() + " s where s.resourceId = :resourceId");
			query.setParameter("resourceId", resourceId);
			List<SynchronizationLog> synchronizationLogList = query.getResultList();
			this.getLog().debug("findAllSynchronizationLogByResourceId successfull.");
			return synchronizationLogList;
		} catch (NoResultException e) {
			this.getLog().info("findAllSynchronizationLogByResourceId returned no result");
			return new ArrayList<SynchronizationLog>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllSynchronizationLogByResourceId failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationLog> findAllSynchronizationLogBySynchronizationLogId(Long synchronizationLogId) {
		this.getLog().debug("Finding all synchronization log with synchronizationlog id : " + synchronizationLogId);
		try {
			Query query = this.getEm()
					.createQuery("select s.synchronizationLog from " + this.getDomainClass().getSimpleName()
							+ " s WHERE initialSynchronizationId =:synchronizationLogId");
			query.setParameter("synchronizationLogId", synchronizationLogId);
			List<SynchronizationLog> result = query.getResultList();
			this.getLog().debug("findAllSynchronizationLogBySynchronizationLogId successfull.");
			return result;
		} catch (NoResultException e) {
			this.getLog().warn("findAllSynchronizationLogBySynchronizationLogId returned no result");
			return new ArrayList<SynchronizationLog>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllSynchronizationLogBySynchronizationLogId failed.", re);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationLog> findAllSynchronizationLogByCriteria(Date startDate, Date endDate,
			Application application, String resourceType, String httpStatus, String synchronizationType,
			String methodType, String statut, Boolean isFinal) {

		this.getLog().debug("Finding all SynchronizationLog by applicationName, httpStatus, methodType, statut");
		try {

			Boolean hasCriteria = Boolean.FALSE;
			StringBuffer queryString = new StringBuffer(
					"select s from " + this.getDomainClass().getSimpleName() + " s");
			if ((null != startDate) || (null != endDate) || (null != application) || (null != resourceType)
					|| (null != httpStatus) || (null != synchronizationType) || (null != methodType) || (null != statut)
					|| (null != isFinal)) {
				queryString.append(" where ");

				if ((null != startDate) && (null == endDate)) {
					queryString.append("s.synchronizationDate > :startDate");
					hasCriteria = Boolean.TRUE;
				}
				if ((null != endDate) && (null == startDate)) {
					queryString.append("s.synchronizationDate < :endDate");
					hasCriteria = Boolean.TRUE;
				}
				if ((null != startDate) && (null != endDate)) {
					queryString.append("s.synchronizationDate between :startDate and :endDate");
					hasCriteria = Boolean.TRUE;
				}
				if (null != application) {
					queryString.append("s.applicationName like :applicationName");
					hasCriteria = Boolean.TRUE;
				}
				if (null != resourceType) {
					if (hasCriteria)
						queryString.append(" and ");
					else
						hasCriteria = Boolean.TRUE;
					queryString.append("s.resourceType =:resourceType");
				}
				if (null != httpStatus) {
					if (hasCriteria)
						queryString.append(" and ");
					else
						hasCriteria = Boolean.TRUE;
					queryString.append("s.httpStatus =:httpStatus");
				}
				if (null != synchronizationType) {
					if (hasCriteria)
						queryString.append(" and ");
					else
						hasCriteria = Boolean.TRUE;
					queryString.append("s.synchronizationType =:synchronizationType");
				}
				if (null != methodType) {
					if (hasCriteria)
						queryString.append(" and ");
					else
						hasCriteria = Boolean.TRUE;
					queryString.append("s.methodType =:methodType");
				}
				if (null != statut) {
					if (hasCriteria)
						queryString.append(" and ");
					else
						hasCriteria = Boolean.TRUE;
					queryString.append("s.statut like :statut");
				}
				if (null != isFinal) {
					if (hasCriteria)
						queryString.append(" and ");
					else
						hasCriteria = Boolean.TRUE;
					queryString.append("s.isFinal =:isFinal");
				}
			}

			Query query = this.getEm().createQuery(queryString.toString());
			if ((null != startDate) && (null == endDate)) {
				query.setParameter("startDate", startDate, TemporalType.DATE);
			}
			if ((null == startDate) && (null != endDate)) {
				query.setParameter("endDate", endDate, TemporalType.DATE);
			}
			if ((null != startDate) && (null != endDate)) {
				if (startDate.equals(endDate)) {
					Calendar dateCal = Calendar.getInstance();
					dateCal.setTime(endDate);
					dateCal.set(Calendar.HOUR_OF_DAY, 23);
					dateCal.set(Calendar.MINUTE, 59);
					dateCal.set(Calendar.SECOND, 59);
					dateCal.set(Calendar.MILLISECOND, 000);

					endDate.setTime(dateCal.getTimeInMillis());
				}
				query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
				query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
			}
			if (null != application)
				query.setParameter("applicationName", "%" + application.getName() + "%");
			if (null != resourceType)
				query.setParameter("resourceType", ResourceType.valueOf(resourceType));
			if (null != httpStatus)
				query.setParameter("httpStatus", HttpStatus.valueOf(httpStatus));
			if (null != synchronizationType)
				query.setParameter("synchronizationType", SynchronizationType.valueOf(synchronizationType));
			if (null != methodType)
				query.setParameter("methodType", MethodType.valueOf(methodType));
			if (null != statut)
				query.setParameter("statut", statut);
			if (null != isFinal)
				query.setParameter("isFinal", isFinal);

			List<SynchronizationLog> synchronizationLogList = query.getResultList();
			this.getLog().debug("findAllSynchronizationLogByCriteria successfull.");
			return synchronizationLogList;
		} catch (NoResultException e) {
			this.getLog().info("findAllSynchronizationLogByCriteria returned no result");
			return new ArrayList<SynchronizationLog>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllSynchronizationLogByCriteria failed.", re);
			throw re;
		}
	}

	@Override
	public Class<SynchronizationLog> getType() {
		return SynchronizationLog.class;
	}
}
