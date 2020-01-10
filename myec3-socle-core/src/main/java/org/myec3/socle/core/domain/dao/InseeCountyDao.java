package org.myec3.socle.core.domain.dao;

import org.myec3.socle.core.domain.model.InseeCounty;

public interface InseeCountyDao extends NoResourceGenericDao<InseeCounty> {
    public InseeCounty findById(String countyId);
}
