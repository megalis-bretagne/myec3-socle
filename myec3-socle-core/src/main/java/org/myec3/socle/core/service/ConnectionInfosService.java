package org.myec3.socle.core.service;

import java.util.List;

import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.ConnectionInfos;
import org.myec3.socle.core.domain.model.MpsUpdateJob;
import org.myec3.socle.core.domain.model.User;

public interface ConnectionInfosService extends IGenericService<ConnectionInfos> {

	ConnectionInfos update(ConnectionInfos conInfos, Long timestamp);

	List<ConnectionInfos> getRecentConnectionInfos();

	Company getConnectionInfosCompany(ConnectionInfos connectionInfo);

	List<MpsUpdateJob> getUserCompanyToUpdate();

	void updateUserLastConnectionTime(User user, long time);
}
