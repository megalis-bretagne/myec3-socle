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

import org.myec3.socle.core.domain.dao.OrganismDao;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Organism;
import org.springframework.stereotype.Repository;

/**
 * Provide specific implementations to perform operations on {@link Organism}
 * objects
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Repository("organismDao")
public class JpaOrganismDao extends JpaGenericStructureDao<Organism> implements OrganismDao {

	@Override
	@SuppressWarnings("unchecked")
	public List<Organism> findAllByCriteria(String label, String siren, String postalCode, String city,
			Customer customer) {

		getLog().debug("Finding all organisms by label, siren, postal code, city and customer");
		try {

			Boolean hasCriteria = Boolean.FALSE;
			StringBuffer queryString = new StringBuffer(
					"SELECT s FROM " + this.getDomainClass().getSimpleName() + " s ");
			if ((null != label) || (null != siren) || (null != postalCode) || (null != city) || (null != customer)) {

				if (null != customer) {
					queryString.append("inner join s.customer c");
				}

				queryString.append(" WHERE ");

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
				if (null != customer) {
					if (hasCriteria) {
						queryString.append(" and ");
					}
					queryString.append("c = :customer");
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
			if (null != customer) {
				query.setParameter("customer", customer);
			}

			List<Organism> organismList = query.getResultList();
			getLog().debug("findAllByCriteria successfull.");
			return organismList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findAllByCriteria returned no result.");
			return new ArrayList<Organism>();
		}
	}

	@Override
	public Class<Organism> getType() {
		return Organism.class;
	}
}
