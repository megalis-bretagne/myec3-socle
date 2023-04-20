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
import org.apache.tapestry5.beanmodel.PropertyConduit;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.beanmodel.services.PropertyConduitSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.services.javascript.StylesheetLink;
import org.apache.tapestry5.services.javascript.StylesheetOptions;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.inject.Named;
import java.util.*;

/**
 * Page used to display the organism's{@link Organism} agents list
 * {@link AgentProfile}.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/agent/ListAgents.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
@SuppressWarnings("unused")
public class ListAgents extends AbstractPage {

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link OrganismDepartment} objects
	 */
	@Inject
	@Named("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Inject
	@Named("agentProfileService")
	private AgentProfileService agentProfileService;

	private OrganismDepartment organismDepartment;

	private Organism organism;

	private AgentProfile agentProfile;

	@Property
	private Boolean isOrganismDepartment = Boolean.FALSE;

	@Property
	private List<AgentProfile> agentProfilesResult;

	@Property
	private Integer rowIndex;

	@Property
	private AgentProfile agentProfileRow;

	@Inject
	private PropertyConduitSource propertyConduitSource;

	// Services
	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Inject
	private BeanModelSource beanModelSource;

	@SetupRender
	public void includePrintCss() {
		javaScriptSupport.importStylesheet(new StylesheetLink("/static/css/print.css", new StylesheetOptions("print")));
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation(Long organismId) {
		this.organism = this.organismService.findOne(organismId);

		if (null == this.organism) {
			return Boolean.FALSE;
		} else {
			this.organismDepartment = this.organismDepartmentService.findRootOrganismDepartment(this.organism);

			isOrganismDepartment = Boolean.FALSE;

			Object hasRights = this.hasRights(this.organism);

			if (hasRights instanceof Boolean) {
				return (Boolean) hasRights || this.hasRightsToManageOrganismApplications(this.organism);
			}

			// Check if loggedUser can access to this page
			return hasRights;
		}
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation(Long organismId, Long organismDepartmentId) {
		this.organismDepartment = this.organismDepartmentService.findOne(organismDepartmentId);
		if (null == this.organismDepartment) {
			this.organism = this.organismService.findOne(Long.valueOf(organismId));
			this.organismDepartment = this.organismDepartmentService.findRootOrganismDepartment(this.organism);
			if (null == this.organism) {
				return Boolean.FALSE;
			}
		} else {
			isOrganismDepartment = Boolean.TRUE;
		}

		Object hasRights = this.hasRights(this.organismDepartment.getOrganism());

		if (hasRights instanceof Boolean) {
			return (Boolean) hasRights
					|| this.hasRightsToManageOrganismApplications(this.organismDepartment.getOrganism());
		}

		// Check if loggedUser can access to this page
		return hasRights;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Object onPassivate() {
		List<Long> result = new ArrayList<Long>();
		if (this.organism != null) {
			result.add(this.organism.getId());
		}
		if (this.organismDepartment != null && isOrganismDepartment) {
			if (!result.contains(this.organismDepartment.getOrganism().getId())) {
				result.add(this.organismDepartment.getOrganism().getId());
			}
			result.add(this.organismDepartment.getId());
		}
		return result;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		return Index.class;
	}

	// Events
	@OnEvent(value = "action", component = "delete")
	public Object removeAgentProfile(Long id) {
		AgentProfile agentProfileToDelete = this.agentProfileService.findOne(id);
		// return user to user list of department. Otherwise display all agents
		// of the organism
		if (isOrganismDepartment) {
			this.setOrganismDepartment(agentProfileToDelete.getOrganismDepartment());
		}

		this.agentProfileService.softDelete(id);
		this.synchronizationService.notifyDeletion(agentProfileToDelete);
		this.successMessage = this.getMessages().get("delete-success");
		return this;
	}

	@OnEvent(value = "action", component = "disable")
	public Object disableAgentProfile(Long id) {
		AgentProfile agentProfileToDisable = this.agentProfileService.findOne(id);
		agentProfileToDisable.getUser().setEnabled(false);

		// Used for e-bourgogne GRC
		agentProfileToDisable.getUser().setLastname(agentProfileToDisable.getUser().getLastname() + " (désactivé)");

		this.agentProfileService.update(agentProfileToDisable);
		this.synchronizationService.notifyUpdate(agentProfileToDisable);
		this.successMessage = this.getMessages().get("disable-success");
		return this;
	}

	@OnEvent(value = "action", component = "enable")
	public Object enableAgentProfile(Long id) {
		AgentProfile agentProfileToEnable = this.agentProfileService.findOne(id);
		agentProfileToEnable.getUser().setEnabled(true);

		// Used for e-bourgogne GRC
		agentProfileToEnable.getUser()
				.setLastname(agentProfileToEnable.getUser().getLastname().replace(" (désactivé)", ""));

		this.agentProfileService.update(agentProfileToEnable);
		this.synchronizationService.notifyUpdate(agentProfileToEnable);
		this.successMessage = this.getMessages().get("enable-success");
		return this;
	}

	// Getters n Setters
	public List<AgentProfile> getAgentProfileList() {
		if (this.organism != null) {
			List<OrganismDepartment> organismDepartments = this.organismDepartmentService
					.findAllDepartmentByOrganism(this.organism);
			List<AgentProfile> aProfiles = new ArrayList<AgentProfile>();
			for (OrganismDepartment oDepartment : organismDepartments) {
				aProfiles.addAll(this.agentProfileService.findAllAgentProfilesByOrganismDepartment(oDepartment));
			}
			return aProfiles;
		}
		return this.agentProfileService.findAllAgentProfilesByOrganismDepartment(this.organismDepartment);
	}

	/**
	 * @return : bean model
	 */
	public BeanModel<AgentProfile> getGridModel() {
		BeanModel<AgentProfile> model = this.beanModelSource.createDisplayModel(AgentProfile.class, this.getMessages());

		PropertyConduit propCdtAttributeLastName = this.propertyConduitSource.create(AgentProfile.class, "user.lastname");

		model.add("user", propCdtAttributeLastName).sortable(true);
		model.add("guRoles", null);
		model.add("actions", null);
		model.include("user", "email", "username", "guRoles", "actions");
		return model;
	}

	public OrganismDepartment getOrganismDepartment() {
		return organismDepartment;
	}

	public void setOrganismDepartment(OrganismDepartment organismDepartment) {
		this.organismDepartment = organismDepartment;
	}

	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}

	public AgentProfile getAgentProfile() {
		return agentProfile;
	}

	public void setAgentProfile(AgentProfile agentProfile) {
		this.agentProfile = agentProfile;
	}

	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	public Integer getResultsNumber() {
		if (null == this.getAgentProfileList())
			return 0;
		return this.getAgentProfileList().size();
	}

	/**
	 * Check if the agentProfileRow is the current LoggedUser
	 * 
	 * @return TRUE if the agentProfileRow is the currentLoggedUser, else FALSE
	 */
	public Boolean getIsAgentRowLogged() {
		if (this.getLoggedProfileExists()) {
			if ((null != this.getLoggedProfile()) && (null != this.getLoggedProfile().getId())) {
				if (this.getLoggedProfile().equals(this.agentProfileRow)) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}


	/**
	 * Check if current Agent is enabled
	 * @return
	 */
	public Boolean getIsEnabled() {
		if (agentProfileRow.getUser() == null) {
			return Boolean.FALSE;
		}
		return agentProfileRow.getUser().isEnabled();
	}

	public String getSocleRolesAsString() {
		SortedSet<String> guRoles = new TreeSet<>();
		//Vérification que la ligne du tableau organism/agents contient bien un utilisateur
		if (agentProfileRow.getUser() == null) {
			return "";
		}
		//Récupération de l'application socle
		Application guApplication = applicationService.findByName(MyEc3ApplicationConstants.GU);
		for (Profile profile: agentProfileRow.getUser().getProfiles()) {
			//Récupération du/des roles de l'agents dans l'application socle
			List<Role> roles = roleService.findAllRoleByProfileAndApplication(profile,guApplication);
			//On récupère le label en évitant les doublons
			roles.forEach(r -> guRoles.add(r.getLabel()));
		}
		return String.join(", ", guRoles);
	}
}