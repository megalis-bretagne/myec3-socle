package org.myec3.socle.core.service.impl;

import org.myec3.socle.core.domain.dao.InseeCountyDao;
import org.myec3.socle.core.domain.model.InseeCounty;
import org.myec3.socle.core.service.InseeCountyService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("inseeCountyService")
public class InseeCountyServiceImpl extends AbstractGenericServiceImpl<InseeCounty, InseeCountyDao>
		implements InseeCountyService {

	public InseeCounty findById(String countyId) {
		Assert.notNull(countyId, "countryId is null. null value is forbiden");
		return this.dao.findById(countyId);
	}
}