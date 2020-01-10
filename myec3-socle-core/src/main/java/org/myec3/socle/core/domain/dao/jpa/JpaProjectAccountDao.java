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

import org.myec3.socle.core.domain.dao.ProjectAccountDao;
import org.myec3.socle.core.domain.model.ProjectAccount;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * This implementation provides methods to perform specific queries on
 * {@link ProjectAccount} objects.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Repository("projectAccountDao")
public class JpaProjectAccountDao extends JpaResourceDao<ProjectAccount> implements ProjectAccountDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectAccount findByEmail(String email) {
		getLog().debug("Finding ProjectAccount by email " + email);
		try {
			Query query = getEm().createQuery(
					"select p from " + this.getDomainClass().getSimpleName() + " p where p.email = :email");
			query.setParameter("email", email);
			ProjectAccount projectAccount = (ProjectAccount) query.getSingleResult();
			getLog().debug("findByEmail successfull.");
			return projectAccount;
		} catch (NoResultException re) {
			this.getLog().warn("findByEmail returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findByEmail failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectAccount> findAllByEmail(String email) {
		// validate entry parameters
		Assert.notNull(email, "email is mandatory. null value is forbidden.");

		this.getLog()
				.debug("find all projectAccounts of class " + this.getDomainClass().getSimpleName() + " by: " + email);

		try {
			Query query = this.getEm().createQuery(
					"select p from " + this.getDomainClass().getSimpleName() + " p where lower(p.email) like :email");
			query.setParameter("email", email.toLowerCase());
			List<ProjectAccount> resourceList = query.getResultList();
			this.getLog().debug("findAllByEmail successfull.");
			return resourceList;
		} catch (RuntimeException re) {
			// error occured, we return an empty list
			this.getLog().error("findAllByEmail failed.", re);
			return new ArrayList<ProjectAccount>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectAccount findByLogin(String login) {
		getLog().debug("Finding ProjectAccount by login " + login);
		try {
			Query query = getEm().createQuery(
					"select p from " + this.getDomainClass().getSimpleName() + " p where p.login = :login");
			query.setParameter("login", login);
			ProjectAccount projectAccount = (ProjectAccount) query.getSingleResult();
			getLog().debug("findByLogin successfull.");
			return projectAccount;
		} catch (NoResultException re) {
			this.getLog().warn("findByLogin returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findByLogin failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectAccount> findAllEnabledByEmailOrUsername(String emailOrUsername) {
		// validate entry parameters
		Assert.notNull(emailOrUsername, "email or username is mandatory. null value is forbidden.");

		this.getLog().debug("find all enabled projectAccounts of class " + this.getDomainClass().getSimpleName()
				+ " by email or username : " + emailOrUsername);

		try {
			Query query = this.getEm().createQuery("select p from " + this.getDomainClass().getSimpleName()
					+ " p inner join p.user u where (lower(p.email) like :email or u.username like :username) and u.enabled = true");
			query.setParameter("email", emailOrUsername.toLowerCase());
			query.setParameter("username", emailOrUsername);
			List<ProjectAccount> resourceList = query.getResultList();
			this.getLog().debug("findAllEnabledByEmailOrUsername successfull.");
			return resourceList;

		} catch (NoResultException e) {
			this.getLog().info("findAllEnabledByEmailOrUsername returned no result.", e);
			return new ArrayList<ProjectAccount>();
		} catch (RuntimeException re) {
			// error occured
			this.getLog().error("findAllEnabledByEmailOrUsername failed.", re);
			throw re;
		}
	}

	@Override
	public Class<ProjectAccount> getType() {
		return ProjectAccount.class;
	}
}
