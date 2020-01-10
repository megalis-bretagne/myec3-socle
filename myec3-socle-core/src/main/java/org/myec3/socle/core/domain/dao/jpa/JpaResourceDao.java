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

import org.myec3.socle.core.domain.dao.ResourceDao;
import org.myec3.socle.core.domain.model.Resource;

/**
 * DAO implementations for {@link Resource} (or derived) objects. This
 * implementation provides global methods that could be called on any class
 * derived from Resource
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T> Concrete Resource class
 */
public abstract class JpaResourceDao<T extends Resource> extends AbstractJpaDao<T> implements ResourceDao<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T findByName(String name) {
		getLog().debug("Finding Resource instance with name: " + name);
		try {
			Query query = getEm().createQuery("select r from "
					// + entityClass.getSimpleName()
					+ this.getDomainClass().getSimpleName() + " r where r.name like :name");
			query.setParameter("name", name);
			T resource = (T) query.getSingleResult();
			getLog().debug("FindByName successfull.");
			return resource;
		} catch (NoResultException e) {
			// No result found, we return null instead of errors
			getLog().info("FindByName returned no result");
			return null;
		} catch (RuntimeException re) {
			getLog().error("FindByName failed.", re);
			throw re;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByIntervalId(Long minId, Long maxId) {
		getLog().debug("Finding interval Resource instances.");
		try {
			Query query = getEm().createQuery("select r from "
					// + entityClass.getSimpleName()
					+ this.getDomainClass().getSimpleName() + " r where r.id between :minId and :maxId");

			query.setParameter("minId", minId);
			query.setParameter("maxId", maxId);
			List<T> resourceList = query.getResultList();
			getLog().debug("FindInterval successfull.");
			return resourceList;
		} catch (NoResultException re) {
			// No result found, we return an empty list instead of errors
			getLog().info("FindInterval failed. " + re.getMessage());
			return new ArrayList<T>();
		} catch (RuntimeException re) {
			getLog().error("FindInterval failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAllByLabel(String label) {
		getLog().debug("Finding all Resource instances with label: " + label);
		try {
			Query query = getEm().createQuery("select r from "
					// + entityClass.getSimpleName()
					+ this.getDomainClass().getSimpleName() + " r where r.label like :label");
			query.setParameter("label", label);
			List<T> resourceList = query.getResultList();
			getLog().debug("FindAllByLabel successfull.");
			return resourceList;
		} catch (RuntimeException re) {
			getLog().error("FindAllByLabel failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAllByPartialLabel(String label) {
		getLog().debug("Finding all Resource instances whose label contains : " + label);
		try {
			Query query = getEm().createQuery("select r from "
					// + entityClass.getSimpleName()
					+ this.getDomainClass().getSimpleName() + " r where UPPER(r.label) like UPPER(:label)");
			query.setParameter("label", "%" + label + "%");
			List<T> resourceList = query.getResultList();
			getLog().debug("findAllByPartialLabel successfull.");
			return resourceList;
		} catch (RuntimeException re) {
			getLog().error("findAllByPartialLabel failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T findByExternalId(Long externalId) {
		getLog().debug("Finding Resource instance with externalId: " + externalId);
		try {
			Query query = getEm().createQuery("select r from "
					// + entityClass.getSimpleName()
					+ this.getDomainClass().getSimpleName() + " r where r.externalId like :externalId");
			query.setParameter("externalId", externalId);
			T resource = (T) query.getSingleResult();
			getLog().debug("FindByExternalId successfull.");
			return resource;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("FindByExternalId returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("FindByExternalId failed.", re);
			throw re;
		}
	}
}
