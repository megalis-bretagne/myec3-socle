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
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.GenericProfileDao;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.User;
import org.springframework.util.Assert;

/**
 * This class implements methods to perform queries on {@link Profile} objects
 * or objects extending profiles.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T> instance, child of Profile to query on
 */
public abstract class JpaGenericProfileDao<T extends Profile> extends JpaResourceDao<T>
		implements GenericProfileDao<T> {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAllByEmail(String email) {
		// validate entry parameters
		Assert.notNull(email, "email is mandatory. null value is forbidden.");

		this.getLog().debug("find all profiles of class " + this.getDomainClass().getSimpleName() + " by: " + email);

		try {
			Query query = this.getEm().createQuery(
					"select p from " + this.getDomainClass().getSimpleName() + " p where lower(p.email) like :email");
			query.setParameter("email", email.toLowerCase());
			List<T> resourceList = query.getResultList();
			this.getLog().debug("findAllProfilesByEmail successfull.");
			return resourceList;
		} catch (RuntimeException re) {
			// error occured, we return an empty list
			this.getLog().error("findAllProfilesByEmail failed.", re);
			return new ArrayList<T>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAllProfileEnabledByEmailOrUsername(String emailOrUsername) {
		// validate entry parameters
		Assert.notNull(emailOrUsername, "email is mandatory. null value is forbidden.");

		this.getLog().debug("find all profiles enabled of class " + this.getDomainClass().getSimpleName()
				+ " by email or username : " + emailOrUsername);

		try {
			Query query = this.getEm().createQuery("select p from " + this.getDomainClass().getSimpleName()
					+ " p inner join p.user u where (lower(p.email) like :email or u.username like :username) and u.enabled = true");
			query.setParameter("email", emailOrUsername.toLowerCase());
			query.setParameter("username", emailOrUsername.toLowerCase());
			List<T> resourceList = query.getResultList();
			this.getLog().debug("findAllProfileEnabledByEmailOrUsername successfull.");
			return resourceList;
		} catch (NoResultException re) {
			getLog().info("findAllProfileEnabledByEmailOrUsername returned no result. " + re.getMessage());
			return new ArrayList<T>();
		} catch (RuntimeException re) {
			// error occured
			this.getLog().error("findAllProfileEnabledByEmailOrUsername failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAllProfilesByUser(User user) {
		getLog().debug("Finding all of class " + this.getDomainClass().getSimpleName() + " by User " + user.getName());
		try {
			Query query = getEm().createQuery("select p from " + this.getDomainClass().getSimpleName() + " p "
					+ "inner join p.user u where u = :user");
			query.setParameter("user", user);
			List<T> profileList = query.getResultList();
			getLog().debug("findAllProfilesByUser successfull.");
			return profileList;
		} catch (RuntimeException re) {
			// error occured, we return an empty list
			getLog().error("findAllProfilesByUser failed.", re);
			return new ArrayList<T>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAllProfilesByRole(Role role) {
		getLog().debug("Finding " + this.getDomainClass().getSimpleName() + " by Role " + role.getName());
		try {
			Query query = getEm().createQuery("select p from " + this.getDomainClass().getSimpleName() + " p "
					+ "inner join p.roles r where r = :role");
			query.setParameter("role", role);
			List<T> profileList = query.getResultList();
			getLog().debug("findAllProfilesByRole successfull.");
			return profileList;
		} catch (RuntimeException re) {
			// error occured, we return an empty list
			getLog().error("findAllProfilesByRole failed.", re);
			return new ArrayList<T>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T findByEmail(String email) {
		getLog().debug("Finding " + this.getDomainClass().getSimpleName() + " by email " + email);
		try {
			Query query = getEm().createQuery(
					"select p from " + this.getDomainClass().getSimpleName() + " p " + "where p.email = :email");
			query.setParameter("email", email);
			T profile = (T) query.getSingleResult();
			getLog().debug("findByEmail successfull.");
			return profile;
		} catch (NoResultException re) {
			getLog().info("findByEmail returned no result. " + re.getMessage());
			return null;
		} catch (RuntimeException re) {
			getLog().error("findByEmail failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAllByCriteria(String email, String username, String firstname, String lastname,
			Long sviProfile) {

		getLog().debug("Finding all Profiles by Email, Firstname, Lastname and sviProfile");
		try {

			Boolean hasCriteria = Boolean.FALSE;
			StringBuffer queryString = new StringBuffer(
					"select s from " + this.getDomainClass().getSimpleName() + " s");
			if ((null != email) || (null != username) || (null != firstname) || (null != lastname)
					|| (null != sviProfile)) {
				queryString.append(" where ");

				if (null != email) {
					queryString.append("s.email like :email");
					hasCriteria = Boolean.TRUE;
				}
				if (null != username) {
					queryString.append("s.user.username like :username");
					hasCriteria = Boolean.TRUE;
				}
				if (null != firstname) {
					if (hasCriteria) {
						queryString.append(" and ");
					} else {
						hasCriteria = Boolean.TRUE;
					}
					queryString.append("s.user.firstname like :firstname");
				}
				if (null != lastname) {
					if (hasCriteria) {
						queryString.append(" and ");
					} else {
						hasCriteria = Boolean.TRUE;
					}
					queryString.append("s.user.lastname like :lastname");
				}
				if (null != sviProfile) {
					if (hasCriteria) {
						queryString.append(" and ");
					} else {
						hasCriteria = Boolean.TRUE;
					}
					queryString.append("s.user.sviProfile.id like :sviProfile");
				}
			}

			Query query = getEm().createQuery(queryString.toString());
			if (null != email) {
				query.setParameter("email", email + "%");
			}
			if (null != username) {
				query.setParameter("username", username + "%");
			}
			if (null != firstname) {
				query.setParameter("firstname", "%" + firstname + "%");
			}
			if (null != lastname) {
				query.setParameter("lastname", "%" + lastname + "%");
			}
			if (null != sviProfile) {
				query.setParameter("sviProfile", sviProfile);
			}

			List<T> profileList = query.getResultList();
			getLog().debug("findAllByCriteria successfull.");
			return profileList;
		} catch (NoResultException re) {
			getLog().warn("findAllByCriteria returned no result.");
			return new ArrayList<T>();
		} catch (RuntimeException re) {
			getLog().error("findAllByCriteria failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAllProfilesByListOfId(List<Long> listOfIds) {
		// validate entry parameters
		Assert.notNull(listOfIds, "List of profile's id is mandatory. null value is forbidden.");
		this.getLog().debug("find all profiles of  by id");

		try {
			Query query = this.getEm().createQuery(
					"select p from " + this.getDomainClass().getSimpleName() + " p where p.id in (:listOfIds)");
			query.setParameter("listOfIds", listOfIds);
			List<T> resourceList = query.getResultList();
			this.getLog().debug("findAllProfilesByListOfId successfull.");
			return resourceList;
		} catch (NoResultException re) {
			this.getLog().error("findAllProfilesByListOfId returned no result.", re);
			return new ArrayList<T>();
		} catch (RuntimeException re) {
			// error occured, we throw the exception
			this.getLog().error("findAllProfilesByListOfId failed.", re);
			throw re;
		}
	}

}
