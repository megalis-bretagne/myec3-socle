package org.myec3.socle.core.domain.dao;

import org.myec3.socle.core.domain.model.InseeRegion;

public interface InseeRegionDao extends NoResourceGenericDao<InseeRegion> {
	public InseeRegion findById(Long regionId);
}
