package org.myec3.socle.core.domain.dao;

import org.myec3.socle.core.domain.model.InseeCanton;

public interface InseeCantonDao extends NoResourceGenericDao<InseeCanton> {

    public InseeCanton findCanton(Long regionId, String countyId,
	    Long cantonId);

   /**
    * Find the highest id from the canton table
    *
    * @return the highest id from the canton table
    *      Return null in case of error.
    */
    public Long getCantonMaxId();

}
