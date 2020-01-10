package org.myec3.socle.webapp.pages.organism.appmanager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
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
 * Created by a602499 on 15/12/2016.
 */
public class ListAppManagers extends AbstractPage {

	private static Log logger = LogFactory.getLog(ListAppManagers.class);

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

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Property
	private List<Application> subscribedApplications;

	@Property
	private List<AgentManagedApplication> appManagers;

	/**
	 * Current application in loop
	 */
	@Property
	private Application applicationLoop;

	@Property
	private AgentManagedApplication appManagerLoop;

	private Organism organism;

	/**
	 * List of the applications whose role must not be managed
	 */
	private List<Application> systemApplications;

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

		this.systemApplications = this.applicationService.findAllDefaultApplicationsByStructureTypeAndCustomer(
				this.organism.getStructureType(), this.organism.getCustomer());

		// BEWARE - FIX because before, reference deletion happened so that deletion on
		// subscribedApplications leads to delete objects on agent's structures organism
		// (update cascade)
		this.subscribedApplications = new ArrayList<Application>(this.getCustomerOfLoggedProfile().getApplications());

		// Remove system applications
		this.subscribedApplications.removeAll(this.systemApplications);

		/*
		 * this.appManagers = new HashMap<Application, List<AgentProfile>>();
		 * for(Application app: this.subscribedApplications) { this.appManagers.put(
		 * app, this.agentManagedApplicationService.
		 * findAgentManagedApplicationsByApplicationAndOrganism(app, this.organism) ); }
		 */

		// Check if loggedUser can access at this organism details
		return this.hasRights(this.organism);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
	}

	@OnEvent(value = "action", component = "delete")
	public Object removeAppManager(Long id) {
		try {
			AgentManagedApplication agentAppManagerToDelete = this.agentManagedApplicationService.findOne(id);
			this.agentManagedApplicationService.delete(agentAppManagerToDelete);

			// Retrieve remainings app managed by the agent we have removed
			AgentProfile deletedAgent = agentAppManagerToDelete.getAgentProfile();
			List<AgentManagedApplication> currentAgentManagedApps = this.agentManagedApplicationService
					.findAgentManagedApplicationsByAgent(deletedAgent);

			// If the agent doesn't manage any applications anymore,
			// we remove his role as an app manager
			if (currentAgentManagedApps.isEmpty()) {
				Role appManagerRole = this.roleService
						.findByName(RoleProfile.ROLE_APPLICATION_MANAGER_AGENT.toString());
				deletedAgent.removeRole(appManagerRole);
				this.agentProfileService.update(deletedAgent);
				logger.debug("ROLE_APPLICATION_MANAGER_AGENT has been removed for agent #" + deletedAgent.getId());
			}

			this.successMessage = this.getMessages().get("delete-success");
			return this;
		} catch (Exception e) {
			logger.error("Application manager deletion has failed for Organism #" + this.organism.getId() + "\n", e);
			this.errorMessage = this.getMessages().get("delete-error");
			return this;
		}
	}

	public List<AgentManagedApplication> getAppManagers(Application application) {
		this.appManagers = this.agentManagedApplicationService
				.findAgentManagedApplicationsByApplicationAndOrganism(application, this.organism);

		return this.appManagers;
	}

	public GenericListEncoder<Application> getSubscribedApplicationEncoder() {
		GenericListEncoder<Application> encoder = new GenericListEncoder<Application>(this.subscribedApplications);
		return encoder;
	}

	public GenericListEncoder<AgentManagedApplication> getAppManagersEncoder() {
		GenericListEncoder<AgentManagedApplication> encoder = new GenericListEncoder<AgentManagedApplication>(
				this.appManagers);
		return encoder;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}
}
