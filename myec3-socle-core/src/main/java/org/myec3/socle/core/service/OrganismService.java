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

import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.service.exceptions.AllAcronymsUsedException;
import org.myec3.socle.core.service.exceptions.OrganismCreationException;

/**
 * Interface defining Business Services methods and providing {@link Organism}
 * specific operations. This interface extends the common
 * {@link GenericStructureService} interface by adding new specific methods
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface OrganismService extends GenericStructureService<Organism> {

	/**
	 * Extends base create method to prive specific Organism Business. Creation will
	 * affect a new free acronym to the new organsism, if its not already done.
	 * After this operation, the organism will be created and a root department will
	 * be created and then associated to the new organism.
	 * 
	 * @param organism : organism to create
	 * @throws IllegalArgumentException  if organism is null
	 * @throws AllAcronymsUsedException  if no acronym is free anymore
	 * @throws OrganismCreationException in case of technical errors
	 */
	@Override
	void create(Organism organism) throws AllAcronymsUsedException, OrganismCreationException;

	/**
	 * Retrieve all structures depending on the filters defined in parameters. If
	 * all parameters are null, retrieve all organisms.
	 * 
	 * @param label      : Organism label to find. If this parameter is not null,
	 *                   filters on all structures with the same label. Case no
	 *                   matters.
	 * @param siren      : Organism siren to find. If this parameter is not null,
	 *                   filters on all structures with exactly the same siren.
	 * @param postalCode : Organism postalCode to find. If this parameter is not
	 *                   null, filters on all structures with exactly the same
	 *                   postalCode.
	 * @param city       : Organism city to find. If this parameter is not null,
	 *                   filters on all organisms with the same city. Case no
	 *                   matters
	 * @param customer   : Organism customer to find. Only for Organism structure
	 *                   because companies don't have a customer until now.If this
	 *                   parameter is not null, filters on all structures with the
	 *                   same customer. Case no matters
	 * @return the list of all matching Organisms. Return an empty list if there's
	 *         no result. Return null in case of errors.
	 * @throws RuntimeException in case of errors
	 */
	List<Organism> findAllByCriteria(String label, String siren, String postalCode, String city, Customer customer);

	/**
	 * This method allows to populate organism's collections
	 * 
	 * @param organism : organism to populate
	 * 
	 * @throws IllegalArgumentException if organism is null
	 */
	void populateCollections(Organism organism);

	/**
	 * This method allows to clean organism's collections (before marshalling object
	 * for send the xml into the JMS message)
	 * 
	 * @param organism : organism to clean
	 * 
	 * @throws IllegalArgumentException if organism is null
	 */
	void cleanCollections(Organism organism);


	Organism findOrganismByIdSdm(long idSdm);
}
