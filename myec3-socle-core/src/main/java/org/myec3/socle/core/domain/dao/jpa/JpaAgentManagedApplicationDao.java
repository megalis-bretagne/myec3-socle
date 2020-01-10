package org.myec3.socle.core.domain.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.myec3.socle.core.domain.dao.AgentManagedApplicationDao;
import org.myec3.socle.core.domain.model.AgentManagedApplication;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.springframework.stereotype.Repository;

/**
 * Created by a602499 on 16/12/2016.
 */
@Repository("agentManagedApplicationsDao")
public class JpaAgentManagedApplicationDao extends JpaNoResourceGenericDao<AgentManagedApplication>
		implements AgentManagedApplicationDao {

	public List<AgentManagedApplication> findAgentManagedApplicationsByOrganism(Organism organism) {
		Query q = getEm().createQuery(
				"SELECT a FROM " + this.getDomainClass().getSimpleName() + " a " + "WHERE a.organism = :organism");
		q.setParameter("organism", organism);

		try {
			List<AgentManagedApplication> result = q.getResultList();
			this.getLog().debug(
					"findAgentManagedApplicationsByOrganism: successfully retrieved AgentManagedApplication for Organism #"
							+ organism.getId());
			return result;
		} catch (NoResultException nre) {
			this.getLog()
					.debug("findAgentManagedApplicationsByOrganism: no AgentManagedApplication was found for Organism #"
							+ organism.getId());
			return new ArrayList<AgentManagedApplication>();
		} catch (RuntimeException re) {
			this.getLog().error("findAgentManagedApplicationsByOrganism: an error occured", re);
			return null;
		}
	}

	public List<AgentManagedApplication> findAgentManagedApplicationsByAgentProfile(AgentProfile agentProfile) {
		Query q = getEm().createQuery("SELECT a FROM " + this.getDomainClass().getSimpleName() + " a "
				+ "WHERE a.agentProfile = :agentProfile");
		q.setParameter("agentProfile", agentProfile);

		try {
			List<AgentManagedApplication> result = q.getResultList();
			this.getLog().debug(
					"findAgentManagedApplicationsByOrganism: successfully retrieved AgentManagedApplication for AgentProfile #"
							+ agentProfile.getId());
			return result;
		} catch (NoResultException nre) {
			this.getLog().debug(
					"findAgentManagedApplicationsByOrganism: no AgentManagedApplication was found for AgentProfile #"
							+ agentProfile.getId());
			return new ArrayList<AgentManagedApplication>();
		} catch (RuntimeException re) {
			this.getLog().error("findAgentManagedApplicationsByOrganism: an error occured", re);
			return null;
		}
	}

	public List<AgentManagedApplication> findAgentManagedApplicationsByApplicationAndOrganism(Application application,
			Organism organism) {
		Query q = getEm().createQuery("SELECT a FROM " + this.getDomainClass().getSimpleName() + " a "
				+ "WHERE a.managedApplication = :application AND a.organism = :organism");
		q.setParameter("application", application);
		q.setParameter("organism", organism);

		try {
			List<AgentManagedApplication> result = q.getResultList();
			this.getLog()
					.debug("findAgentManagedApplicationsByApplicationAndOrganism: " + result.size()
							+ " AgentProfiles were found for Organism #" + organism.getId() + " and Application #"
							+ application.getId());
			return result;
		} catch (NoResultException nre) {
			this.getLog()
					.debug("findAgentManagedApplicationsByApplicationAndOrganism: "
							+ "no AgentProfiles were found for Organism #" + organism.getId() + " and Application #"
							+ application.getId());
			return new ArrayList<AgentManagedApplication>();
		} catch (RuntimeException re) {
			this.getLog().error("findAgentManagedApplicationsByApplicationAndOrganism: an error occured", re);
			return null;
		}
	}

	@Override
	public Class<AgentManagedApplication> getType() {
		return AgentManagedApplication.class;
	}
}
