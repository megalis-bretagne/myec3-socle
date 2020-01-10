package org.myec3.socle.core.domain.dao;

import org.myec3.socle.core.domain.model.InseeBorough;

public interface InseeBoroughDao extends NoResourceGenericDao<InseeBorough> {

    public InseeBorough findBourough(Long regionId, String countyId, Long arId);
    
}
