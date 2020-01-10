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

import org.myec3.socle.core.domain.dao.StructureTypeApplicationDao;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.meta.StructureTypeApplication;
import org.springframework.stereotype.Repository;

/**
 * This implementation provides methods to perform specific queries on
 * {@link StructureTypeApplication} objects.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
@Repository("structureTypeApplicationDao")
public class JpaStructureTypeApplicationDao extends JpaNoResourceGenericDao<StructureTypeApplication>
		implements StructureTypeApplicationDao {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StructureTypeApplication> findAllStructureTypeApplicationByApplication(Application application) {
		this.getLog().debug("find all StructureTypeApplication by application : " + application.getName());
		try {
			Query query = this.getEm().createQuery("select s from " + this.getDomainClass().getSimpleName()
					+ " s inner join s.application a where a = :application");
			query.setParameter("application", application);
			List<StructureTypeApplication> resourceList = query.getResultList();
			this.getLog().debug("findAllStructureTypeApplicationByApplication successfull.");
			return resourceList;
		} catch (RuntimeException re) {
			this.getLog().error("findAllStructureTypeApplicationByApplication failed.", re);
			return new ArrayList<StructureTypeApplication>();
		}
	}

	@Override
	public Class<StructureTypeApplication> getType() {
		return StructureTypeApplication.class;
	}

}
