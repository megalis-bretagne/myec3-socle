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
package org.myec3.socle.core.service;

import java.util.List;

import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.MpsUpdateJob;
import org.myec3.socle.core.domain.model.enums.MpsUpdateTypeValue;

/**
 * Interface defining Business Services methods and providing
 * {@link Establishment} specific operations. This interface extends the
 * common {@link ResourceService} interface by adding new specific methods
 * 
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 */
public interface EstablishmentService extends ResourceService<Establishment> {
		
	/**
	 * Find all {@link Establishment} of a {@link Company}
	 * 
	 * @param company
	 *            : company to search on
	 * @return the list of the {@link Establishment} of this company. Return
	 *         an empty list if no result or in case of error.
	 * @throws IllegalArgumentException
	 *             if company is null.
	 */
	List<Establishment> findAllEstablishmentsByCompany(Company company);
	
	/**
	 * Find the head office {@link Establishment} of a {@link Company}.
	 * 
	 * @param company
	 *            : company to search on
	 * @return the head office {@link Establishment} of this company. Return null
	 *         if no result or in case of error.
	 * @throws IllegalArgumentException
	 *             if company is null.
	 */
	Establishment findHeadOfficeEstablishmentByCompany(Company company);
	
	/**
	 * This method allows to populate establishment's collections
	 * 
	 * @param establishment's
	 *            : establishment's object that we want to populate
	 * @throws IllegalArgumentException
	 *             if establishment's is null.
	 */
	void populateCollections(Establishment establishment);
	
	/**
	 * This method allows to clean establishment's collections
	 * 
	 * @param establishment
	 *            : establishment to clean
	 * 
	 * @throws IllegalArgumentException
	 *             if establishment is null
	 */
	void cleanCollections(Establishment establishment);
	
	/**
	 * {@inheritDoc}
	 * 
	 * <br>
	 * 
	 * If the {@link Etablishment} is its {@link Company}'s head office,
	 * the following {@link Company}'s informations will be updated :
	 * <ul>
	 * 	<li>nic</li>
	 * 	<li>address</li>
	 * 	<li>phone</li>
	 * 	<li>fax</li>
	 * 	<li>email</li>
	 * </ul>
	 * 
	 * @throws IllegalArgumentException
	 *             if establishment has no Company associated
	 */
	@Override
	Establishment update(Establishment resource);
	
	List<MpsUpdateJob> getEstablishmentToUpdate();
	
	List<MpsUpdateJob> getEstablishmentToUpdateByCompany(Company company, MpsUpdateTypeValue updateType);
	
	Establishment findLastForeignCreated(String nationalId);
	
	Establishment findByNic(String siren, String nic);
}
