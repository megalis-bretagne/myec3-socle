package org.myec3.socle.core.service;

import org.myec3.socle.core.domain.model.InseeRegion;

public interface InseeRegionService extends IGenericService<InseeRegion> {

	public InseeRegion findById(Long regionId);
}
