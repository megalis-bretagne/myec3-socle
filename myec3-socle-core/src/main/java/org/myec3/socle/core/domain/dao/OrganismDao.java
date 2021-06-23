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

import org.myec3.socle.core.domain.dto.OrganismLightDTO;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Organism;

import java.util.List;

/**
 * This interface define methods to perform specific queries on {@link Organism}
 * objects. It only defines new specific methods and inherits methods from
 * {@link ResourceDao}.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface OrganismDao extends GenericStructureDao<Organism> {

	/**
	 * Retrieve all structures depending on the filters defined in parameters.
	 * If all parameters are null, retrieve all organisms.
	 * 
	 * @param label
	 *            : Organism label to find. If this parameter is not null,
	 *            filters on all structures with the same label. Case no
	 *            matters.
	 * @param siren
	 *            : Organism siren to find. If this parameter is not null,
	 *            filters on all structures with exactly the same siren.
	 * @param postalCode
	 *            : Organism postalCode to find. If this parameter is not null,
	 *            filters on all structures with exactly the same postalCode.
	 * @param city
	 *            : Organism city to find. If this parameter is not null,
	 *            filters on all organisms with the same city. Case no matters
	 * @param customer
	 *            : Organism customer to find. Only for Organism structure
	 *            because companies don't have a customer until now.If this
	 *            parameter is not null, filters on all structures with the same
	 *            customer. Case no matters
	 * @return the list of all matching Organisms. Return an empty list if
	 *         there's no result. Return null in case of errors.
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<Organism> findAllByCriteria(String label, String siren,
			String postalCode, String city, Customer customer);

	/**
	 * Retrive all organism
	 * @return	list of organism Light
	 */
	List<OrganismLightDTO> findOrganismLight();

	Organism findOrganismByIdSdm(long idSdm);
}