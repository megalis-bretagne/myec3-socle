package org.myec3.socle.core.service;

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Competence;

public interface CompetenceService extends IGenericService<Competence> {
	
	public void update(AgentProfile ap);
}
