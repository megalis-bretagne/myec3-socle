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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.service.AgentManagedApplicationService;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.StructureApplicationService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.synchro.api.constants.SynchronizationRelationsName;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.inject.Named;
import java.util.*;

/**
 * Page used to modify roles{@link Role} of an agent
 * profile{@link AgentProfile}<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/organism/agent/ModifyRoles.
 * tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class ModifyRoles extends AbstractPage {

	private static Log logger = LogFactory.getLog(ModifyRoles.class);

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Inject
	@Named("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Inject
	@Named("roleService")
	private RoleService roleService;

	@Inject
	@Named("structureApplicationService")
	private StructureApplicationService structureApplicationService;

	@Inject
	@Named("agentManagedApplicationService")
	private AgentManagedApplicationService agentManagedApplicationService;

	@InjectPage
	private View viewAgentPage;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Component(id = "modification_form")
	private Form modificationRolesForm;

	@Property
	private List<Role> agentRoles;

	@Property
	private List<Application> subscribedApplications;

	@SuppressWarnings("unused")
	private Role roleSelected;

	/**
	 * Current application in loop
	 */
	private Application applicationLoop;

	private Role multipleRolesLoop;

	/**
	 * List of selected roles
	 */
	private List<Role> selectedRoles;

	/**
	 * List of the applications whose role must not be managed
	 */
	private List<Application> systemApplications;

	private List<Application> applicationsAllowingMultipleRoles;

	private List<Application> applicationsSelected;

	/**
	 * Agent to manage his roles
	 */
	private AgentProfile agentProfile;

	/**
	 * Return true in case of create , false otherwise
	 */
	private Boolean agentProfileCreation = Boolean.FALSE;

	private Boolean activeRole;

	@SuppressWarnings("unused")
	private Boolean activeMultipleRoles;

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

		this.selectedRoles = new ArrayList<>();
		this.applicationsSelected = new ArrayList<>();

		this.agentProfile = this.agentProfileService.findOne(profileId);
		if (null == this.agentProfile) {
			return Boolean.FALSE;
		}

		if (!this.agentProfile.getUser().isEnabled()) {
			return viewAgentPage;
		}
		this.systemApplications = this.applicationService.findAllDefaultApplicationsByStructureTypeAndCustomer(
				this.agentProfile.getOrganismDepartment().getOrganism().getStructureType(),
				this.agentProfile.getOrganismDepartment().getOrganism().getCustomer());

		this.applicationsAllowingMultipleRoles = this.applicationService
				.findAllApplicationsAllowingMultipleRolesByStructureTypeValue(StructureTypeValue.ORGANISM);

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

		this.agentRoles = this.agentProfile.getRoles();

		if (this.getIsOrganismAdmin()) {
			// BEWARE - FIX because before, reference deletion happened so that deletion on
			// subscribedApplications leads to delete objects on agent's structures organism
			// (update cascade)
			this.subscribedApplications = new ArrayList<>(
					this.agentProfile.getOrganismDepartment().getOrganism().getApplications());
		} else if (this.getIsApplicationManagerAgent()) {
			this.subscribedApplications = this.agentManagedApplicationService
					.findApplicationManagedByAgent((AgentProfile) this.getLoggedProfile());
		}

		// Remove system applications
		this.subscribedApplications.removeAll(this.systemApplications);

		Object hasRights = this.hasRights(this.agentProfile);

		if (hasRights instanceof Boolean) {
			return (Boolean) hasRights || this
					.hasRightsToManageOrganismApplications(this.agentProfile.getOrganismDepartment().getOrganism());
		}

		// Check if loggedUser can access to this page
		return hasRights;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public void onActivate(Long profileId, Boolean creation) {
		this.agentProfileCreation = creation;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object onPassivate() {
		List activationParameters = new ArrayList();
		activationParameters.add((this.agentProfile != null) ? this.agentProfile.getId() : null);
		activationParameters.add(this.agentProfileCreation);
		return activationParameters;
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {
			for (Application application : systemApplications) {
				Role aRole = this.roleService
						.findBasicRoleByProfileTypeAndApplication(this.agentProfile.getProfileType(), application);
				if (aRole != null) {
					this.selectedRoles.add(aRole);
				}
			}

			// We check that there is no the same role 2 times in the
			// selectRoles list
			List<Role> checkedRoles = new ArrayList<>();
			for (Role role : this.selectedRoles) {
				if (!checkedRoles.contains(role)) {
					if (isTooMuchSubscription(role)) {
						this.modificationRolesForm.recordError(String.format(this.getMessages().get("toomuchsubscription-error-message"), role.getApplication().getLabel()));
						return null;
					}
					checkedRoles.add(role);
				}
			}

			List<Role> agentCurrentRoles = this.agentProfile.getRoles();
			List<Role> agentCurrentNotHiddenRoles = this.agentProfile.getRolesWithoutHidden();
			List<Resource> rolesRemoved = new ArrayList<>();
			List<Resource> rolesAdded = new ArrayList<>();

			// Removing roles (if they were displayed)
			for (Role role : new ArrayDeque<Role>(agentCurrentNotHiddenRoles)) {
				if (this.subscribedApplications.contains(role.getApplication()) && !checkedRoles.contains(role)) {
					rolesRemoved.add(role);
					agentCurrentRoles.remove(role);
				}
			}

			// Adding roles (if they were displayed)
			for (Role role : checkedRoles) {
				if (this.subscribedApplications.contains(role.getApplication())
						&& !agentCurrentNotHiddenRoles.contains(role)) {
					rolesAdded.add(role);
					agentCurrentRoles.add(role);
				}
			}

			this.agentProfile.setRoles(agentCurrentRoles);

			this.agentProfileService.update(this.agentProfile);

			// Synchronize the update
			this.synchronizationService.notifyCollectionUpdate(this.agentProfile, SynchronizationRelationsName.ROLES,
					null, rolesAdded, rolesRemoved);

			this.viewAgentPage.setAgentProfile(this.agentProfile);

			if (this.getAgentProfileCreation()) {
				this.viewAgentPage.setSuccessMessage(this.getMessages().get("agent-role-success-message"));
			} else {
				this.viewAgentPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
			}
			return this.viewAgentPage;
		} catch (Exception e) {
			this.modificationRolesForm.recordError(this.getMessages().get("recording-error-message"));
			logger.error("ModifyRoles.onSuccess(): role couldn't be updated for Agent #" + this.agentProfile.getId(),
					e);
			return null;
		}
	}

	private boolean isTooMuchSubscription(Role role) {
		Long nbMaxLicenses = this.structureApplicationService.findByStructureAndApplication(this.agentProfile.getOrganismDepartment().getOrganism(), role.getApplication()).getnbMaxLicenses();
		if(nbMaxLicenses == null){
			return false;
		}
		List<AgentProfile> agentProfiles = this.agentProfileService.findAllAgentProfilesByOrganismAndApplication(this.agentProfile.getOrganismDepartment().getOrganism(), role.getApplication());
		long nbSubscription = agentProfiles.size();
		return nbSubscription >= nbMaxLicenses;

	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		this.viewAgentPage.setAgentProfile(this.agentProfile);
		return View.class;
	}

	// Getters n Setters
	public AgentProfile getAgentProfile() {
		return agentProfile;
	}

	public void setAgentProfile(AgentProfile agentProfile) {
		this.agentProfile = agentProfile;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public GenericListEncoder<Role> getRoleEncoder() {
		List<Role> availableRoles = this.roleService.findAllRoleByProfileTypeAndApplicationWithoutHidden(
				this.agentProfile.getProfileType(), this.applicationLoop);
		return new GenericListEncoder<>(availableRoles);
	}

	public Map<Role, String> getRolesModel() {
		List<Role> availableRoles = this.roleService.findAllRoleByProfileTypeAndApplicationWithoutHidden(
				this.agentProfile.getProfileType(), this.applicationLoop);

		Map<Role, String> roles = new LinkedHashMap<>();
		for (Role role : availableRoles) {
			roles.put(role, role.getLabel());
		}

		return roles;
	}

	public List<Role> getMultipleRolesModel() {

		return  this.roleService.findAllRoleByProfileTypeAndApplicationWithoutHidden(
				this.agentProfile.getProfileType(), this.applicationLoop);
	}

	public GenericListEncoder<Application> getSubscribedApplicationEncoder() {
		return new GenericListEncoder<>(this.subscribedApplications);
	}

	public Application getApplicationLoop() {
		return applicationLoop;
	}

	public void setApplicationLoop(Application applicationLoop) {
		this.applicationLoop = applicationLoop;
	}

	public Role getMultipleRolesLoop() {
		return multipleRolesLoop;
	}

	public void setMultipleRolesLoop(Role multipleRolesLoop) {
		this.multipleRolesLoop = multipleRolesLoop;
	}

	public Role getRoleSelected() {
		for (Role role : this.agentRoles) {
			if (role.getApplication().equals(this.applicationLoop)) {
				return role;
			}
		}

		return null;
	}

	public void setRoleSelected(Role roleSelected) {
		// If there is only application GU in the list
		if ((this.activeRole == null)
				&& (MyEc3ApplicationConstants.GU.equals(roleSelected.getApplication().getName())
						|| MyEc3ApplicationConstants.GED_SERVICE.equals(roleSelected.getApplication().getName()))) {
			this.selectedRoles.add(roleSelected);
		} else if ((this.activeRole)
				|| (MyEc3ApplicationConstants.GU.equals(roleSelected.getApplication().getName()))) {
			this.selectedRoles.add(roleSelected);
		}
	}

	public Boolean getActiveRole() {
		for (Role role : this.agentRoles) {
			if (role.getApplication().equals(this.applicationLoop)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public void setActiveRole(Boolean activeRole) {
		this.activeRole = activeRole;
		if (activeRole) {
			if (!this.applicationsSelected.contains(this.applicationLoop)) {
				this.applicationsSelected.add(this.applicationLoop);
			}
		} else {
			if (this.applicationsSelected.contains(this.applicationLoop)) {
				this.applicationsSelected.remove(this.applicationLoop);
			}
		}
	}

	public Boolean getActiveMultipleRoles() {
		for (Role role : this.agentRoles) {
			if ((role.getApplication().equals(this.applicationLoop)) && (role.equals(this.multipleRolesLoop))) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public void setActiveMultipleRoles(Boolean activeMultipleRoles) {
		if (activeMultipleRoles && this.applicationsSelected.contains(this.multipleRolesLoop.getApplication())) {
			this.selectedRoles.add(this.multipleRolesLoop);
		} else {
			if ((this.multipleRolesLoop != null) && this.selectedRoles.contains(this.multipleRolesLoop)) {
				this.selectedRoles.remove(this.multipleRolesLoop);
			}
		}
	}

	public Boolean getIsCurrentApplicationEnabled() {
		return MyEc3ApplicationConstants.GU.equals(this.applicationLoop.getName())
				|| MyEc3ApplicationConstants.GED_SERVICE.equals(this.applicationLoop.getName())
				|| MyEc3ApplicationConstants.OC_PROJETS_SERVICE.equals(this.applicationLoop.getName())
				|| MyEc3ApplicationConstants.OC_REUNIONS_SERVICE.equals(this.applicationLoop.getName())
				|| MyEc3ApplicationConstants.OC_BLOC_NOTES_SERVICE.equals(this.applicationLoop.getName());
	}

	public Boolean getIsCurrentApplicationOC() {
		return MyEc3ApplicationConstants.OC_PROJETS_SERVICE.equals(this.applicationLoop.getName())
				|| MyEc3ApplicationConstants.OC_REUNIONS_SERVICE.equals(this.applicationLoop.getName())
				|| MyEc3ApplicationConstants.OC_BLOC_NOTES_SERVICE.equals(this.applicationLoop.getName());
	}

	public Boolean getIsCurrentApplicationGU() {
		return MyEc3ApplicationConstants.GU.equals(this.applicationLoop.getName());
	}

	public Boolean getAgentProfileCreation() {
		return this.agentProfileCreation;
	}

	public void setAgentProfileCreation(Boolean agentProfileCreation) {
		this.agentProfileCreation = agentProfileCreation;
	}

	public Boolean getIsApplicationAllowingMultipleRoles() {
		if (this.applicationsAllowingMultipleRoles != null && this.applicationsAllowingMultipleRoles.contains(this.applicationLoop)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
