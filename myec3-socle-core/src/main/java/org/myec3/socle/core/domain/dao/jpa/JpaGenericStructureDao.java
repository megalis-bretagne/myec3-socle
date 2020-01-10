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

import org.myec3.socle.core.domain.dao.GenericStructureDao;
import org.myec3.socle.core.domain.model.Structure;

/**
 * This class implements methods to perform queries on {@link Structure} objects
 * or objects extending structures.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T> instance, child of Profile to query on
 */
public abstract class JpaGenericStructureDao<T extends Structure> extends JpaResourceDao<T>
		implements GenericStructureDao<T> {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAllByCriteria(String label, String siren, String postalCode, String city) {

		getLog().debug("Finding all Structures by Label, Siren, PostalCode, City");
		try {

			Boolean hasCriteria = Boolean.FALSE;
			StringBuffer queryString = new StringBuffer(
					"select s from " + this.getDomainClass().getSimpleName() + " s");
			if ((null != label) || (null != siren) || (null != postalCode) || (null != city)) {
				queryString.append(" where ");

				if (null != label) {
					queryString.append("UPPER(s.label) like UPPER(:label)");
					hasCriteria = Boolean.TRUE;
				}
				if (null != siren) {
					if (hasCriteria) {
						queryString.append(" and ");
					} else {
						hasCriteria = Boolean.TRUE;
					}
					queryString.append("s.siren like :siren");
				}
				if (null != postalCode) {
					if (hasCriteria) {
						queryString.append(" and ");
					} else {
						hasCriteria = Boolean.TRUE;
					}
					queryString.append("s.address.postalCode like :postalCode");
				}
				if (null != city) {
					if (hasCriteria) {
						queryString.append(" and ");
					} else {
						hasCriteria = Boolean.TRUE;
					}
					queryString.append("UPPER(s.address.city) like UPPER(:city)");
				}
			}

			Query query = getEm().createQuery(queryString.toString());
			if (null != label) {
				query.setParameter("label", "%" + label + "%");
			}
			if (null != siren) {
				query.setParameter("siren", siren + "%");
			}
			if (null != postalCode) {
				query.setParameter("postalCode", postalCode + "%");
			}
			if (null != city) {
				query.setParameter("city", city);
			}

			List<T> structureList = query.getResultList();
			getLog().debug("findAllByCriteria successfull.");
			return structureList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findAllByCriteria returned no result.");
			return new ArrayList<T>();
		} catch (RuntimeException re) {
			getLog().error("findAllByCriteria failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T findBySiren(String siren) {
		getLog().debug("Finding a Structure by Siren");
		try {
			Query query = getEm().createQuery(
					"select s from " + this.getDomainClass().getSimpleName() + " s " + "where s.siren = :siren");
			query.setParameter("siren", siren);
			T foundStructure = (T) query.getSingleResult();
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

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T findByAcronym(String acronym) {
		getLog().debug("Finding a Structure by Acronym");
		try {
			Query query = getEm().createQuery(
					"select s from " + this.getDomainClass().getSimpleName() + " s " + "where s.acronym = :acronym");
			query.setParameter("acronym", acronym);
			T foundStructure = (T) query.getSingleResult();
			getLog().debug("findByAcronym successfull.");
			return foundStructure;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findByAcronym returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findByAcronym failed.", re);
			throw re;
		}
	}

}
