package org.myec3.socle.core.service.impl;

import java.util.List;

import org.myec3.socle.core.domain.dao.MpsUpdateJobDao;
import org.myec3.socle.core.domain.model.MpsUpdateJob;
import org.myec3.socle.core.service.MpsUpdateJobService;
import org.springframework.stereotype.Service;

@Service("mpsUpdateJobService")
public class MpsUpdateJobServiceImpl extends AbstractGenericServiceImpl<MpsUpdateJob, MpsUpdateJobDao>
		implements MpsUpdateJobService {

	public List<MpsUpdateJob> findLimit(int limit) {

		return (dao.findLimit(limit));

	}

}
