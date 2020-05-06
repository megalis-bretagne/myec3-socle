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

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.core.domain.dao.SynchronizationSubscriptionDao;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.springframework.stereotype.Repository;

/**
 * This implementation provide methods to perform specific queries on
 * {@link SynchronizationSubscription} objects. It only provides new specific
 * methods and inherits methods from {@link JpaGenericSynchronizationDao}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Repository("synchronizationSubscriptionDao")
public class JpaSynchronizationSubscriptionDao extends JpaGenericSynchronizationDao<SynchronizationSubscription>
		implements SynchronizationSubscriptionDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationSubscription> findAllByApplicationId(Long id) {
		this.getLog().debug("Finding all Subscription with application id: " + id);
		try {
			Query query = getEm().createQuery(
					"select s from " + this.getDomainClass().getSimpleName() + " s WHERE application_id = :id");
			query.setParameter("id", id);
			List<SynchronizationSubscription> synchronizationSubscriptionList = query.getResultList();
			this.getLog().debug("findAllByApplicationId successfull.");
			return synchronizationSubscriptionList;
		} catch (NoResultException e) {
			this.getLog().warn("findAllByApplicationId returned no result");
			return new ArrayList<SynchronizationSubscription>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllByApplicationId failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationSubscription> findAllByResourceLabel(ResourceType resourceLabel) {
		this.getLog().debug("Finding all Subscription with ResourceLabel : " + resourceLabel);
		try {
			Query query = getEm().createQuery("select s from " + this.getDomainClass().getSimpleName()
					+ " s WHERE resourceLabel = :resourceType");
			query.setParameter("resourceType", resourceLabel);
			List<SynchronizationSubscription> synchronizationSubscriptionList = query.getResultList();
			this.getLog().debug("findAllByResourceLabel successfull.");
			return synchronizationSubscriptionList;
		} catch (NoResultException e) {
			this.getLog().warn("findAllByResourceLabel returned no result");
			return new ArrayList<SynchronizationSubscription>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllByResourceLabel failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SynchronizationSubscription> findByResourceTypeAndApplicationId(ResourceType resourceType,
			Long applicationId) {
		this.getLog().debug("Finding instance of SynchronizationSubscription with ResourceType : " + resourceType
				+ " and Application Id : " + applicationId);
		try {
			Query query = getEm().createQuery("select s from " + this.getDomainClass().getSimpleName()
					+ " s WHERE resourceLabel = :resourceType AND application_id = :applicationId");
			query.setParameter("resourceType", resourceType);
			query.setParameter("applicationId", applicationId);
			List<SynchronizationSubscription> instance = (List<SynchronizationSubscription>) query.getResultList();
			this.getLog().debug("findByResourceTypeAndApplicationId successfull.");
			return instance;
		} catch (NoResultException e) {
			this.getLog().warn("findByResourceTypeAndApplicationId returned no result");
			return null;
		} catch (RuntimeException re) {
			this.getLog().error("findByResourceTypeAndApplicationId failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Application> findAllApplicationsSubscribe() {
		this.getLog().debug("Finding all application witch are synchronized: ");
		try {
			Query query = getEm().createQuery("select distinct a from " + this.getDomainClass().getSimpleName()
					+ " s inner join s.application a where s.application.id = a.id");

			List<Application> result = query.getResultList();
			this.getLog().debug("findAllApplicationsSubscribe successfull.");
			return result;

		} catch (NoResultException re) {
			this.getLog().warn("findByResourceTypeAndApplicationId returned no result.");
			return new ArrayList<Application>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllApplicationsSubscribe failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SynchronizationSubscription> findAllByCriteria(ResourceType resourceType, Application application) {
		this.getLog().debug("Finding all SynchronizationSubscription by ResourceType and application");
		try {
			Boolean hasCriteria = Boolean.FALSE;
			StringBuffer queryString = new StringBuffer(
					"select s from " + this.getDomainClass().getSimpleName() + " s");
			if ((null != resourceType) || (null != application)) {
				queryString.append(" where ");

				if (null != resourceType) {
					queryString.append("s.resourceLabel =:resourceType");
					hasCriteria = Boolean.TRUE;
				}
				if (null != application) {
					if (hasCriteria)
						queryString.append(" and ");
					else
						hasCriteria = Boolean.TRUE;
					queryString.append("s.application.id =:applicationId");
				}
			}
			Query query = getEm().createQuery(queryString.toString());
			if (null != resourceType)
				query.setParameter("resourceType", resourceType);
			if (null != application)
				query.setParameter("applicationId", application.getId());
			List<SynchronizationSubscription> synchronizationSubscriptionList = query.getResultList();
			this.getLog().debug("findAllSynchronizationSubscriptionByCriteria successfull.");
			return synchronizationSubscriptionList;
		} catch (NoResultException e) {
			this.getLog().info("findAllSynchronizationSubscriptionByCriteria returned no result");
			return new ArrayList<SynchronizationSubscription>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllSynchronizationSubscriptionByCriteria failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SynchronizationSubscription> findAllByResourceLabelAndCustomer(ResourceType resourceType,
			Customer customer) {
		try {
			Query query = getEm().createQuery("SELECT s FROM " + this.getDomainClass().getSimpleName()
					+ " s INNER JOIN s.customer c where s.resourceLabel = :resourceType AND c = :customer");

			query.setParameter("resourceType", resourceType);
			query.setParameter("customer", customer);

			List<SynchronizationSubscription> result = query.getResultList();
			this.getLog().debug("findAllByResourceLabelAndCustomer successfull.");
			return result;
		} catch (NoResultException nre) {
			this.getLog().info("findAllByResourceLabelAndCustomer returned no result");
			return new ArrayList<SynchronizationSubscription>();
		}
	}

	@Override
	public Class<SynchronizationSubscription> getType() {
		return SynchronizationSubscription.class;
	}
}
