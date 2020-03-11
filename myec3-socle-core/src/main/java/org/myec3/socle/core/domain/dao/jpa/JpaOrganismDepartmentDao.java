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

import org.myec3.socle.core.domain.dao.OrganismDepartmentDao;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.springframework.stereotype.Repository;

/**
 * This interface define methods to perform specific queries on
 * {@link OrganismDepartment} objects. It only provides new specific methods and
 * inherits methods from {@link JpaResourceDao}.
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Repository("organismDepartmentDao")
public class JpaOrganismDepartmentDao extends JpaResourceDao<OrganismDepartment> implements OrganismDepartmentDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<OrganismDepartment> findAllDepartmentByOrganism(Organism organism) {

		this.getLog().debug("Finding all Resource instances with Organism: " + organism);
		try {
			Query query = this.getEm().createQuery("select r from " + this.getDomainClass().getSimpleName()
					+ " r where r.organism like :organism order by r.parentDepartment.id");
			query.setParameter("organism", organism);
			List<OrganismDepartment> resourceList = query.getResultList();
			this.getLog().debug("findAllDepartmentByOrganismId successfull.");
			return resourceList;
		} catch (RuntimeException re) {
			this.getLog().error("findAllDepartmentByOrganismId failed.", re);
			return new ArrayList<OrganismDepartment>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrganismDepartment findRootOrganismDepartment(Organism organism) {
		this.getLog().debug("Finding root Department instances with Organism: " + organism);
		try {
			Query query = this.getEm().createQuery("select d from " + this.getDomainClass().getSimpleName()
					+ " d where d.organism = :organism and d.parentDepartment is null");
			query.setParameter("organism", organism);
			OrganismDepartment res = (OrganismDepartment) query.getSingleResult();
			this.getLog().debug("findRootOrganismDepartment successfull.");
			return res;
		} catch (NoResultException e) {
			this.getLog().info("findRootOrganismDepartment returned no result");
			return null;
		} catch (RuntimeException re) {
			this.getLog().error("findRootOrganismDepartment failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<OrganismDepartment> findAllChildrenDepartment(OrganismDepartment department) {
		this.getLog().debug("Finding all children department of OrganismDepartment: " + department);
		try {
			Query query = this.getEm().createQuery("select d from " + this.getDomainClass().getSimpleName()
					+ " d where d.parentDepartment = :department");
			query.setParameter("department", department);
			List<OrganismDepartment> resourceList = query.getResultList();
			this.getLog().debug("findAllChildrenDepartment successfull.");
			return resourceList;
		} catch (RuntimeException re) {
			this.getLog().error("findAllChildrenDepartment failed.", re);
			return new ArrayList<OrganismDepartment>();
		}
	}

	@Override
	public OrganismDepartment findOrganismDepartmentByIdSdm(long idSdm) {

		Query q = this.getEm().createQuery("select c from " + this.getDomainClass().getSimpleName() + " c" + " JOIN SynchroIdentifiantExterne s on c.id=s.idSocle"
				+ " WHERE s.idAppliExterne= :idSdm AND s.typeRessource= :typeResource");
		q.setParameter("idSdm", idSdm);
		q.setParameter("typeResource", ResourceType.ORGANISM_DEPARTMENT);
		try{
			//Company results = (Company) q.getSingleResult();
			OrganismDepartment result = (OrganismDepartment) q.getSingleResult();
			getLog().debug("findOrganismDepartmentByIdSdm successfull.");
			return result;
		} catch (NoResultException nre) {
			getLog().warn("findOrganismDepartmentByIdSdm returned no results.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findOrganismDepartmentByIdSdm failed.", re);
			return null;
		}
	}

	@Override
	public Class<OrganismDepartment> getType() {
		return OrganismDepartment.class;
	}
}
