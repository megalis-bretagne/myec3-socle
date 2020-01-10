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

import org.myec3.socle.core.domain.dao.StructureDao;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Structure;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * This implementation provides methods to perform specific queries on
 * {@link Structure} objects.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Repository("structureDao")
public class JpaStructureDao extends JpaResourceDao<Structure> implements StructureDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Structure> findAllByLabelOrSiren(String value) {
		getLog().debug("Finding all Structures by Label or Siren");
		try {
			Query query = getEm().createQuery("select s from " + this.getDomainClass().getSimpleName()
					+ " s where UPPER(s.label) like UPPER(:label) or s.siren like :siren");

			query.setParameter("label", "%" + value + "%");
			query.setParameter("siren", value + "%");

			List<Structure> structureList = query.getResultList();
			getLog().debug("findAllByCriteria successfull.");
			return structureList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findAllByCriteria returned no result.");
			return new ArrayList<Structure>();
		} catch (RuntimeException re) {
			getLog().error("findAllByCriteria failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Structure> findAllStructureByApplication(Application application) {
		Assert.notNull(application, "application cannot be null");
		getLog().debug("Finding all Structure by Application " + application.getName());
		try {
			Query query = getEm().createQuery("select s from " + this.getDomainClass().getSimpleName() + " s "
					+ "inner join s.applications a where a = :application");
			query.setParameter("application", application);
			List<Structure> structureList = query.getResultList();
			getLog().debug("findAllStructureByApplication successfull.");
			return structureList;
		} catch (RuntimeException re) {
			getLog().error("findAllStructureByApplication failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Structure> findAllChildStructuresByStructure(Structure structure) {
		Assert.notNull(structure, "structure cannot be null");
		getLog().debug("Finding all child structures by structure " + structure.getName());
		try {
			Query query = getEm().createQuery("select s.childStructures from " + this.getDomainClass().getSimpleName()
					+ " s where s = :structure");
			query.setParameter("structure", structure);
			List<Structure> childStructuresList = query.getResultList();
			getLog().debug("findAllChildStructuresByStructure successfull.");
			return childStructuresList;
		} catch (NoResultException re) {
			getLog().warn("findAllChildStructuresByStructure returned no result.");
			return new ArrayList<Structure>();
		} catch (RuntimeException re) {
			getLog().error("findAllChildStructuresByStructure failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Structure> findAllParentStructuresByStructure(Structure structure) {
		Assert.notNull(structure, "structure cannot be null");
		getLog().debug("Finding all parent structures by structure " + structure.getName());
		try {
			Query query = getEm().createQuery("select s.parentStructures from " + this.getDomainClass().getSimpleName()
					+ " s where s = :structure");
			query.setParameter("structure", structure);
			List<Structure> parentStructuresList = query.getResultList();
			getLog().debug("findAllParentStructuresByStructure successfull.");
			return parentStructuresList;
		} catch (NoResultException re) {
			getLog().warn("findAllParentStructuresByStructure returned no result.");
			return new ArrayList<Structure>();
		} catch (RuntimeException re) {
			getLog().error("findAllParentStructuresByStructure failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Structure findBySiren(String siren) {
		getLog().debug("Finding a Structure by Siren");
		try {
			Query query = getEm().createQuery(
					"select s from " + this.getDomainClass().getSimpleName() + " s " + "where s.siren = :siren");
			query.setParameter("siren", siren);
			Structure foundStructure = (Structure) query.getSingleResult();
			getLog().debug("findBySiren successfull.");
			return foundStructure;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findBySiren returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findBySiren failed.", re);
			throw re;
		}
	}

	@Override
	public Class<Structure> getType() {
		return Structure.class;
	}
}
