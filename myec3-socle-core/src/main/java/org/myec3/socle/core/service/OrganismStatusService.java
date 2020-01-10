package org.myec3.socle.core.service;

import java.util.Set;

import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismStatus;

public interface OrganismStatusService extends IGenericService<OrganismStatus> {

	/**
	 * Find all {@link OrganismStatus} of a {@link Organism}.
	 *
	 * @param organism : organism to search on
	 * @return the list of the {@link OrganismStatus} of this company. return an
	 *         empty list if no result or in case of error.
	 * @throws IllegalArgumentException if organism is null.
	 */
	public Set<OrganismStatus> findAllOrganismStatusByOrganism(Organism organism);

}
