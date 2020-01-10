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
package org.myec3.socle.webapp.pages.organism.appmanager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.AgentManagedApplication;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.RoleProfile;
import org.myec3.socle.core.service.AgentManagedApplicationService;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Created by a602499 on 19/12/2016.
 */
public class ModifyAppManagers extends AbstractPage {

	private static Log logger = LogFactory.getLog(ModifyAppManagers.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Inject
	@Named("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

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

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Inject
	@Named("roleService")
	private RoleService roleService;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@InjectPage
	private ListAppManagers listAppManagers;

	@Component(id = "modification_form")
	private Form modificationRolesForm;

	@Property
	private Organism organism;

	@Property
	private List<Application> subscribedApplications;

	@Property
	private List<AgentProfile> agentList;

	@Property
	private Map<String, AgentProfile> agentModelMap;

	/**
	 * Current application in loop
	 */
	@Property
	private Application applicationLoop;

	@Property
	private AgentManagedApplication appManagerLoop;

	@Property
	private AgentProfile selectedAgent;

	@Property
	private String agentSelect;

	private Boolean isManagedApplication;

	/**
	 * List of the applications whose role must not be managed
	 */
	private List<Application> systemApplications;

	private List<AgentManagedApplication> selectedAgentManagedApps;

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.organism = this.organismService.findOne(id);

		if (null == this.organism) {
			return Index.class;
		}

		this.agentList = this.agentProfileService.findAllAgentProfilesByOrganism(this.organism);

		fillAgentModelMap(this.agentList);

		this.selectedAgent = this.agentList.get(0);

		this.isManagedApplication = Boolean.FALSE;

		this.systemApplications = this.applicationService.findAllDefaultApplicationsByStructureTypeAndCustomer(
				this.organism.getStructureType(), this.organism.getCustomer());

		// BEWARE - FIX because before, reference deletion happened so that deletion on
		// subscribedApplications leads to delete objects on agent's structures organism
		// (update cascade)
		this.subscribedApplications = new ArrayList<Application>(this.getCustomerOfLoggedProfile().getApplications());

		// Remove system applications
		this.subscribedApplications.removeAll(this.systemApplications);

		this.selectedAgentManagedApps = new ArrayList<AgentManagedApplication>();

		// Check if loggedUser can access at this organism details
		return this.hasRights(this.organism);
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {

		if (!this.agentModelMap.containsKey(agentSelect)) {
			modificationRolesForm.recordError("Erreur");
			return null;
		}

		this.selectedAgent = this.agentModelMap.get(agentSelect);

		List<AgentManagedApplication> currentAgentManagedApps = agentManagedApplicationService
				.findAgentManagedApplicationsByAgent(selectedAgent);
		try {

			Role appManagerRole = this.roleService.findByName(RoleProfile.ROLE_APPLICATION_MANAGER_AGENT.toString());

			// If at least on app has been selected,
			// we add the role of app manager to the user
			// (if he doesn't already have it)
			if (!this.selectedAgentManagedApps.isEmpty() && !this.selectedAgent.getRoles().contains(appManagerRole)) {
				this.selectedAgent.addRole(appManagerRole);
				this.agentProfileService.update(this.selectedAgent);
				logger.debug("ROLE_APPLICATION_MANAGER_AGENT has been added for agent #" + this.selectedAgent.getId());
			}

			for (AgentManagedApplication managedApp : this.selectedAgentManagedApps) {
				managedApp.setAgentProfile(selectedAgent);
				if (!currentAgentManagedApps.contains(managedApp)) {
					this.agentManagedApplicationService.create(managedApp);
				}
			}

			this.listAppManagers.setOrganism(this.organism);
			this.listAppManagers.setSuccessMessage(this.getMessages().get("recording-success-message"));
			return this.listAppManagers;
		} catch (Exception e) {
			logger.error(
					"Modification of Application managers has failed for Organism #" + this.organism.getId() + "\n", e);
			this.modificationRolesForm.recordError(this.getMessages().get("recording-error-message"));
			return null;
		}
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
	}

	public GenericListEncoder<Application> getSubscribedApplicationEncoder() {
		GenericListEncoder<Application> encoder = new GenericListEncoder<Application>(this.subscribedApplications);
		return encoder;
	}

	/**
	 * This method allows to fill the autocomplete field
	 *
	 * @param searchValue : the string to search
	 * @return a string list of agent profiles which corresponding at the search
	 */
	public List<String> onProvideCompletionsFromAgentSelect(String searchValue) {
		List<String> foundListAgentsToString = new ArrayList<String>();

		for (Map.Entry<String, AgentProfile> entry : this.agentModelMap.entrySet()) {
			if (entry.getKey().toLowerCase().contains(searchValue.toLowerCase())) {
				foundListAgentsToString.add(entry.getKey());
			}
		}

		return foundListAgentsToString;
	}

	public Boolean getIsManagedApplication() {
		return isManagedApplication;
	}

	public void setIsManagedApplication(Boolean isManagedApplication) {
		this.isManagedApplication = isManagedApplication;

		AgentManagedApplication managedApp = new AgentManagedApplication(selectedAgent, this.organism, applicationLoop);

		if (isManagedApplication) {
			if (!this.selectedAgentManagedApps.contains(managedApp)) {
				this.selectedAgentManagedApps.add(managedApp);
			}
		} else {
			this.selectedAgentManagedApps.remove(managedApp);
		}
	}

	private void fillAgentModelMap(List<AgentProfile> agentList) {
		agentModelMap = new LinkedHashMap<String, AgentProfile>();
		for (AgentProfile agent : agentList) {
			String username = agent.getUser().getFirstname() + " " + agent.getUser().getLastname();
			agentModelMap.put(username + " (" + agent.getUsername() + ")", agent);
		}
	}
}