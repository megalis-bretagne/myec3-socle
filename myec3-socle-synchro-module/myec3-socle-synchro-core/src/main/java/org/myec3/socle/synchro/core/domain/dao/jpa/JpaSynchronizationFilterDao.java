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

import org.myec3.socle.synchro.core.domain.dao.SynchronizationFilterDao;
import org.myec3.socle.synchro.core.domain.model.SynchronizationFilter;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 * This implementation provide methods to perform specific queries on
 * {@link SynchronizationFilter} objects. It only provides new specific methods
 * and inherits methods from {@link JpaGenericSynchronizationDao}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Repository("synchronizationFilterDao")
public class JpaSynchronizationFilterDao extends JpaGenericSynchronizationDao<SynchronizationFilter>
		implements SynchronizationFilterDao {

	@Override
	public Class<SynchronizationFilter> getType() {
		return SynchronizationFilter.class;
	}

	@Override
	public SynchronizationFilter findByApplicationsDisplayedAndByRolesDisplayed(boolean applicationsDisplayed, boolean rolesDisplayed) {
		this.getLog().debug("Finding instance of SynchronizationFilter with applicationsDisplayed : {} and rolesDisplayed : {}", applicationsDisplayed, rolesDisplayed);

		try {
			Query query = getEm().createQuery("select s from " + this.getDomainClass().getSimpleName()
					+ " s WHERE allRolesDisplayed = :rolesDisplayed AND allApplicationsDisplayed = :applicationsDisplayed");
			query.setParameter("applicationsDisplayed", applicationsDisplayed);
			query.setParameter("rolesDisplayed", rolesDisplayed);
			SynchronizationFilter instance = (SynchronizationFilter) query.getSingleResult();
			this.getLog().debug("findByApplicationsDisplayedAndByRolesDisplayed successfull.");
			return instance;
		} catch (NoResultException e) {
			this.getLog().warn("findByApplicationsDisplayedAndByRolesDisplayed returned no result");
			return null;
		}
	}
}
