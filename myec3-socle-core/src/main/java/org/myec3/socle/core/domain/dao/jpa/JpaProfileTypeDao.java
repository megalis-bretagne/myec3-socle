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

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.ProfileTypeDao;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.springframework.stereotype.Repository;

/**
 * This implementation provides methods to perform specific queries on
 * {@link ProfileType} objects.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
@Repository("profileTypeDao")
public class JpaProfileTypeDao extends JpaNoResourceGenericDao<ProfileType> implements ProfileTypeDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileType findByValue(ProfileTypeValue value) {

		this.getLog().debug("find ProfileType by: " + value);
		try {
			Query query = this.getEm().createQuery(
					"select p from " + this.getDomainClass().getSimpleName() + " p where p.value like :value");
			query.setParameter("value", value);
			ProfileType resourceResult = (ProfileType) query.getSingleResult();
			this.getLog().debug("findByValue successfull.");
			return resourceResult;
		} catch (NoResultException e) {
			this.getLog().warn("findByValue returned no result.");
			return null;
		} catch (RuntimeException re) {
			this.getLog().error("findByValue failed.", re);
			return null;
		}
	}

	@Override
	public Class<ProfileType> getType() {
		return ProfileType.class;
	}
}
