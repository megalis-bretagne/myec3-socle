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
package org.myec3.socle.webapp.pages.organism.agent;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.service.AgentManagedApplicationService;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Page used to display agent's{@link AgentProfile} roles{@link Role}<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/organism/agent/ViewRoles.tml<br
 * />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class ViewRoles extends AbstractPage {

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Inject
	@Named("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	@Inject
	@Named("agentManagedApplicationService")
	private AgentManagedApplicationService agentManagedApplicationService;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	private AgentProfile agentProfile;

	@Property
	private List<Role> roles;

	@Property
	private Application applicationLoop;

	@Property
	private Role roleApplicationLoop;

	private List<Application> systemApplications;

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long profileId) {
		super.initUser();

		this.agentProfile = this.agentProfileService.findOne(profileId);
		if (null == this.agentProfile) {
			return Boolean.FALSE;
		}

		roles = this.agentProfile.getRolesWithoutHidden();

		this.systemApplications = this.applicationService.findAllDefaultApplicationsByStructureTypeAndCustomer(
				this.agentProfile.getOrganismDepartment().getOrganism().getStructureType(),
				this.agentProfile.getOrganismDepartment().getOrganism().getCustomer());

		// Remove GU and GED because they are specific applications.
		Application gu = this.applicationService.findByName(MyEc3ApplicationConstants.GU);
		this.systemApplications.remove(gu);

		Application ged = this.applicationService.findByName(MyEc3ApplicationConstants.GED_SERVICE);
		this.systemApplications.remove(ged);

		// Remove ocProjets because that's a specific application.
		Application ocProjets = this.applicationService.findByName(MyEc3ApplicationConstants.OC_PROJETS_SERVICE);
		this.systemApplications.remove(ocProjets);

		// Remove ocReunions because that's a specific application.
		Application ocReunions = this.applicationService.findByName(MyEc3ApplicationConstants.OC_REUNIONS_SERVICE);
		this.systemApplications.remove(ocReunions);

		// Remove ocBlocNotes because that's a specific application.
		Application ocBlocNotes = this.applicationService.findByName(MyEc3ApplicationConstants.OC_BLOC_NOTES_SERVICE);
		this.systemApplications.remove(ocBlocNotes);

		// Check if loggedUser can access at this page
		return this.hasRights(this.agentProfile);
	}

	public Object onPassivate() {
		return (this.agentProfile != null) ? this.agentProfile.getId() : null;
	}

	// Getters n Setters
	public AgentProfile getAgentProfile() {
		return agentProfile;
	}

	public void setAgentProfile(AgentProfile agentProfile) {
		this.agentProfile = agentProfile;
	}

	public Boolean getDisplayedApplication() {
		if (this.systemApplications.contains(applicationLoop)) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	public List<Application> getApplicationsAgentSubscribed() {
		List<Application> applicationsAgentSubscribed = new ArrayList<Application>();
		List<Application> appManagedByLoggedUser = null;

		if (this.getIsApplicationManagerAgent()) {
			appManagedByLoggedUser = this.agentManagedApplicationService
					.findApplicationManagedByAgent((AgentProfile) this.getLoggedProfile());
		}

		for (Role role : this.roles) {
			if (!applicationsAgentSubscribed.contains(role.getApplication())) {
				if (appManagedByLoggedUser == null || appManagedByLoggedUser.contains(role.getApplication())) {
					applicationsAgentSubscribed.add(role.getApplication());
				}
			}
		}
		return applicationsAgentSubscribed;
	}

	public List<Role> getRolesApplication() {
		List<Role> rolesApplicationList = new ArrayList<Role>();
		for (Role role : this.roles) {
			if (role.getApplication().equals(this.applicationLoop)) {
				rolesApplicationList.add(role);
			}
		}
		return rolesApplicationList;
	}

	public Boolean getIsMultipleRolesApplication() {
		if (this.getRolesApplication().size() > 1) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	/**
	 * Check if current employee is enabled
	 * @return
	 */
	public Boolean getIsEnabled() {
		if (agentProfile.getUser() == null) {
			return Boolean.FALSE;
		}
		return agentProfile.getUser().isEnabled();
	}
}
