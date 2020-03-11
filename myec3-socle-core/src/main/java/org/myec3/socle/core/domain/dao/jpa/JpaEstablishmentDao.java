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
import org.myec3.socle.core.domain.dao.EstablishmentDao;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.springframework.stereotype.Repository;

/**
 * This interface define methods to perform specific queries on
 * {@link Establishment} objects. It only provides new specific methods and
 * inherits methods from {@link JpaResourceDao}.
 * 
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 */
@Repository("establishmentDao")
public class JpaEstablishmentDao extends JpaResourceDao<Establishment> implements EstablishmentDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Establishment> findAllEstablishments(Company company) {

		this.getLog().debug("Finding all Establishment instances with Company: " + company);
		try {
			Query q = this.getEm().createQuery(
					"SELECT r FROM " + this.getDomainClass().getSimpleName() + " r WHERE r.company LIKE :company");
			q.setParameter("company", company);
			List<Establishment> results = q.getResultList();
			getLog().debug("findAllEstablishments successfull.");
			return results;
		} catch (NoResultException nre) {
			getLog().warn("findAllEstablishments returned no results.");
			return new ArrayList<Establishment>();
		} catch (RuntimeException re) {
			getLog().error("findAllEstablishments failed.", re);
			return new ArrayList<Establishment>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Establishment findHeadOfficeEstablishment(Company company) {
		this.getLog().debug("Finding Head Office Establishment instance with Company " + company);
		try {
			Query q = this.getEm().createQuery("SELECT r FROM " + this.getDomainClass().getSimpleName()
					+ " r WHERE r.company LIKE :company AND r.isHeadOffice = 1");

			q.setParameter("company", company);
			Establishment result = (Establishment) q.getSingleResult();
			getLog().debug("findHeadOfficeEstablishment successfull.");
			return result;
		} catch (NoResultException nre) {
			getLog().warn("findHeadOfficeEstablishment returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findHeadOfficeEstablishment failed.", re);
			return null;
		}
	}

	public List<Establishment> getEstablishmentToUpdate() {
		this.getLog().debug("Finding old Establishment that need update");
		try {

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, Integer.parseInt(MyEc3MpsUpdateConstants.getMonth()));
			SimpleDateFormat formatForBdd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			formatForBdd.setCalendar(cal);

			Query q = this.getEm()
					.createQuery("SELECT r FROM " + this.getDomainClass().getSimpleName()
							+ " r WHERE r.lastUpdate < :lastUpdateLimit OR r.lastUpdate is null"
							+ " AND r.foreignIdentifier = 0");
			q.setParameter("lastUpdateLimit", formatForBdd.getCalendar().getTime());
			List<Establishment> results = q.getResultList();
			getLog().debug("getEstablishmentToUpdate successfull.");
			return results;
		} catch (NoResultException nre) {
			getLog().warn("getCompanyToUpdate returned no results.");
			return new ArrayList<Establishment>();
		} catch (RuntimeException re) {
			getLog().error("getCompanyToUpdate failed.", re);
			return new ArrayList<Establishment>();
		}
	}

	public List<Establishment> getEstablishmentToUpdateByCompany(Company company) {
		this.getLog().debug("Finding old Establishment that need update, by Company");
		try {

			Calendar cal = Calendar.getInstance();

			// TODO : add property for month
			cal.add(Calendar.MONTH, Integer.parseInt(MyEc3MpsUpdateConstants.getMonth()));
			SimpleDateFormat formatForBdd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			formatForBdd.setCalendar(cal);

			Query q = this.getEm().createQuery("SELECT r FROM " + this.getDomainClass().getSimpleName()
					+ " r WHERE (r.lastUpdate < :lastUpdateLimit OR lastUpdate is null) AND r.company = :company AND LENGTH(r.siret)=14");
			q.setParameter("lastUpdateLimit", formatForBdd.getCalendar().getTime());
			q.setParameter("company", company);
			List<Establishment> results = q.getResultList();
			getLog().debug("getEstablishmentToUpdate successfull.");
			return results;
		} catch (NoResultException nre) {
			getLog().warn("getCompanyToUpdate returned no results.");
			return new ArrayList<Establishment>();
		} catch (RuntimeException re) {
			getLog().error("getCompanyToUpdate failed.", re);
			return new ArrayList<Establishment>();
		}
	}

