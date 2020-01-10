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

import org.myec3.socle.core.domain.dao.CompanyDepartmentDao;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.springframework.stereotype.Repository;

/**
 * This interface define methods to perform specific queries on
 * {@link CompanyDepartment} objects. It only provides new specific methods and
 * inherits methods from {@link JpaResourceDao}.
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Repository("companyDepartmentDao")
public class JpaCompanyDepartmentDao extends JpaResourceDao<CompanyDepartment> implements CompanyDepartmentDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<CompanyDepartment> findAllDepartmentByCompany(Company company) {

		this.getLog().debug("Finding all Resource instances with Company: " + company);
		try {
			Query query = this.getEm().createQuery(
					"select r from " + this.getDomainClass().getSimpleName() + " r where r.company like :company");
			query.setParameter("company", company);
			List<CompanyDepartment> resourceList = query.getResultList();
			getLog().debug("findAllDepartmentByCompanyId successfull.");
			return resourceList;
		} catch (NoResultException re) {
			return new ArrayList<CompanyDepartment>();
		} catch (RuntimeException re) {
			getLog().error("findAllDepartmentByCompany failed.", re);
			return new ArrayList<CompanyDepartment>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompanyDepartment findRootCompanyDepartmentByCompany(Company company) {
		this.getLog().debug("Finding root CompanyDepartment instance with Company " + company);
		try {
			Query query = this.getEm().createQuery("select d from " + this.getDomainClass().getSimpleName()
					+ " d where d.company = :company and d.parentDepartment is null");
			query.setParameter("company", company);

			CompanyDepartment rootCompanyDepartment = (CompanyDepartment) query.getSingleResult();
			getLog().debug("findRootCompanyDepartmentByCompany successfull.");
			return rootCompanyDepartment;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findRootCompanyDepartmentByCompany returned no result.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findRootCompanyDepartmentByCompany failed.", re);
			return null;
		}
	}

	@Override
	public Class<CompanyDepartment> getType() {
		return CompanyDepartment.class;
	}
}
