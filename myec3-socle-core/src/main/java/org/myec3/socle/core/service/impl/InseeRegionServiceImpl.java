package org.myec3.socle.core.service.impl;

import org.myec3.socle.core.domain.dao.InseeRegionDao;
import org.myec3.socle.core.domain.model.InseeRegion;
import org.myec3.socle.core.service.InseeRegionService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("inseeRegionService")
public class InseeRegionServiceImpl extends AbstractGenericServiceImpl<InseeRegion, InseeRegionDao>
		implements InseeRegionService {

	@Override
	public InseeRegion findById(Long regionId) {
		Assert.notNull(regionId, "regionId is null. null value is forbiden");
		return this.dao.findById(regionId);
	}
}