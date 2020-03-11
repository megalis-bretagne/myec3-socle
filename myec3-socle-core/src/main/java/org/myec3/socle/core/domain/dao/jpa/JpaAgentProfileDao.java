/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p>
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.core.domain.dao.jpa;

import org.myec3.socle.core.domain.dao.AgentProfileDao;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.model.enums.RoleProfile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * This interface define methods to perform specific queries on
 * {@link AgentProfile} objects. It only provides new specific methods and
 * inherits methods from {@link JpaGenericProfileDao}.
 *
 * @author Matthieu Dubromez <matthieu.dubromez@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Repository("agentProfileDao")
public class JpaAgentProfileDao extends JpaGenericProfileDao<AgentProfile> implements AgentProfileDao {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AgentProfile> findAllAgentProfilestByOrganismDepartment(OrganismDepartment organismDepartment) {
		this.getLog().debug("Finding all Resource instances with OrganismDepartment: " + organismDepartment);
		try {
			Query query = this.getEm().createQuery("select r from " + this.getDomainClass().getSimpleName()
					+ " r where r.organismDepartment like :organismDepartment and r.enabled = :enabled");
			query.setParameter("organismDepartment", organismDepartment);
			query.setParameter("enabled", Boolean.TRUE);
			List<AgentProfile> resourceList = query.getResultList();
			this.getLog().debug("findAllAgentProfilestByOrganismDepartment successfull.");
			return resourceList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findAllAgentProfilestByOrganismDepartment returned no result.");
			return new ArrayList<AgentProfile>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllAgentProfilestByOrganismDepartment failed.", re);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AgentProfile> findAllAgentProfilesByOrganism(Organism organism) {
		this.getLog().debug("Finding all agents in organism: " + organism.getLabel());
		try {
			Query query = this.getEm()
					.createQuery("SELECT ap FROM " + this.getDomainClass().getSimpleName()
							+ " ap INNER JOIN ap.organismDepartment od "
							+ "WHERE od.organism = :organism AND ap.enabled is true");
			query.setParameter("organism", organism);
			List<AgentProfile> resourceList = query.getResultList();
			this.getLog().debug("findAllAgentProfilesByOrganism successfull.");
			return resourceList;
		} catch (NoResultException re) {
			// No result found, we return null instead of errors
			getLog().warn("findAllAgentProfilesByOrganism returned no result.");
			return new ArrayList<AgentProfile>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllAgentProfilesByOrganism failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<AgentProfile> findAllGuAdministratorByOrganismId(Long organismId) {
		this.getLog().debug("Finding GU administrators for the Organism with id: " + organismId);
		try {
			Query query = this.getEm()
					.createQuery("SELECT ap FROM " + this.getDomainClass().getSimpleName() + " ap "
							+ "LEFT JOIN ap.roles r WHERE r.name = '" + RoleProfile.ROLE_MANAGER_AGENT
							+ "' AND ap.organismDepartment.id in ("
							+ "SELECT o.id from OrganismDepartment o where o.organism.id = :organismId) "
							+ "AND ap.enabled=true");
			query.setParameter("organismId", organismId);
			List<AgentProfile> agentProfileList = query.getResultList();
			this.getLog().debug("findAllGuAdministratorByOrganismId successfull.");
			return agentProfileList;
		} catch (NoResultException re) {
			// No result found, we return empty arrayList instead of errors
			this.getLog().warn("findAllGuAdministratorByOrganismId returned no result.");
			return new ArrayList<AgentProfile>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllGuAdministratorByOrganismId failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<AgentProfile> findAllRepresentativesByOrganism(Organism organism) {
		this.getLog().debug("Finding representatives agent for the organism with id: " + organism.getId());
		try {
			Query query = this.getEm()
					.createQuery("SELECT ap FROM " + this.getDomainClass().getSimpleName()
							+ " ap inner join ap.organismDepartment od "
							+ "WHERE od.organism = :organism AND ap.representative is true AND ap.enabled is true");
			query.setParameter("organism", organism);
			List<AgentProfile> agentProfileList = query.getResultList();
			this.getLog().debug("findAllRepresentativesByOrganism successfull.");
			return agentProfileList;
		} catch (NoResultException re) {
			// No result found, we return empty arrayList instead of errors
			this.getLog().warn("findAllRepresentativesByOrganism returned no result.");
			return new ArrayList<AgentProfile>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllRepresentativesByOrganism failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<AgentProfile> findAllSubstitutesByOrganism(Organism organism) {
		this.getLog().debug("Finding substitutes agent for the organism with id: " + organism.getId());
		try {
			Query query = this.getEm()
					.createQuery("SELECT ap FROM " + this.getDomainClass().getSimpleName()
							+ " ap inner join ap.organismDepartment od "
							+ "WHERE od.organism = :organism AND ap.substitute is true AND ap.enabled is true");
			query.setParameter("organism", organism);
			List<AgentProfile> agentProfileList = query.getResultList();
			this.getLog().debug("findAllSubstitutesByOrganism successfull.");
			return agentProfileList;
		} catch (NoResultException re) {
			// No result found, we return empty arrayList instead of errors
			this.getLog().warn("findAllSubstitutesByOrganism returned no result.");
			return new ArrayList<AgentProfile>();
		} catch (RuntimeException re) {
			this.getLog().error("findAllSubstitutesByOrganism failed.", re);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<AgentProfile> findAllByListOfEmailsAndOrganism(List<String> listOfEmails, Organism organism) {
		this.getLog().debug("find all profiles by list of emails");

		try {
			Query query = this.getEm()
					.createQuery("SELECT ap FROM " + this.getDomainClass().getSimpleName()
							+ " ap INNER JOIN ap.organismDepartment od "
							+ "WHERE od.organism = :organism AND ap.enabled is true AND ap.email in (:listOfEmails)");
			query.setParameter("listOfEmails", listOfEmails);
			query.setParameter("organism", organism);
			List<AgentProfile> resourceList = query.getResultList();
			this.getLog().debug("findAllByListOfEmailsAndOrganism successfull.");
			return resourceList;
		} catch (NoResultException re) {
			// No results, we return an empty list
			this.getLog().error("findAllByListOfEmailsAndOrganism returned no result.", re);
			return new ArrayList<AgentProfile>();
		} catch (RuntimeException re) {
			// error occured, we throw the exception
			this.getLog().error("findAllByListOfEmailsAndOrganism failed.", re);
			throw re;
		}
	}

	@Override
	public Class<AgentProfile> getType() {
		return AgentProfile.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<AgentProfile> findAnnuaire(int page, String filter, String sortBy, String sortDir, int size,
										   String competences, Boolean enableFilter) {
		String bodyRequest = bodyRequest(competences, enableFilter);
		String orderLine = "ORDER BY ap." + sortBy + " " + sortDir;
		filter = '%' + filter + '%';

		Query q = getEm().createQuery("SELECT ap " + bodyRequest + "GROUP BY ap.id " + orderLine);
		q.setParameter("filter", filter);
		q.setFirstResult(page * size);
		q.setMaxResults(size);
		return q.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long findAnnuaireCount(String filter, String competences, Boolean enableFilter) {
		String bodyRequest = bodyRequest(competences, enableFilter);
		filter = '%' + filter + '%';
		Query q = getEm().createQuery("SELECT count(ap.id) " + bodyRequest);
		q.setParameter("filter", filter);
		return (Long) q.getSingleResult();
	}

	private String bodyRequest(String competences, Boolean enableFilter) {
		String competencesClause = (StringUtils.isEmpty(competences)) ? "" : " AND cp.id IN " + competences + " ";
		String enabledClause = (enableFilter == null) ? "" : " AND ap.enabled = " + enableFilter + " ";
		return "FROM " + this.getDomainClass().getSimpleName() + " ap " +
				"LEFT JOIN  ap.competences cp " +
				"LEFT JOIN ap.user u " +
				"WHERE  (u.lastname LIKE :filter OR u.firstname LIKE :filter OR ap.email LIKE :filter) " + competencesClause + enabledClause;


	}


	@Override
	public AgentProfile findAgentProfileByIdSdm(long idSdm) {
		Query q = this.getEm().createQuery("select c from " + this.getDomainClass().getSimpleName() + " c" + " JOIN SynchroIdentifiantExterne s on c.id=s.idSocle"
				+ " WHERE s.idAppliExterne= :idSdm AND s.typeRessource= :typeResource");
		q.setParameter("idSdm", idSdm);
		q.setParameter("typeResource", ResourceType.AGENT_PROFILE);
		try{
			AgentProfile result = (AgentProfile) q.getSingleResult();
			getLog().debug("findAgentProfileByIdSdm successfull.");
			return result;
		} catch (NoResultException nre) {
			getLog().warn("findAgentProfileByIdSdm returned no results.");
			return null;
		} catch (RuntimeException re) {
			getLog().error("findAgentProfileByIdSdm failed.", re);
			return null;
		}
	}

}
