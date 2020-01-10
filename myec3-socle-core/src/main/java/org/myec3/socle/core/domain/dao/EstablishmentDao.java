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
package org.myec3.socle.core.domain.dao;

import java.util.List;

import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;

/**
 * DAO interface for {@link Establishment} objects. This interface defines
 * global methods that could be called on {@link Establishment}
 * 
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 */
public interface EstablishmentDao extends ResourceDao<Establishment> {
	
	/**
	 * Find all {@link Establishment} of a {@link Company}.
	 * 
	 * @param company
	 *            : company to search on
	 * @return the list of the {@link Establishment} of this company. Return
	 *         an empty list if no result or in case of error.
	 */
	List<Establishment> findAllEstablishments(Company company);
	
	/**
	 * Find the head office {@link Establishment} of a {@link Company}.
	 * 
	 * @param company
	 *            : company to search on
	 * @return the head office {@link Establishment} of this company. Return null
	 *         if no result or in case of error.
	 */
	Establishment findHeadOfficeEstablishment(Company company);
	
	List<Establishment> getEstablishmentToUpdate();
	
	List<Establishment> getEstablishmentToUpdateByCompany(Company company);
	
	List<Establishment> getEstablishmentManualUpdateByCompany(Company company);
	
	Establishment findLastForeignCreatedDao(String nationalId);
	
	Establishment findByNic(String siren, String nic);

}
