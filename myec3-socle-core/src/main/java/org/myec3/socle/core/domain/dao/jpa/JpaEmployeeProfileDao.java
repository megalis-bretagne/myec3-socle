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

import org.myec3.socle.core.domain.dao.EmployeeProfileDao;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.enums.RoleProfile;
import org.springframework.stereotype.Repository;

/**
 * This interface define methods to perform specific queries on
 * {@link EmployeeProfile} objects. It only provides new specific methods and
 * inherits methods from {@link JpaGenericProfileDao}.
 * 
 * @author Matthieu Dubromez <matthieu.dubromez@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Repository("employeeProfileDao")
public class JpaEmployeeProfileDao extends JpaGenericProfileDao<EmployeeProfile> implements EmployeeProfileDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<EmployeeProfile> findAllEmployeeByCompanyDepartment(CompanyDepartment companyDepartment) {
		getLog().debug("Finding all EmployeeProfile by CompanyDepartment " + companyDepartment);
		try {
			Query query = getEm().createQuery("select e from " + this.getDomainClass().getSimpleName() + " e "
					+ "inner join e.companyDepartment c where c = :companyDepartment and e.enabled = :enabled");
			query.setParameter("companyDepartment", companyDepartment);
			query.setParameter("enabled", Boolean.TRUE);
			List<EmployeeProfile> employeeProfileList = query.getResultList();
			getLog().debug("findAllEmployeeByCompanyDepartment successfull.");
			return employeeProfileList;
		} catch (NoResultException re) {
			// No result found, we return empty list instead of errors
			getLog().warn("findAllEmployeeByCompanyDepartment returned no result.");
			return new ArrayList<EmployeeProfile>();
		} catch (RuntimeException re) {
			getLog().error("findAllEmployeeByCompanyDepartment failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<EmployeeProfile> findAllByEstablishment(Establishment establishment) {
		getLog().debug("Finding all EmployeeProfile by Establishment " + establishment);
		try {
			Query query = getEm().createQuery("select e from " + this.getDomainClass().getSimpleName() + " e "
					+ "inner join e.establishment es where es = :establishment and e.enabled = :enabled");
			query.setParameter("establishment", establishment);
			query.setParameter("enabled", Boolean.TRUE);
			List<EmployeeProfile> employeeProfileList = query.getResultList();
			getLog().debug("findAllByEstablishment successfull.");
			return employeeProfileList;
		} catch (NoResultException re) {
			// No result found, we return empty list instead of errors
			getLog().warn("findAllByEstablishment returned no result.");
			return new ArrayList<EmployeeProfile>();
		} catch (RuntimeException re) {
			getLog().error("findAllByEstablishment failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<EmployeeProfile> findAllGuAdministratorByCompanyId(Long companyId) {
		this.getLog().debug("Finding GU administrators for the Company with UID: " + companyId);
		try {
			Query query = getEm().createQuery("SELECT ep FROM " + this.getDomainClass().getSimpleName() + " ep "
					+ "LEFT JOIN ep.roles r WHERE r.name = '" + RoleProfile.ROLE_MANAGER_EMPLOYEE
					+ "' AND ep.companyDepartment.id in ("
					+ "SELECT c.id from CompanyDepartment c where c.company.id = :companyId) ");
			query.setParameter("companyId", companyId);
			List<EmployeeProfile> employeeProfileList = query.getResultList();

			this.getLog().debug("findAllGuAdministratorByCompanyId successfull.");
			return employeeProfileList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			this.getLog().warn("findAllGuAdministratorByCompanyId returned no result.");
			return new ArrayList<EmployeeProfile>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllGuAdministratorByCompanyId failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EmployeeProfile> findAllGuAdministratorEnabledByCompanyId(Long companyId) {
		this.getLog().debug("Finding GU administrators enabled for the Company with UID: " + companyId);
		try {
			Query query = getEm().createQuery("SELECT ep FROM " + this.getDomainClass().getSimpleName() + " ep "
					+ "LEFT JOIN ep.roles r WHERE r.name = '" + RoleProfile.ROLE_MANAGER_EMPLOYEE + "' AND ep.enabled="
					+ Boolean.TRUE + " AND ep.companyDepartment.id in ("
					+ "SELECT c.id from CompanyDepartment c where c.company.id = :companyId) ");
			query.setParameter("companyId", companyId);
			List<EmployeeProfile> employeeProfileList = query.getResultList();

			this.getLog().debug("findAllGuAdministratorEnabledByCompanyId successfull.");
			return employeeProfileList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			this.getLog().warn("findAllGuAdministratorEnabledByCompanyId returned no result.");
			return new ArrayList<EmployeeProfile>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllGuAdministratorEnabledByCompanyId failed.", re);
			return null;
		}
	}

	@Override
	public Class<EmployeeProfile> getType() {
		return EmployeeProfile.class;
	}
}
