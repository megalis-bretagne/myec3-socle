/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p>
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.core.domain.dao.jpa;

import org.myec3.socle.core.domain.dao.UserDao;
import org.myec3.socle.core.domain.model.ConnectionInfos;
import org.myec3.socle.core.domain.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.util.List;

/**
 * This implementation provides methods to perform specific queries on
 * {@link User} objects.
 *
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Repository("userDao")
public class JpaUserDao extends JpaResourceDao<User> implements UserDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findByUsername(String username) {
		this.getLog().debug("Finding User instance by username: " + username);
		try {
			Query query = getEm().createQuery(
					"select r from " + this.getDomainClass().getSimpleName() + " r where r.username = :username");
			query.setParameter("username", username);
			User user = (User) query.getSingleResult();
			this.getLog().debug("findByUsername successfull.");
			return user;
		} catch (NoResultException e) {
			this.getLog().warn("findByUsername returned no result.");
			return null;
		} catch (NonUniqueResultException e) {
			this.getLog().error("findByUsername returned more than one result.", e);
			return null;
		} catch (RuntimeException re) {
			this.getLog().error("findByUsername failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findByFunctionalAccountId(Long id) {
		this.getLog().debug("Finding User instance by functional account id: " + id);
		try {
			Query query = getEm()
					.createQuery("select u from " + "FunctionalAccount f " + "inner join f.user u where f.id = :id");
			query.setParameter("id", id);
			User result = (User) query.getSingleResult();
			getLog().debug("findByFunctionalAccountId successfull.");
			return result;
		} catch (NoResultException e) {
			this.getLog().warn("findByFunctionalAccountId returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findByFunctionalAccountId failed.", re);
			return null;
		}
	}

	public Long findUserIdByConnectionInfos(ConnectionInfos connectionInfos) {
		this.getLog().debug("Finding User id by connection infos");
		try {
			Query q = getEm().createQuery("SELECT id FROM " + this.getDomainClass().getSimpleName()
					+ " r WHERE r.connectionInfos = :connectionInfos");
			q.setParameter("connectionInfos", connectionInfos);
			Long userId = (Long) q.getSingleResult();
			getLog().debug("findUserIdByConnectionInfos successfull.");
			return userId;
		} catch (NoResultException e) {
			this.getLog().warn("findUserIdByConnectionInfos returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findUserIdByConnectionInfos failed.", re);
			return null;
		}
	}

	public List<User> findUsersByCertificate(String certificate) {
		this.getLog().debug("Finding User id by certificate");
		try {
			Query q = getEm().createQuery(
					"SELECT u FROM " + this.getDomainClass().getSimpleName() + " u WHERE u.certificate = :certificate");
			q.setParameter("certificate", certificate.replaceAll("\\r", ""));
			List<User> user = q.getResultList();
			getLog().debug("findUsersIdByCertificate successfull.");
			return user;
		} catch (NoResultException e) {
			this.getLog().warn("findUsersIdByCertificate returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findUsersIdByCertificate failed.", re);
			return null;
		}
	}

	@Override
	public Class<User> getType() {
		return User.class;
	}
}
