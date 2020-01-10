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

import org.myec3.socle.core.domain.dao.AcronymsListDao;
import org.myec3.socle.core.domain.model.AcronymsList;
import org.springframework.stereotype.Repository;

/**
 * This is implementation of methods defined by the interface
 * {@link AcronymsListDao} to execute specific queries on {@link AcronymsList}
 * objects). It is only the implementations on methods and inherits methods from
 * {@link JpaResourceDao}.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
@Repository("acronymsListDao")
public class JpaAcronymsListDao extends JpaNoResourceGenericDao<AcronymsList> implements AcronymsListDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AcronymsList findOneAvailableAcronym() {
		getLog().debug("Finding one available acronym");

		try {
			Query query = getEm().createQuery(
					"select a from " + this.getDomainClass().getSimpleName() + " a where a.available = :available");
			query.setParameter("available", Boolean.TRUE);
			query.setMaxResults(1);

			AcronymsList acronymResult = (AcronymsList) query.getSingleResult();
			getLog().debug("findOneAvailableAcronym successfull.");
			return acronymResult;
		} catch (RuntimeException re) {
			getLog().error("findOneAvailableAcronym failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AcronymsList findByValue(String value) {
		getLog().debug("Finding acronym with value " + value);
		try {
			Query query = getEm().createQuery(
					"select a from " + this.getDomainClass().getSimpleName() + " a where a.value = :value");
			query.setParameter("value", value);

			AcronymsList acronymResult = (AcronymsList) query.getSingleResult();
			getLog().debug("findByValue successfull.");
			return acronymResult;
		} catch (NoResultException e) {
			getLog().warn("findByValue returns no result.");
			return null;
		}
	}

	@Override
	public Class<AcronymsList> getType() {
		return AcronymsList.class;
	}

}
