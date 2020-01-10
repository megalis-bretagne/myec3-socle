package org.myec3.socle.core.service;

import org.myec3.socle.core.domain.model.InseeCounty;

public interface InseeCountyService extends IGenericService<InseeCounty> {

	public InseeCounty findById(String countyId);
}
