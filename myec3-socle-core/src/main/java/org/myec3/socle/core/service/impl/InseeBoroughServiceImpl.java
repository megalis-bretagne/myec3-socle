package org.myec3.socle.core.service.impl;

import org.myec3.socle.core.domain.dao.InseeBoroughDao;
import org.myec3.socle.core.domain.model.InseeBorough;
import org.myec3.socle.core.service.InseeBoroughService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("inseeBoroughService")
public class InseeBoroughServiceImpl extends AbstractGenericServiceImpl<InseeBorough, InseeBoroughDao>
		implements InseeBoroughService {

	public InseeBorough findBourough(Long regionId, String countyId, Long arId) {
		Assert.notNull(regionId, "Region Id is mandatory, it cannot be null");
		Assert.notNull(countyId, "Department Id is mandatory, it cannot be null");
		Assert.notNull(arId, "Borough Id is mandatory, it cannot be null");
		return this.dao.findBourough(regionId, countyId, arId);

	}
}