	public List<Establishment> getEstablishmentManualUpdateByCompany(Company company) {
		this.getLog().debug("Finding old Establishment that need update, by Company");
		try {

			Calendar cal = Calendar.getInstance();

			// TODO : add property for month
			cal.add(Calendar.MONTH, Integer.parseInt(MyEc3MpsUpdateConstants.getMonth()));
			SimpleDateFormat formatForBdd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			formatForBdd.setCalendar(cal);

			Query q = this.getEm().createQuery("SELECT r FROM " + this.getDomainClass().getSimpleName()
					+ " r WHERE r.company = :company AND LENGTH(r.siret)=14");
			q.setParameter("company", company);
			List<Establishment> results = q.getResultList();
			getLog().debug("getEstablishmentManualUpdate successfull.");
			return results;
		} catch (NoResultException nre) {
			getLog().warn("getEstablishmentManualUpdate returned no results.");
			return new ArrayList<Establishment>();
		} catch (RuntimeException re) {
			getLog().error("getEstablishmentManualUpdate failed.", re);
			return new ArrayList<Establishment>();
		}
	}

	public Establishment findLastForeignCreatedDao(String nationalId) {
		this.getLog().debug("Finding last foreign Establishment instance with nationalId " + nationalId);
		try {
			Query q = this.getEm().createQuery("SELECT r FROM " + this.getDomainClass().getSimpleName()
					+ " r WHERE r.nationalID = :nationalId order by lastUpdate desc");

			q.setParameter("nationalId", nationalId);
			q.setMaxResults(1);
			Establishment result = (Establishment) q.getSingleResult();
			getLog().debug("findLastForeignCreatedDao successfull.");
			return result;
		} catch (NoResultException nre) {
			getLog().warn("findLastForeignCreatedDao returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findLastForeignCreatedDao failed.", re);
			return null;
		}
	}

	public Establishment findByNic(String siren, String nic) {
		this.getLog().debug("Finding Establishment with following SIREN/NIC : " + siren + " / " + nic);
		String siret = siren + nic;
		try {
			Query q = this.getEm().createQuery(
					"SELECT r FROM " + this.getDomainClass().getSimpleName() + " r WHERE r.siret = :siret");

			q.setParameter("siret", siret);
			Establishment result = (Establishment) q.getSingleResult();
			getLog().debug("findByNic successfull.");
			return result;
		} catch (NoResultException nre) {
			getLog().warn("findByNic returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findByNic failed.", re);
			return null;
		}
	}

	@Override
	public Establishment findEstablishmentyIdSdm(long idSdm) {
		Query q = this.getEm().createQuery("select c from " + this.getDomainClass().getSimpleName() + " c" + " JOIN SynchroIdentifiantExterne s on c.id=s.idSocle"
				+ " WHERE s.idAppliExterne= :idSdm AND s.typeRessource= :typeResource");
		q.setParameter("idSdm", idSdm);
		q.setParameter("typeResource", ResourceType.ESTABLISHMENT);
		try{
			//Company results = (Company) q.getSingleResult();
			Establishment result = (Establishment) q.getSingleResult();
			getLog().debug("findEstablishmentyIdSdm successfull.");
			return result;
		} catch (NoResultException nre) {
			getLog().warn("findEstablishmentyIdSdm returned no results.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findEstablishmentyIdSdm failed.", re);
			return null;
		}
	}

	@Override
	public Class<Establishment> getType() {
		return Establishment.class;
	}
}
