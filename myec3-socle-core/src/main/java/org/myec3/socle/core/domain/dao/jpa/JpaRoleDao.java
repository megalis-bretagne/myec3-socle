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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.myec3.socle.core.domain.dao.RoleDao;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ProfileTypeRoleValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.springframework.stereotype.Repository;

/**
 * This implementation provides methods to perform specific queries on
 * {@link Role} objects. It only provides new specific methods and inherits
 * methods from {@link JpaResourceDao}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Repository("roleDao")
public class JpaRoleDao extends JpaResourceDao<Role> implements RoleDao {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> findAllRoleByProfile(Profile profile) {
		getLog().debug("Finding all Role by Profile " + profile);
		try {
			Query query = getEm().createQuery("select r from " + this.getDomainClass().getSimpleName() + " r "
					+ "inner join r.profiles p where p = :profile");
			query.setParameter("profile", profile);
			List<Role> roleList = query.getResultList();
			getLog().debug("findAllRoleByProfile successfull.");
			return roleList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findAllRoleByProfile returned no result.");
			return new ArrayList<Role>();
		} catch (RuntimeException re) {
			getLog().error("findAllRoleByProfile failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> findAllRoleByApplication(Application application) {
		getLog().debug("Finding all Role by Application " + application.getName());
		try {
			Query query = getEm().createQuery("select r from " + this.getDomainClass().getSimpleName() + " r "
					+ "inner join r.application a where a = :application");
			query.setParameter("application", application);
			List<Role> roleList = query.getResultList();
			getLog().debug("findAllRoleByApplication successfull.");
			return roleList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findAllRoleByApplication returned no result.");
			return new ArrayList<Role>();
		} catch (RuntimeException re) {
			getLog().error("findAllRoleByApplication failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Role> findAllRoleByProfileTypeAndApplication(ProfileType profileType, Application application,
			boolean withoutHidden) {

		getLog().debug("Finding all Role by ProfileType " + profileType.getValue() + " and Application "
				+ application.getName());
		try {
			StringBuffer queryString = new StringBuffer("select r from " + this.getDomainClass().getSimpleName());
			queryString.append(" r ");
			queryString.append("inner join r.profileTypes p where p.profileType = :profileType ");
			queryString.append("and r.application = :application");

			if (withoutHidden) {
				queryString.append(" and r.hidden <> 1");
			}
			Query query = getEm().createQuery(queryString.toString());
			query.setParameter("profileType", profileType);
			query.setParameter("application", application);
			List<Role> roleList = query.getResultList();
			getLog().debug("findAllRoleByProfileTypeAndApplication successfull.");
			return roleList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findAllRoleByProfileTypeAndApplication returned no result.");
			return new ArrayList<Role>();
		} catch (RuntimeException re) {
			getLog().error("findAllRoleByProfileTypeAndApplication failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	private Role findRoleByProfileTypeAndApplication(ProfileType profileType, Application application,
			ProfileTypeRoleValue profileTypeRoleValue) {
		if (null == profileTypeRoleValue) {
			throw new IllegalArgumentException("profileTypeRoleValue is mandatory. null value is forbidden");
		}
		getLog().debug("Finding admin Role by ProfileType " + profileType.getValue() + " and Application "
				+ application.getName());
		try {
			StringBuilder queryStr = new StringBuilder();
			queryStr.append("select r from ").append("Role r ").append("inner join r.profileTypes p ")
					.append("where p.profileType = :profileType ").append("and r.application = :application ");

			// adapt beahaviour if the query is about and Admin or a Basic Role
			if (ProfileTypeRoleValue.ADMIN.equals(profileTypeRoleValue)) {
				queryStr.append("and p.defaultAdmin = :profileTypeRoleValue");
			} else if (ProfileTypeRoleValue.BASIC.equals(profileTypeRoleValue)) {
				queryStr.append("and p.defaultBasic = :profileTypeRoleValue");
			}

			Query query = getEm().createQuery(queryStr.toString());
			query.setParameter("profileType", profileType);
			query.setParameter("application", application);
			query.setParameter("profileTypeRoleValue", Boolean.TRUE);
			Role role = (Role) query.getSingleResult();

			getLog().debug("findRoleByProfileTypeAndApplication successfull.");
			return role;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findRoleByProfileTypeAndApplication returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findRoleByProfileTypeAndApplication failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role findAdminRoleByProfileTypeAndApplication(ProfileType profileType, Application application) {
		return this.findRoleByProfileTypeAndApplication(profileType, application, ProfileTypeRoleValue.ADMIN);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role findBasicRoleByProfileTypeAndApplication(ProfileType profileType, Application application) {
		return this.findRoleByProfileTypeAndApplication(profileType, application, ProfileTypeRoleValue.BASIC);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Role> findAllRoleByProfileAndApplication(Profile profile, Application application) {

		return findAllRoleByProfileAndApplication(profile, application, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Role> findAllRoleByProfileAndApplication(Profile profile, Application application,
			boolean withoutHidden) {

		getLog().debug("Finding all Role by Application " + application.getName());
		try {
			StringBuffer queryString = new StringBuffer("select r from " + this.getDomainClass().getSimpleName());
			queryString.append(" r ");
			queryString.append("inner join r.profiles p where p = :profile ");
			queryString.append("and r.application = :application");

			if (withoutHidden) {
				queryString.append(" and r.hidden <> 1");
			}

			Query query = getEm().createQuery(queryString.toString());

			query.setParameter("profile", profile);
			query.setParameter("application", application);
			List<Role> roleList = query.getResultList();
			getLog().debug("findAllRoleByProfileAndApplication successfull.");
			return roleList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findAllRoleByProfileAndApplication returned no result.");
			return new ArrayList<Role>();
		} catch (RuntimeException re) {
			getLog().error("findAllRoleByProfileAndApplication failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Role> findAllByCriteria(String label, String name, Application application) {

		getLog().debug("Finding all Role by label, name");
		try {
			CriteriaBuilder cb = getEm().getCriteriaBuilder();
			CriteriaQuery<Role> query = cb.createQuery(Role.class);
			Root<Role> role = query.from(Role.class);

			List<Predicate> predicates = new ArrayList<>();
			if (null != name) {
				predicates.add(cb.like(role.get("name"), "%" + name + "%"));
			}
			if (null != label) {
				predicates.add(cb.like(role.get("label"), "%" + label + "%"));
			}
			if (null != application) {
				predicates.add(cb.equal(role.get("application"), application));
			}

			query.where(predicates.toArray(new Predicate[predicates.size()]));
			TypedQuery<Role> typedQuery = getEm().createQuery(query);
			return typedQuery.getResultList();
		} catch (NoResultException re) {
			// No result found, we return empty collection instead of errors
			getLog().warn("findAllByCriteria returned no result.");
			return new ArrayList<Role>();
		} catch (RuntimeException re) {
			getLog().error("findAllByCriteria failed.", re);
			throw re;
		}
	}

	@Override
	public Class<Role> getType() {
		return Role.class;
	}
}
