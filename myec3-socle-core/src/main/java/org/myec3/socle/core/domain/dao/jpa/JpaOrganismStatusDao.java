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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.OrganismStatusDao;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismStatus;
import org.springframework.stereotype.Repository;

/**
 * This interface define methods to perform specific queries on
 * {@link OrganismStatus} objects. It only provides new specific methods and
 * inherits methods from {@link JpaNoResourceGenericDao}.
 */
@Repository("organismStatusDao")
public class JpaOrganismStatusDao extends JpaNoResourceGenericDao<OrganismStatus> implements OrganismStatusDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Set<OrganismStatus> findAllOrganismStatusByOrganism(Organism organism) {

		this.getLog().debug("Finding all Resource instances with Organism: " + organism);
		try {
			Query query = this.getEm().createQuery("select r from " + this.getDomainClass().getSimpleName()
					+ " r where r.organism like :organism order by r.organism.id");
			query.setParameter("organism", organism);
			List<OrganismStatus> resourceList = query.getResultList();
			Set<OrganismStatus> resourceSet = new HashSet<>(resourceList);
			this.getLog().debug("findAllStatusByOrganismId successfull.");
			return resourceSet;
		} catch (RuntimeException re) {
			this.getLog().error("findAllStatusByOrganismId failed.", re);
			return new HashSet<OrganismStatus>();
		}
	}

	@Override
	public Class<OrganismStatus> getType() {
		return OrganismStatus.class;
	}
}
