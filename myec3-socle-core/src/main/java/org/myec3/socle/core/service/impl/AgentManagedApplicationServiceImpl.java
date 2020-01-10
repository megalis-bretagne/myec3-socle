package org.myec3.socle.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.myec3.socle.core.domain.dao.AgentManagedApplicationDao;
import org.myec3.socle.core.domain.model.AgentManagedApplication;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.service.AgentManagedApplicationService;
import org.springframework.stereotype.Service;

/**
 * Created by a602499 on 16/12/2016.
 */
@Service("agentManagedApplicationService")
public class AgentManagedApplicationServiceImpl
		extends AbstractGenericServiceImpl<AgentManagedApplication, AgentManagedApplicationDao>
		implements AgentManagedApplicationService {

	@Override
	public List<AgentManagedApplication> findAgentManagedApplicationsByOrganism(Organism organism) {
		return this.dao.findAgentManagedApplicationsByOrganism(organism);
	}

	@Override
	public List<AgentManagedApplication> findAgentManagedApplicationsByAgent(AgentProfile agent) {
		return this.dao.findAgentManagedApplicationsByAgentProfile(agent);
	}

	@Override
	public List<AgentManagedApplication> findAgentManagedApplicationsByApplicationAndOrganism(Application application,
			Organism organism) {
		return this.dao.findAgentManagedApplicationsByApplicationAndOrganism(application, organism);
	}

	@Override
	public List<Application> findApplicationManagedByAgent(AgentProfile agent) {
		List<AgentManagedApplication> agentManagedApplications = this.findAgentManagedApplicationsByAgent(agent);
		List<Application> apps = new ArrayList<Application>();
		for (AgentManagedApplication agentManagedApplication : agentManagedApplications) {
			apps.add(agentManagedApplication.getManagedApplication());
		}

		return apps;
	}

	@Override
	public void deleteList(List<AgentManagedApplication> agentManagedApplications) {
		for (AgentManagedApplication app : agentManagedApplications) {
			delete(app);
		}
	}

	@Override
	public void deleteAllByAgentProfile(AgentProfile agent) {
		List<AgentManagedApplication> apps = this.findAgentManagedApplicationsByAgent(agent);
		this.deleteList(apps);
	}
}
