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

import org.myec3.socle.core.domain.dao.StructureTypeDao;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * This implementation provides methods to perform specific queries on
 * {@link StructureType} objects.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
@Repository("structureTypeDao")
public class JpaStructureTypeDao extends JpaNoResourceGenericDao<StructureType> implements StructureTypeDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureType findByValue(StructureTypeValue value) {
		Assert.notNull(value, "value is mandatory. null value is forbidden.");
		this.getLog().debug("find StructureType by: " + value);
		try {
			Query query = this.getEm().createQuery(
					"select s from " + this.getDomainClass().getSimpleName() + " s where s.value like :value");
			query.setParameter("value", value);
			StructureType resourceResult = (StructureType) query.getSingleResult();
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
	public Class<StructureType> getType() {
		return StructureType.class;
	}

}
