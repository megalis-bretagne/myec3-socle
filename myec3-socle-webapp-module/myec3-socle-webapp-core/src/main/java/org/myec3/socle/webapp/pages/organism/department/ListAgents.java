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
package org.myec3.socle.webapp.pages.organism.department;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.beanmodel.PropertyConduit;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.beanmodel.services.PropertyConduitSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Page used to display the list of agents{@link AgentProfile} contained in the
 * organism department {@link OrganismDepartment}.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/organism/department/ListAgents.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
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
	 * {@link AgentProfile} objects
	 */
	@Inject
	@Named("agentProfileService")
	private AgentProfileService agentProfileService;

	@SuppressWarnings("unused")
	@Inject
	private JavaScriptSupport javaScriptSupportF;

	@Inject
	private PropertyConduitSource propertyConduitSource;

	@Inject
	private BeanModelSource beanModelSource;

	@SuppressWarnings("unused")
	@Property
	private List<AgentProfile> agentProfilesResult;

	@SuppressWarnings("unused")
	@Property
	private Integer rowIndex;

	@Property
	private AgentProfile agentProfileRow;

	private OrganismDepartment organismDepartment;

	private Organism organism;

	private AgentProfile agentProfile;

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation(String id) {
		super.initUser();

		this.organismDepartment = this.organismDepartmentService.findOne(Long.valueOf(id));
		if (null == this.organismDepartment) {
			return Boolean.FALSE;
		}

		// Check if loggedUser can access at this page
		return this.hasRights(this.organismDepartment.getOrganism());
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {

		if (this.organismDepartment != null) {
			return this.organismDepartment.getId();
		}

		return null;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		return Index.class;
	}

	// Events
	@OnEvent(value = "action", component = "delete")
	public Object removeAgentProfile(Long id) {
		AgentProfile agentProfileToDelete = this.agentProfileService.findOne(id);
		this.setOrganismDepartment(agentProfileToDelete.getOrganismDepartment());

		this.agentProfileService.softDelete(id);
		this.synchronizationService.notifyDeletion(agentProfileToDelete);
		this.successMessage = this.getMessages().get("delete-success");

		return this;
	}

	@OnEvent(value = "action", component = "disable")
	public Object disableAgentProfile(Long id) {
		AgentProfile agentProfileToDisable = this.agentProfileService.findOne(id);
		agentProfileToDisable.getUser().setEnabled(false);
		agentProfileToDisable.getUser().setLastname(agentProfileToDisable.getUser().getLastname() + " (désactivé)");
		this.agentProfileService.update(agentProfileToDisable);
		this.synchronizationService.notifyUpdate(agentProfileToDisable);
		this.successMessage = this.getMessages().get("disable-success");
		return this;
	}

	@OnEvent(value = "action", component = "enable")
	public Object enabledAgentProfile(Long id) {
		AgentProfile agentProfileToEnable = this.agentProfileService.findOne(id);
		agentProfileToEnable.getUser().setEnabled(true);
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
		PropertyConduit propCdtAttributeExpirationDatePassword = this.propertyConduitSource.create(AgentProfile.class, "user.expirationDatePassword");

		model.add("user", propCdtAttributeLastName).sortable(true);
		model.add("expirationDatePassword", propCdtAttributeExpirationDatePassword).sortable(true);
		model.add("actions", null);
		model.include("user", "email", "username", "expirationDatePassword", "actions");
		return model;


	}

	public OrganismDepartment getOrganismDepartment() {
		return organismDepartment;
	}

	public void setOrganismDepartment(OrganismDepartment organismDepartment) {
		this.organismDepartment = organismDepartment;
	}

	// Getters n Setters
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
	 * Check if current agent is enabled
	 * @return
	 */
	public Boolean getIsEnabled() {
		if (agentProfileRow.getUser() == null) {
			return Boolean.FALSE;
		}
		return agentProfileRow.getUser().isEnabled();
	}
}