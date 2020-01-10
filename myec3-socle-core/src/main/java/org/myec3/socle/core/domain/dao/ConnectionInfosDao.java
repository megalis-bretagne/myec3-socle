package org.myec3.socle.core.domain.dao;

import java.util.List;

import org.myec3.socle.core.domain.model.ConnectionInfos;

public interface ConnectionInfosDao extends NoResourceGenericDao<ConnectionInfos> {
	
    List<ConnectionInfos> getRecentConnectionInfos();
}
