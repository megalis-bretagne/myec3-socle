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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.constants.MyEc3MpsUpdateConstants;
import org.myec3.socle.core.domain.dao.CompanyDao;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.springframework.stereotype.Repository;

/**
 * This interface define methods to perform specific queries on {@link Company}
 * objects. It only provides new specific methods and inherits methods from
 * {@link JpaGenericStructureDao}.
 *
 * @author Matthieu Dubromez <matthieu.dubromez@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 */
@Repository("companyDao")
public class JpaCompanyDao extends JpaGenericStructureDao<Company> implements CompanyDao {

	@SuppressWarnings("unchecked")
	public List<Long> getCompanyToUpdate() {
		this.getLog().debug("Finding old Company that need update");
		try {

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, Integer.parseInt(MyEc3MpsUpdateConstants.getMonth()));
			SimpleDateFormat formatForBdd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			formatForBdd.setCalendar(cal);

			Query q = this.getEm().createQuery("SELECT r.id FROM " + this.getDomainClass().getSimpleName()
					+ " r WHERE r.lastUpdate < :lastUpdateLimit" + " AND r.foreignIdentifier = 0");
			q.setParameter("lastUpdateLimit", formatForBdd.getCalendar().getTime());
			List<Long> results = q.getResultList();
			getLog().debug("getCompanyToUpdate successfull.");
			return results;
		} catch (NoResultException nre) {
			getLog().warn("getCompanyToUpdate returned no results.");
			return new ArrayList<Long>();
		} catch (RuntimeException re) {
			getLog().error("getCompanyToUpdate failed.", re);
			return new ArrayList<Long>();
		}
	}

	@Override
	public List<Company> findAllByCriteria(String label, String acronym, String siren, String postalCode, String city) {

		getLog().debug("Finding all Structures by Label, Acronym, Siren, PostalCode, City");
		try {

			Boolean hasCriteria = Boolean.FALSE;
			StringBuffer queryString = new StringBuffer(
					"select c from " + this.getDomainClass().getSimpleName() + " c");
			if ((null != label) || (null != acronym) || (null != siren) || (null != postalCode) || (null != city)) {
				queryString.append(" where ");

				if (null != label) {
					queryString.append("UPPER(c.label) like UPPER(:label)");
					hasCriteria = Boolean.TRUE;
				}
				if (null != acronym) {
					if (hasCriteria) {
						queryString.append(" and ");
					} else {
						hasCriteria = Boolean.TRUE;
					}
					queryString.append("c.companyAcronym like :acronym");
				}
				if (null != siren) {
					if (hasCriteria) {
						queryString.append(" and ");
					} else {
						hasCriteria = Boolean.TRUE;
					}
					queryString.append("c.siren like :siren");
				}
				if (null != postalCode) {
					if (hasCriteria) {
						queryString.append(" and ");
					} else {
						hasCriteria = Boolean.TRUE;
					}
					queryString.append("c.address.postalCode like :postalCode");
				}
				if (null != city) {
					if (hasCriteria) {
						queryString.append(" and ");
					} else {
						hasCriteria = Boolean.TRUE;
					}
					queryString.append("UPPER(c.address.city) like UPPER(:city)");
				}
			}

			Query query = getEm().createQuery(queryString.toString());
			if (null != label) {
				query.setParameter("label", "%" + label + "%");
			}
			if (null != acronym) {
				query.setParameter("acronym", "%" + acronym + "%");
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

			List<Company> structureList = query.getResultList();
			getLog().debug("findAllByCriteria successfull.");
			return structureList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findAllByCriteria returned no result.");
			return new ArrayList<Company>();
		} catch (RuntimeException re) {
			getLog().error("findAllByCriteria failed.", re);
			throw re;
		}
	}

	@Override
	public Company findCompanyByIdSdm(long idSdm) {
		Query q = this.getEm().createQuery("select c from " + this.getDomainClass().getSimpleName() + " c" + " JOIN SynchroIdentifiantExterne s on c.id=s.idSocle"
				+ " WHERE s.idAppliExterne= :idSdm AND s.typeRessource= :typeResource");
		q.setParameter("idSdm", idSdm);
		q.setParameter("typeResource", ResourceType.COMPANY);
		try{

			//Company results = (Company) q.getSingleResult();
			Company result = (Company) q.getSingleResult();


			getLog().debug("getCompanyToUpdate successfull.");
			return result;


		} catch (NoResultException nre) {
			getLog().warn("findCompanyByIdSdm returned no results.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findCompanyByIdSdm failed.", re);
			return null;
		}
	}

	@Override
	public Class<Company> getType() {
		return Company.class;
	}

}
