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
package org.myec3.socle.core.service;

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;

import java.util.List;

/**
 * Interface defining Business Services methods and providing
 * {@link AgentProfile} specific operations. This interface extends the common
 * GenericProfileService interface by adding new specific methods
 *
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface AgentProfileService extends GenericProfileService<AgentProfile> {

	/**
	 * Find all agent profiles associated directly to an OrganismDepartment. Only
	 * enabled profiles will be returned
	 *
	 * @param organismDepartment : organism Department to search
	 * @return List of AgentProfiles associated to this department
	 * @throws IllegalArgumentException when organismDepartment is null
	 */
	List<AgentProfile> findAllAgentProfilesByOrganismDepartment(OrganismDepartment organismDepartment);

	/**
	 * Find all agent profiles associated directly to an {@link Organism}. Only
	 * enabled profiles will be returned
	 *
	 * @param organism : agentProfile's organism
	 * @return List of AgentProfiles associated to this organism. Return an empty
	 * list if no result or null in case of error.
	 */
	List<AgentProfile> findAllAgentProfilesByOrganism(Organism organism);

	/**
	 * This method allows to populate agentProfile's collections
	 *
	 * @param agentProfile : agentProfile to populate
	 * @throws IllegalArgumentException if agentProfile is null
	 */
	void populateCollections(AgentProfile agentProfile);

	/**
	 * This method allows to clean agentProfile's collections
	 *
	 * @param agentProfile : agentProfile to clean
	 * @throws IllegalArgumentException if agentProfile is null
	 */
	void cleanCollections(AgentProfile agentProfile);

	/**
	 * Find agent profiles which have administrator rights on GU application for an
	 * organism
	 *
	 * @param organismId : organism UID
	 * @return List of AgentProfiles which have administrator rights on GU
	 * application
	 * @throws IllegalArgumentException when organismId is null
	 */
	List<AgentProfile> findAllGuAdministratorByOrganismId(Long organismId);

	/**
	 * Find agent profiles which are representatives at the General Assembly for the
	 * given organism
	 *
	 * @param organism : agentProfile's organism
	 * @return List of AgentProfiles which are representatives at the General
	 * Assembly for the given organism
	 */
	List<AgentProfile> findAllRepresentativesByOrganism(Organism organism);

	/**
	 * Find agent profiles which are substitutes at the General Assembly for the
	 * given organism
	 *
	 * @param organism : agentProfile's organism
	 * @return List of AgentProfiles which are substitutes at the General Assembly
	 * for the given organism
	 */
	List<AgentProfile> findAllSubstitutesByOrganism(Organism organism);

	/**
	 * Retrieve a list of profiles from a list of emails.
	 *
	 * @param listOfEmails : list of emails to search for
	 * @return found Profiles with this email. Returns an empty array if no result
	 * found.
	 */
	List<AgentProfile> findAllByListOfEmailsAndOrganism(List<String> listOfEmails, Organism organism);

	/**
	 * Find dashboard in agent profile
	 *
	 * @param id profile agent
	 * @return found dashboard
	 */
	String getDashboard(Long id);

	/**
	 * Update dashboard in agent profile
	 *
	 * @param id        of profile agent
	 * @param dashboard dashboard
	 * @return updated agent
	 */
	String updateOrCreateDashboard(Long id, String dashboard);

	/**
	 * Return a list AgentProfile paginate
	 *
	 * @param page     page wanted, begin at 1
	 * @param filter   word to search in name, label or competences
	 * @param sortBy   column to order
	 * @param sortDir DESC, ASC
	 * @param size     number of element per page
	 * @return
	 */
	List<AgentProfile> getAnnuaire(int page, String filter, String sortBy, String sortDir, int size, String competences,
								   Boolean enableFilter) throws Exception;

	/**
	 * Return number of results without pagination
	 *
	 * @param filter word to search in name, label or competences
	 * @return number of agentProfile found
	 */
	Long getAnnuaireCount(String filter, String competences, Boolean enableFilter);
	
	/**
	* This methode return organism of an agentProfile
	* @param agentProfileId
	 * 
	* @return
	*/
	OrganismDepartment getOrganismDepartmentByAgentProfileId(Long agentProfileId);

}
