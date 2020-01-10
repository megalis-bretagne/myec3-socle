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
package org.myec3.socle.webapp.pages.organism;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.synchro.api.constants.SynchronizationRelationsName;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Page used to mangage the applications{@link Application} subscribed by the
 * organism{@link Organism}<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/organism/ManageSubscriptions.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
@Import(library = { "context:static/js/manageSubscriptions.js" })
public class ManageSubscriptions extends AbstractPage {

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	@Property
	private String errorMessage;

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Inject
	@Named("roleService")
	private RoleService roleService;

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

	@InjectPage
	private View viewPage;

	@InjectPage
	private DetailOrganismApplications detailOrganismApplicationsPage;

	@Persist
	private Set<Application> selectedApplications;

	@Property
	private Long idDouble;

	@Property
	private Organism organism;

	@Inject
	private PropertyAccess propertyAccess;

	private Application applicationLoop;

	private List<Application> choosenApplications;

	private GenericListEncoder<Application> applicationEncoder;

	private List<Application> availableApplications;

	public boolean beginRender() {
		List<Application> subscribableApplications = this.applicationService
				.findAllApplicationByStructure(this.organism);
		selectedApplications = new HashSet<Application>();
		for (Application application : subscribableApplications) {
			selectedApplications.add(application);
		}
		setSelectedApplications(selectedApplications);

		return true;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return viewPage;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.organism = this.organismService.findOne(id);
		if (null == this.organism) {
			return false;
		}

		if (null == this.organism.getAddress()) {
			this.organism.setAddress(new Address());
		}

		this.availableApplications = applicationService.findAllApplicationSubscribableByStructureTypeAndCustomer(
				this.organism.getStructureType(), this.organism.getCustomer());

		this.applicationEncoder = new GenericListEncoder<Application>(this.availableApplications);

		this.choosenApplications = new ArrayList<Application>();

		// Check if loggedUser can access to this organism
		return this.hasRights(this.organism);
	}

	/**
	 * @param id : organism id
	 * @return : boolean
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Boolean onActivate(Long id, Long idDouble) {
		this.organism = this.organismService.findOne(id);
		if (null == this.organism) {
			return false;
		}

		if (null == this.organism.getAddress()) {
			this.organism.setAddress(new Address());
		}

		this.availableApplications = applicationService.findAllApplicationSubscribableByStructureTypeAndCustomer(
				this.organism.getStructureType(), this.organism.getCustomer());

		this.applicationEncoder = new GenericListEncoder<Application>(this.availableApplications);
		this.idDouble = idDouble;
		this.choosenApplications = new ArrayList<Application>();

		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {

		try {
			// add default application
			List<Application> defaultApplications = this.applicationService
					.findAllDefaultApplicationsByStructureTypeAndCustomer(this.organism.getStructureType(),
							this.organism.getCustomer());
			for (Application application : defaultApplications) {
				this.choosenApplications.add(application);
			}

			// Remove agents roles if applications have been removed
			List<Application> oldApplications = this.applicationService.findAllApplicationByStructure(this.organism);
			List<AgentProfile> agentList = new ArrayList<AgentProfile>();
			for (OrganismDepartment organismDepartment : this.organism.getDepartments()) {
				agentList.addAll(organismDepartment.getAgents());
			}

			for (Application application : oldApplications) {
				if (!this.choosenApplications.contains(application)) {
					for (AgentProfile agent : agentList) {
						List<Role> aRoleList = this.roleService.findAllRoleByProfileAndApplication(agent, application);
						List<Resource> rolesRemoved = new ArrayList<Resource>();

						for (Role role : aRoleList) {
							agent.removeRole(role);
							rolesRemoved.add(role);
						}

						if (!rolesRemoved.isEmpty()) {
							// if agent is still active and is not disabled, we send a notification to
							// unchecked applications and GRC
							// Fixed PHT-EB-PROD-1171 : don't send a PUT operation to distant applications
							// whereas some agents are already deleted on it. Some agents are reactivated
							// wrongly.
							if (agent.isEnabled()) {
								// Clone object to correct concurency acces leading to crazy errors
								// jira PHT-EB-PROD-1129
								AgentProfile newAgent = agent.cloneForSynchro();

								// Notify the other applications
								this.synchronizationService.notifyCollectionUpdate(newAgent,
										SynchronizationRelationsName.ROLES, null, null, rolesRemoved);
							}
						}
					}
				}
			}

			for (AgentProfile agentProfile : agentList) {
				this.agentProfileService.update(agentProfile);
			}
			// Check if list of organism's applications has changed before
			// setting the new list
			Boolean organismApplicationsChanged = this.organismApplicationsHasChanged();

			this.organism.setApplications(this.choosenApplications);
			this.organismService.update(this.organism);

			// If the list of organism's applications has changed we send a
			// notification to external applications
			if (organismApplicationsChanged) {
				this.synchronizationService.notifyUpdate(this.organism);
			}

		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("recording-error-message");
			return null;
		}

		if (this.idDouble == null) {
			this.viewPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
			this.viewPage.setOrganism(this.organism);
			return this.viewPage;
		} else {
			this.detailOrganismApplicationsPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
			this.detailOrganismApplicationsPage.setOrganism(this.organism);
			return this.detailOrganismApplicationsPage;
		}
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		this.viewPage.setOrganism(this.organism);
		return View.class;
	}

	public Boolean organismApplicationsHasChanged() {
		List<Application> organismCurrentApplications = this.organism.getApplications();
		// applications removed
		for (Application application : organismCurrentApplications) {
			if (!this.choosenApplications.contains(application)) {
				return Boolean.TRUE;
			}
		}

		// applications added
		for (Application application : this.choosenApplications) {
			if (!organismCurrentApplications.contains(application)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public List<Application> getAvailableApplications() {
		return availableApplications;
	}

	public void setAvailableApplications(List<Application> availableApplications) {
		this.availableApplications = availableApplications;
	}

	public GenericListEncoder<Application> getApplicationEncoder() {
		return applicationEncoder;
	}

	public void setDetailOrganismApplicationsPage(DetailOrganismApplications detailOrganismApplicationsPage) {
		this.detailOrganismApplicationsPage = detailOrganismApplicationsPage;
	}

	public DetailOrganismApplications getDetailOrganismApplicationsPage() {
		return detailOrganismApplicationsPage;
	}

	public Application getApplicationLoop() {
		return applicationLoop;
	}

	public void setApplicationLoop(Application applicationLoop) {
		this.applicationLoop = applicationLoop;
	}

	public Set<Application> getSelectedApplications() {
		return selectedApplications;
	}

	public void setSelectedApplications(Set<Application> selectedApplications) {
		this.selectedApplications = selectedApplications;
	}

	public boolean isSelected() {
		return getSelectedApplications().contains(getApplicationLoop());
	}

	/**
	 * Add application to the selected set if selected.
	 */
	public void setSelected(boolean selected) {
		if (selected) {
			this.choosenApplications.add(getApplicationLoop());
		}
	}
}
