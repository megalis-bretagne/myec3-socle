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

import org.myec3.socle.core.domain.dao.PersonDao;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Person;
import org.springframework.stereotype.Repository;

/**
 * This implementation provide methods to perform specific queries on
 * {@link Person} objects. It only provides new specific methods and inherits
 * methods from {@link JpaResourceDao}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Repository("personDao")
public class JpaPersonDao extends JpaResourceDao<Person> implements PersonDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Person> findAllPersonByCompany(Company company) {
		getLog().debug("Finding all Person by Company " + company.getName());
		try {
			Query query = getEm().createQuery(
					"select p from " + this.getDomainClass().getSimpleName() + " p where p.company = :company");
			query.setParameter("company", company);
			List<Person> personList = query.getResultList();
			getLog().debug("findAllPersonByCompany successfull.");
			return personList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findAllPersonByCompany returned no result.");
			return new ArrayList<Person>();
		} catch (RuntimeException re) {
			getLog().error("findAllPersonByCompany failed.", re);
			return null;
		}
	}

	@Override
	public Class<Person> getType() {
		return Person.class;
	}
}
