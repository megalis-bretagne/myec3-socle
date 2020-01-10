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
package org.myec3.socle.core.domain.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.ApplicationDao;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.springframework.stereotype.Repository;

/**
 * This implementation provides methods to perform specific queries on
 * {@link Application} objects. It only provides new specific methods and
 * inherits methods from {@link JpaResourceDao}.
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Repository("applicationDao")
public class JpaApplicationDao extends JpaResourceDao<Application> implements ApplicationDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Application> findAllApplicationByStructure(Structure structure) {
		getLog().debug("Finding all Application by Company " + structure.getName());
		try {
			Query query = getEm().createQuery("select a from " + this.getDomainClass().getSimpleName()
					+ " a inner join a.structures s where s = :structure");
			query.setParameter("structure", structure);
			List<Application> applicationList = query.getResultList();
			getLog().debug("findAllApplicationByStructure successfull.");
			return applicationList;
		} catch (NoResultException re) {
			// No result found, we return empty collection instead of errors
			getLog().warn("findAllApplicationByStructure returned no result.");
			return new ArrayList<Application>();
		} catch (RuntimeException re) {
			getLog().error("findAllApplicationByStructure failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Application> findAllDefaultApplicationsByStructureTypeAndCustomer(StructureType structureType,
			Customer customer) {
		getLog().debug("Finding all default Application for StructureType" + structureType.getValue().toString());
		try {
			Query query = getEm().createQuery("select a from " + this.getDomainClass().getSimpleName()
					+ " a inner join a.structureTypes s inner join a.customers c where s.structureType = :structureType and s.defaultSubscription = :defaultApplication and s.subscribable = :subscribable and c = :customer");
			query.setParameter("structureType", structureType);
			query.setParameter("defaultApplication", Boolean.TRUE);
			query.setParameter("subscribable", Boolean.FALSE);
			query.setParameter("customer", customer);

			List<Application> applicationList = query.getResultList();
			getLog().debug("findAllDefaultApplicationsByStructureTypeAndCustomer successfull.");
			return applicationList;
		} catch (NoResultException re) {
			// No result found, we return empty collection instead of errors
			getLog().warn("findAllDefaultApplicationsByStructureTypeAndCustomer returned no result.");
			return new ArrayList<Application>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Application> findAllDefaultApplicationsByStructureType(StructureType structureType) {
		getLog().debug("Finding all default Application for StructureType" + structureType.getValue().toString());
		try {
			Query query = getEm().createQuery("select a from " + this.getDomainClass().getSimpleName()
					+ " a inner join a.structureTypes s where s.structureType = :structureType and s.defaultSubscription = :defaultApplication and s.subscribable = :subscribable");
			query.setParameter("structureType", structureType);
			query.setParameter("defaultApplication", Boolean.TRUE);
			query.setParameter("subscribable", Boolean.FALSE);
			List<Application> applicationList = query.getResultList();
			getLog().debug("findAllDefaultApplicationByStructureType successfull.");
			return applicationList;
		} catch (NoResultException re) {
			// No result found, we return empty collection instead of errors
			getLog().warn("findAllDefaultApplicationByStructureType returned no result.");
			return new ArrayList<Application>();
		} catch (RuntimeException re) {
			getLog().error("findAllDefaultApplicationByStructureType failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Application> findAllApplicationSubscribableByStructureType(StructureType structureType,
			Boolean subscribable) {
		getLog().debug("Finding all subscribable(" + subscribable + ") Application  for StructureType"
				+ structureType.getValue().toString());
		try {
			Query query = getEm().createQuery("select a from " + this.getDomainClass().getSimpleName() + " a "
					+ "inner join a.structureTypes s where s.structureType = :structureType and s.subscribable = :subscribable");
			query.setParameter("structureType", structureType);
			query.setParameter("subscribable", subscribable);
			List<Application> applicationList = query.getResultList();
			getLog().debug("findAllApplicationSubscribableByStructureType successfull.");
			return applicationList;
		} catch (NoResultException re) {
			getLog().warn("findAllApplicationSubscribableByStructureType returned no result.");
			return new ArrayList<Application>();
		} catch (RuntimeException re) {
			getLog().error("findAllApplicationSubscribableByStructureType failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Application> findAllApplicationsAllowingMultipleRolesByStructureTypeValue(
			StructureTypeValue structureTypeValue) {
		getLog().debug("Finding all Applications allowing multiple roles for structure type = " + structureTypeValue);
		try {
			Query query = getEm().createQuery("select a from  " + this.getDomainClass().getSimpleName()
					+ "  a inner join a.structureTypes s where s.structureType.value like :structureTypeValue AND s.multipleRoles = :multipleRoles");
			query.setParameter("structureTypeValue", structureTypeValue);
			query.setParameter("multipleRoles", Boolean.TRUE);

			List<Application> applicationList = query.getResultList();
			getLog().debug("findAllApplicationsAllowingMultipleRolesByStructureTypeValue successfull.");
			return applicationList;
		} catch (NoResultException re) {
			// No result found, we return empty collection instead of errors
			getLog().warn("findAllApplicationsAllowingMultipleRolesByStructureTypeValue returned no result.");
			return new ArrayList<Application>();
		} catch (RuntimeException re) {
			getLog().error("findAllApplicationsAllowingMultipleRolesByStructureTypeValue failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Application> findAllApplicationsWithManageableRolesByStructureType(StructureType structureType) {
		getLog().debug("Finding all manageable application for StructureType" + structureType.getValue().toString());
		try {
			Query query = getEm().createQuery("select a from " + this.getDomainClass().getSimpleName()
					+ " a inner join a.structureTypes s where s.structureType = :structureType and s.manageableRoles = :manageableRoles");
			query.setParameter("structureType", structureType);
			query.setParameter("manageableRoles", Boolean.TRUE);

			List<Application> applicationList = query.getResultList();
			getLog().debug("findAllApplicationsWithManageableRolesByStructureType successfull.");
			return applicationList;
		} catch (NoResultException re) {
			getLog().warn("findAllApplicationsWithManageableRolesByStructureType returned no result.");
			return new ArrayList<Application>();
		} catch (RuntimeException re) {
			getLog().error("findAllApplicationsWithManageableRolesByStructureType failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Application> findAllApplicationSubscribableByStructureTypeAndCustomer(StructureType structureType,
			Customer customer) {
		this.getLog().debug("Finding all subscribable applications for StructureType"
				+ structureType.getValue().toString() + "and customer " + customer.getLabel());
		try {
			Query query = this.getEm().createQuery("select a from " + this.getDomainClass().getSimpleName() + " a "
					+ "inner join a.structureTypes s inner join a.customers c WHERE c = :customer AND s.structureType = :structureType AND s.subscribable = :subscribable");
			query.setParameter("structureType", structureType);
			query.setParameter("subscribable", Boolean.TRUE);
			query.setParameter("customer", customer);

			List<Application> applicationList = query.getResultList();
			getLog().debug("findAllApplicationSubscribableByStructureTypeAndCustomer successfull.");
			return applicationList;
		} catch (NoResultException re) {
			getLog().warn("findAllApplicationSubscribableByStructureTypeAndCustomer returned no result.");
			return new ArrayList<Application>();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Application> findAllApplicationsByCustomer(Customer customer) {
		this.getLog().debug("Finding all applications for customer" + customer.getLabel());
		try {
			Query query = this.getEm().createQuery("select a from " + this.getDomainClass().getSimpleName() + " a "
					+ "inner join a.customers c WHERE c = :customer");
			query.setParameter("customer", customer);

			List<Application> applicationList = query.getResultList();
			getLog().debug("findAllApplicationsByCustomer successfull.");
			return applicationList;
		} catch (NoResultException re) {
			getLog().warn("findAllApplicationsByCustomer returned no result.");
			return new ArrayList<Application>();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Application> findAllApplicationByStructureType(StructureType structureType) {
		getLog().debug("Finding all Application for StructureType" + structureType.getValue().toString());
		try {
			Query query = getEm().createQuery("select a from " + this.getDomainClass().getSimpleName()
					+ " a inner join a.structureTypes s where s.structureType = :structureType ");
			query.setParameter("structureType", structureType);
			List<Application> applicationList = query.getResultList();
			getLog().debug("findAllApplicationByStructureType successfull.");
			return applicationList;
		} catch (NoResultException re) {
			// No result found, we return empty collection instead of errors
			getLog().warn("findAllApplicationByStructureType returned no result.");
			return new ArrayList<Application>();
		} catch (RuntimeException re) {
			getLog().error("findAllApplicationByStructureType failed.", re);
			throw re;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Application> findAllByCriteria(String label, StructureType structureType) {
		if (null != structureType) {
			getLog().debug("Finding all Application by label," + label + "and structureType"
					+ structureType.getValue().toString());
		} else {
			getLog().debug("Finding all Application by label," + label + "and structureType TOUS");
		}
		try {
			Boolean hasCriteria = Boolean.FALSE;
			StringBuffer queryString = new StringBuffer(
					"SELECT a FROM " + this.getDomainClass().getSimpleName() + " a ");
			if ((null != label) || (null != structureType)) {
				if (null != structureType) {
					queryString.append("inner join a.structureTypes s");
				}
				queryString.append(" WHERE ");
				if (null != label) {
					queryString.append("UPPER(a.label) like UPPER(:label)");
					hasCriteria = Boolean.TRUE;
				}
				if (null != structureType) {
					if (hasCriteria) {
						queryString.append(" and ");
					}
					queryString.append("s.structureType = :structureType");
				}
			}
			Query query = getEm().createQuery(queryString.toString());
			if (null != label) {
				query.setParameter("label", "%" + label + "%");
			}
			if (null != structureType) {
				query.setParameter("structureType", structureType);
			}
			List<Application> applicationList = query.getResultList();
			getLog().debug("findAllByCriteria successfull.");
			return applicationList;
		} catch (NoResultException re) {
			// No result found, we return empty collection instead of errors
			getLog().warn("findAllByCriteria returned no result.");
			return new ArrayList<Application>();
		} catch (RuntimeException re) {
			getLog().error("findAllByCriteria failed.", re);
			throw re;
		}

	}

	@Override
	public Class<Application> getType() {
		return Application.class;
	}

}
