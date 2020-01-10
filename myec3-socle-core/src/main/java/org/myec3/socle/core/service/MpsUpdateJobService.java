package org.myec3.socle.core.service;

import java.util.List;

import org.myec3.socle.core.domain.model.MpsUpdateJob;

public interface MpsUpdateJobService extends IGenericService<MpsUpdateJob> {

	List<MpsUpdateJob> findLimit(int limit);
}
