package org.myec3.socle.core.domain.dao;

import org.myec3.socle.core.domain.model.AgentManagedApplication;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;

import java.util.List;

/**
 * Created by a602499 on 16/12/2016.
 */
public interface AgentManagedApplicationDao extends NoResourceGenericDao<AgentManagedApplication> {

	public List<AgentManagedApplication> findAgentManagedApplicationsByOrganism(Organism organism);

	public List<AgentManagedApplication> findAgentManagedApplicationsByAgentProfile(AgentProfile agentProfile);

	public List<AgentManagedApplication> findAgentManagedApplicationsByApplicationAndOrganism(Application application, Organism organism);
}
