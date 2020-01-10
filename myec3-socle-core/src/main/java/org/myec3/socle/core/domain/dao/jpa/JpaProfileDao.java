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

import org.myec3.socle.core.domain.dao.ProfileDao;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.springframework.stereotype.Repository;

/**
 * This implementation provide methods to perform specific queries on
 * {@link Profile} objects. It only provides new specific methods and inherits
 * methods from {@link JpaGenericProfileDao}.
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Repository("profileDao")
public class JpaProfileDao extends JpaGenericProfileDao<Profile> implements ProfileDao {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Profile> findAllProfilesByProfileType(ProfileType profileType) {
		this.getLog().debug(
				"find all " + this.getDomainClass().getSimpleName() + " by profile type : " + profileType.getValue());
		try {
			Query query = this.getEm().createQuery("select p from " + this.getDomainClass().getSimpleName() + " p "
					+ "inner join p.profileType t where t = :profileType");
			query.setParameter("profileType", profileType);
			List<Profile> resourceList = query.getResultList();
			this.getLog().debug("findAllProfilesByProfileType successfull.");
			return resourceList;
		} catch (RuntimeException re) {
			// error occured, we return an empty list
			this.getLog().error("findAllProfilesByProfileType failed.", re);
			return new ArrayList<Profile>();
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Profile findByUsername(String username) {
		this.getLog().debug("find profile by username : " + username);
		try {
			Query query = this.getEm().createQuery("select p from " + this.getDomainClass().getSimpleName() + " p "
					+ "inner join p.user u where u.username = :username");
			query.setParameter("username", username);
			Profile profile = (Profile) query.getSingleResult();
			this.getLog().debug("findByUsername successfull.");
			return profile;
		} catch (NoResultException nre) {
			if (username != null && !username.isEmpty()) {
				this.getLog().warn("findByUsername returned no results with following username : " + username);
			} else {
				this.getLog().warn("findByUsername returned no results !");
			}
			return null;
		} catch (RuntimeException re) {
			this.getLog().error("findByUsername failed.", re);
			return null;
		}
	}

	public Profile findByUserId(Long userId) {
		this.getLog().debug("find profile by userId : " + userId);
		try {
			Query query = this.getEm().createQuery(
					"select p from " + this.getDomainClass().getSimpleName() + " p WHERE p.user.id = :userId ");
			query.setParameter("userId", userId);
			Profile profile = (Profile) query.getSingleResult();
			this.getLog().debug("findByUserId successfull.");
			return profile;
		} catch (NoResultException nre) {
			if (userId != null) {
				this.getLog().warn("findByUserId returned no results with following userId : " + userId);
			} else {
				this.getLog().warn("findByUserId returned no results !");
			}
			return null;
		} catch (RuntimeException re) {
			this.getLog().error("findByUserId failed.", re);
			return null;
		}
	}

	@Override
	public Class<Profile> getType() {
		return Profile.class;
	}
}
