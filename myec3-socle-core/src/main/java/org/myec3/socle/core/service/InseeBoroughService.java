package org.myec3.socle.core.service;

import org.myec3.socle.core.domain.model.InseeBorough;

public interface InseeBoroughService extends IGenericService<InseeBorough> {

	public InseeBorough findBourough(Long regionId, String countyId, Long arId);

}