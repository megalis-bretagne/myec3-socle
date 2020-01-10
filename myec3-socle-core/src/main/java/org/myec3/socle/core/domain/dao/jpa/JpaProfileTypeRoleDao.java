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

import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.ProfileTypeRoleDao;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.domain.model.meta.ProfileTypeRole;
import org.springframework.stereotype.Repository;

/**
 * This implementation provides methods to perform specific queries on
 * {@link ProfileTypeRole} objects.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
@Repository("profileTypeRoleDao")
public class JpaProfileTypeRoleDao extends JpaNoResourceGenericDao<ProfileTypeRole> implements ProfileTypeRoleDao {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ProfileTypeRole> findAllProfileTypeRoleByProfileType(ProfileType profileType) {
		this.getLog().debug("find all ProfileTypeRole by profile type : " + profileType.getValue());
		try {
			Query query = this.getEm().createQuery("select p from " + this.getDomainClass().getSimpleName()
					+ " p inner join p.profileType t where t = :profileType");
			query.setParameter("profileType", profileType);
			List<ProfileTypeRole> resourceList = query.getResultList();
			this.getLog().debug("findAllProfileTypeRoleByProfileType successfull.");
			return resourceList;
		} catch (RuntimeException re) {
			this.getLog().error("findAllProfileTypeRoleByProfileType failed.", re);
			return new ArrayList<ProfileTypeRole>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ProfileTypeRole> findAllProfileTypeRoleByRole(Role role) {
		this.getLog().debug("find all ProfileTypeRole by role : " + role.getName());
		try {
			Query query = this.getEm().createQuery("select p from " + this.getDomainClass().getSimpleName()
					+ " p inner join p.role r where r = :role");
			query.setParameter("role", role);
			List<ProfileTypeRole> resourceList = query.getResultList();
			this.getLog().debug("findAllProfileTypeRoleByRole successfull.");
			return resourceList;
		} catch (RuntimeException re) {
			this.getLog().error("findAllProfileTypeRoleByRole failed.", re);
			return new ArrayList<ProfileTypeRole>();
		}
	}

	@Override
	public Class<ProfileTypeRole> getType() {
		return ProfileTypeRole.class;
	}

}
