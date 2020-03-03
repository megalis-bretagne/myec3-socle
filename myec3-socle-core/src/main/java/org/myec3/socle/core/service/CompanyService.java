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
import org.myec3.socle.core.domain.model.MpsUpdateJob;
import org.myec3.socle.core.service.exceptions.CompanyCreationException;

/**
 * Interface defining Business Services methods and providing {@link Company}
 * specific operations. This interface extends the common
 * {@link ResourceService} interface by adding new specific methods
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 */
public interface CompanyService extends GenericStructureService<Company> {

	/**
	 * Extends base create method to prive specific Company Business. <br>
	 * 
	 * After creation, the company and a root department will be created. <br>
	 * 
	 * They are then associated to the new company. <br>
	 * 
	 * @param company : company to create
	 * @throws IllegalArgumentException if company is null
	 * @throws CompanyCreationException in case of technical errors
	 */
	@Override
	void create(Company company) throws CompanyCreationException;

	/**
	 * Check if siren is valid depending on siren an nic
	 * 
	 * @param siren : siren to check
	 * @param nic   : nic to check
	 * @return TRUE if siren is valid, FALSE otherwise or if siren or nic is null
	 */
	Boolean isSiretValid(String siren, String nic);

	/**
	 * This method allows to populate company's collections
	 * 
	 * @param company : company to populate
	 * 
	 * @throws IllegalArgumentException if company is null
	 */
	void populateCollections(Company company);

	/**
	 * This method allows to clean company's collections
	 * 
	 * @param company : company to clean
	 * 
	 * @throws IllegalArgumentException if company is null
	 */
	void cleanCollections(Company company);

	List<MpsUpdateJob> getCompanyToUpdate();

	public void findAllPersonByCompany(Company company);

	List<Company> findAllByCriteria(String label, String acronym, String siren, String postalCode, String city);

	Company findCompanyByIdSdm(long idSdm);

}
