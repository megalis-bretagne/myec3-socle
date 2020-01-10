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

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.InseeGeoCodeDao;
import org.myec3.socle.core.domain.model.InseeGeoCode;
import org.springframework.stereotype.Repository;

/**
 * This interface define methods to perform specific queries on
 * {@link InseeGeoCode} objects. It only provides new specific methods and
 * inherits methods from {@link JpaResourceDao}.
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 */
@Repository("inseeGeoCodeDao")
public class JpaInseeGeoCodeDao extends JpaResourceDao<InseeGeoCode> implements InseeGeoCodeDao {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<InseeGeoCode> findByPostalCode(String postalCode) {
		getLog().debug("Finding an InseeGeoCode by postalCode");
		try {
			Query query = getEm().createQuery("select i from " + this.getDomainClass().getSimpleName() + " i "
					+ "where i.postalCode = :postalCode");
			query.setParameter("postalCode", postalCode);
			List<InseeGeoCode> findByPostalCode = (List<InseeGeoCode>) query.getResultList();
			getLog().debug("findByPostalCode successfull.");
			return findByPostalCode;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findByPostalCode returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findByPostalCode failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public InseeGeoCode findByInseeCode(String inseeCode) {
		getLog().debug("Finding an InseeGeoCode by inseeCode");
		try {
			Query query = getEm().createQuery("select i from " + this.getDomainClass().getSimpleName() + " i "
					+ "where i.inseeCode = :inseeCode");
			query.setParameter("inseeCode", inseeCode);
			List<InseeGeoCode> findByInseeCode = (List<InseeGeoCode>) query.getResultList();
			getLog().debug("findByInseeCode successfull.");
			if (findByInseeCode.size() != 0) {
				return findByInseeCode.get(0);
			} else {
				return null;
			}
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findByInseeCode returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findByInseeCode failed.", re);
			throw re;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public InseeGeoCode findByPostalCodeAndCity(String postalCode, String city) {
		getLog().debug("Finding an InseeGeoCode by inseeCode");
		try {
			Query query = getEm().createQuery("select i from " + this.getDomainClass().getSimpleName() + " i "
					+ "where i.postalCode = :postalCode and i.label = :city");
			query.setParameter("postalCode", postalCode);
			query.setParameter("city", city.toUpperCase());
			List<InseeGeoCode> findByInseeCode = (List<InseeGeoCode>) query.getResultList();
			getLog().debug("findByInseeCode successfull.");
			if (findByInseeCode.size() != 0) {
				return findByInseeCode.get(0);
			} else {
				return null;
			}
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findByInseeCode returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findByInseeCode failed.", re);
			throw re;
		}
	}

	@Override
	public Class<InseeGeoCode> getType() {
		return InseeGeoCode.class;
	}

}
