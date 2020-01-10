package org.myec3.socle.core.domain.dao;

import java.util.List;

import org.myec3.socle.core.domain.model.MpsUpdateJob;

public interface MpsUpdateJobDao extends NoResourceGenericDao<MpsUpdateJob> {

    List<MpsUpdateJob> findLimit (int limit);
    
}
