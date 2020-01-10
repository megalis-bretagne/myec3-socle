package org.myec3.socle.core.service;

import java.util.List;

import org.myec3.socle.core.domain.model.AgentManagedApplication;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;

/**
 * Created by a602499 on 16/12/2016.
 */
public interface AgentManagedApplicationService extends IGenericService<AgentManagedApplication> {

	public List<AgentManagedApplication> findAgentManagedApplicationsByOrganism(Organism organism);

	public List<AgentManagedApplication> findAgentManagedApplicationsByAgent(AgentProfile agent);

	public List<AgentManagedApplication> findAgentManagedApplicationsByApplicationAndOrganism(Application application,
			Organism organism);

	public List<Application> findApplicationManagedByAgent(AgentProfile agent);

	public void deleteList(List<AgentManagedApplication> agentManagedApplications);

	public void deleteAllByAgentProfile(AgentProfile agent);
}
