/**
 * Copyright (c) 2011 Atos Bourgogne
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.pages.user;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.service.*;
import org.myec3.socle.webapp.pages.AbstractPage;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Page used to search users{@link User} by using filters<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/user/Search.tml<br />
 * 
 * This page is only visible by technical administrator (role
 * SUPER_ADMIN).<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class Search extends AbstractPage {

	private static final Log logger = LogFactory.getLog(Search.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects
	 */
	@Inject
	@Named("profileService")
	private ProfileService profileService;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Inject
	@Named("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	@Inject
	@Named("agentProfileService")
	private AgentProfileService agentProfileService;

	@Inject
	@Named("companyDepartmentService")
	private CompanyDepartmentService companyDepartmentService;

	@Inject
	@Named("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@Inject
	@Named("establishmentService")
	private EstablishmentService establishmentService;

	@Inject
	@Named("structureService")
	private StructureService structureService;

	@InjectPage
	private SearchResult searchResultPage;

	@Component(id = "user_search_form")
	private Form userSearchForm;

	@Property
	private String searchEmail;

	@Property
	private String searchUsername;

	@Property
	private String searchFirstname;

	@Property
	private String searchLastname;

	@Property
	private Long searchSviProfile;

	@Property
	private List<Profile> profileResult;

	@Property
	private ProfileSearch profileSearch;

	@Property
	private ArrayList<ProfileSearch> profileSearchList;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String infoMessage;

	@OnEvent(EventConstants.ACTIVATE)
	public void activation() {
		// EMPTY
	}

	@OnEvent(component = "user_search_form", value = EventConstants.SUCCESS)
	public Object onSuccess() {

		this.profileSearchList = new ArrayList<>();
		Profile loggerUser = this.getLoggedProfile();

		if (!canLaunchSearch()) {
			this.errorMessage = this.getMessages().get("search-error");
			return null;
		}

		// get the matching Profiles
		this.profileResult = this.profileService.findAllByCriteria(searchEmail, searchUsername, searchFirstname,
				searchLastname, searchSviProfile);

		for (Profile currentProfile : profileResult) {

			this.profileSearch = new ProfileSearch();

			// if Profile is disabled, do not add him to the result list
			// just get the next Profile
			if (currentProfile.getEnabled().equals(Boolean.FALSE)) {
				logger.debug("Profile Removed : " + currentProfile.getId());
			} else {
				// set the Profile for SearchResult
				this.profileSearch.setSearchProfile(currentProfile);

				// if the currentProfile is an employee
				if (currentProfile.isEmployee()) {
					// find the Employee Company
					EmployeeProfile searchEmployeeProfile = this.employeeProfileService.findOne(currentProfile.getId());
					Structure searchStructure = this.structureService
							.findOne(searchEmployeeProfile.getEstablishment().getCompany().getId());

					// if loggerUser is a ManagerEmployee
					if (BooleanUtils.isTrue(this.getIsManagerEmployee())) {

						// get the loggedUser EmplyeeProfile
						EmployeeProfile loggedEmployee = employeeProfileService.findOne(loggerUser.getId());

						// get the loggedUser company
						Company loggedEmployeeCompany = loggedEmployee.getEstablishment().getCompany();

						// check that loggedEmployee Company is the same
						if (loggedEmployeeCompany.getId()
								.equals(searchEmployeeProfile.getEstablishment().getCompany().getId())) {
							// set the EmployeeStructure for SearchResult
							this.profileSearch.setSearchStructure(searchStructure);
							logger.debug("Add following ProfileSearch for Company : "
									+ profileSearch.getSearchProfile().toString());
							this.profileSearchList.add(this.profileSearch);
							continue;
						}
					}

					// Functional or Technical Admin ?
					// Don't ask question and add Profile
					else if (this.getIsTechnicalAdmin() || this.getIsFunctionalAdmin()) {
						this.profileSearch.setSearchStructure(searchStructure);
						this.profileSearchList.add(this.profileSearch);
						continue;
					}

				}

				// if the currentProfile is an agent
				else if (currentProfile.isAgent()) {

					// find the Agent organism
					AgentProfile searchAgentProfile = this.agentProfileService.findOne(currentProfile.getId());
					Structure searchStructure = this.structureService
							.findOne(searchAgentProfile.getOrganismDepartment().getOrganism().getId());

					if (BooleanUtils.isTrue(this.getIsGlobalManagerAgent())) {

						// get the loggedUser AgentProfile
						AgentProfile loggedAgent = agentProfileService.findOne(loggerUser.getId());

						// get the loggedUser organism
						Organism loggedAgentOrganism = loggedAgent.getOrganismDepartment().getOrganism();

						// check that loggedAgent organism is the same
						if (loggedAgentOrganism.getId()
								.equals(searchAgentProfile.getOrganismDepartment().getOrganism().getId())) {

							// set the EmployeeStructure for SearchResult
							this.profileSearch.setSearchStructure(searchStructure);
							this.profileSearchList.add(this.profileSearch);
							continue;
						}
					}
					// Functional or Technical Admin ?
					// Don't ask question and add Profile
					else if (this.getIsTechnicalAdmin() || this.getIsFunctionalAdmin()) {
						this.profileSearch.setSearchStructure(searchStructure);
						logger.debug("Add following ProfileSearch for Organism : "
								+ profileSearch.getSearchProfile().toString());
						this.profileSearchList.add(this.profileSearch);
						continue;
					}
				}
			}
		}
		logger.info("User Search Complete");

		if (this.profileSearchList.isEmpty()) {
			logger.debug("profileSearchList is empty !");
			this.infoMessage = this.getMessages().get("empty-result-message");
			return null;
		}

		this.searchResultPage.setProfileSearchResult(this.profileSearchList);
		return SearchResult.class;
	}

	public List<EmployeeProfile> getEmployeeProfileList(Company company) {
		List<EmployeeProfile> employeeProfiles = new ArrayList<>();
		if (company != null) {
			List<CompanyDepartment> companyDepartments = this.companyDepartmentService
					.findAllDepartmentByCompany(company);

			for (CompanyDepartment companyDepartment : companyDepartments) {
				employeeProfiles.addAll(
						this.employeeProfileService.findAllEmployeeProfilesByCompanyDepartment(companyDepartment));
			}
		}
		return employeeProfiles;
	}

	public List<AgentProfile> getAgentProfileList(Organism organism) {
		List<AgentProfile> aProfiles = new ArrayList<>();
		if (organism != null) {
			List<OrganismDepartment> organismDepartments = this.organismDepartmentService
					.findAllDepartmentByOrganism(organism);

			for (OrganismDepartment oDepartment : organismDepartments) {
				aProfiles.addAll(this.agentProfileService.findAllAgentProfilesByOrganismDepartment(oDepartment));
			}
		}
		return aProfiles;
	}

	/**
	 * Check if user can launch Search
	 * @return	true if at least one field is not empty
	 */
	private boolean canLaunchSearch() {
		return !(StringUtils.isEmpty(searchEmail) &&
				StringUtils.isEmpty(searchUsername) &&
				StringUtils.isEmpty(searchFirstname) &&
				StringUtils.isEmpty(searchLastname) &&
				searchSviProfile == null);
	}
}
