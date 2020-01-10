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

import org.myec3.socle.core.domain.dao.AdminProfileDao;
import org.myec3.socle.core.domain.model.AdminProfile;
import org.springframework.stereotype.Repository;

/**
 * * This is implementation of methods defined by the interface
 * {@link AdminProfileDao} to execute specific queries on {@link AdminProfile}
 * objects). It is only the implementations on methods and inherits methods from
 * {@link JpaResourceDao}.
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 */
@Repository("adminProfileDao")
public class JpaAdminProfileDao extends JpaGenericProfileDao<AdminProfile> implements AdminProfileDao {

	@Override
	public Class<AdminProfile> getType() {
		return AdminProfile.class;
	}
}
