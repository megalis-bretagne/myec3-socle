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
package org.myec3.socle.core.domain.dao;

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;

import java.util.List;

/**
 * This interface define methods to perform specific queries on
 * {@link AgentProfile} objects. It only defines new specific methods and
 * inherits methods from {@link GenericProfileDao}.
 *
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface AgentProfileDao extends GenericProfileDao<AgentProfile> {

	/**
	 * Find all agent profiles associated directly to an OrganismDepartment.
	 * Only enabled profiles will be returned
	 *
	 * @param organismDepartment : organism Department to search
	 * @return List of AgentProfiles associated to this department. return an
	 * empty list if no result or null in case of error.
	 */
	List<AgentProfile> findAllAgentProfilestByOrganismDepartment(
			OrganismDepartment organismDepartment);

	/**
	 * Find all agent profiles associated directly to an {@link Organism}. Only
	 * enabled profiles will be returned
	 *
	 * @param organism : agentProfile's organism
	 * @return List of AgentProfiles associated to this organism. Return an
	 * empty list if no result or null in case of error.
	 */
	List<AgentProfile> findAllAgentProfilesByOrganism(Organism organism);

	/**
	 * Find all subscribed agent profiles associated directly to an {@link Organism} and an {@link Application}. Only
	 * enabled profiles will be returned
	 * @param organism agentProfile's organism
	 * @param application Subscriber application
	 * @return List of subscribed AgentProfiles associated to this organism and application
	 */
	List<AgentProfile> findAllAgentProfilesByOrganismAndApplication(Organism organism, Application application);

	/**
	 * Find agent profiles which have administrator rights on GU application
	 *
	 * @param organismId : organism UID
	 * @return List of AgentProfiles which have administrator rights on GU
	 * application. Return an empty list if no result or null in case of
	 * error.
	 * @throws IllegalArgumentException when organismDepartment is null
	 */
	List<AgentProfile> findAllGuAdministratorByOrganismId(Long organismId);

	/**
	 * Find agent profiles which are representatives at the General Assembly for
	 * the current organism
	 *
	 * @param organism : agentProfile's organism
	 * @return List of AgentProfiles which are representatives at the General
	 * Assembly for the current organism. Return an empty list if no
	 * result or null in case of error.
	 */
	List<AgentProfile> findAllRepresentativesByOrganism(Organism organism);

	/**
	 * Find agent profiles which are substitutes at the General Assembly for the
	 * current organism
	 *
	 * @param organism : agentProfile's organism
	 * @return List of AgentProfiles which are substitutes at the General
	 * Assembly for the current organism. Return an empty list if no
	 * result or null in case of error.
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
	 * Find agent profiles wich match with filter
	 *
	 * @param page     page wanted begin at 0
	 * @param filter   word to search in name, label or competences
	 * @param sortBy   column to order
	 * @param sortDir DESC, ASC
	 * @param size     number of element per page
	 * @return
	 */
	List<AgentProfile> findAnnuaire(int page, String filter, String sortBy, String sortDir, int size,
									String competences, Boolean enableFilter);

	/**
	 * Count agent profiles wich match with filter
	 *
	 * @param filter word to search in name, label or competences
	 * @return
	 */
	Long findAnnuaireCount(String filter, String competences, Boolean enableFilter);

	AgentProfile findAgentProfileByIdSdm(long idSdm);
}