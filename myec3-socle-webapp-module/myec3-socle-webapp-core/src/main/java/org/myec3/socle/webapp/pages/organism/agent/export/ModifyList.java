package org.myec3.socle.webapp.pages.organism.agent.export;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.PropertyConduit;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;
import org.myec3.socle.core.constants.MyEc3EsbConstants;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationQueue;
import org.myec3.socle.synchro.core.service.SynchronizationQueueService;
import org.myec3.socle.webapp.components.AgentListFilter;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to display the list of {@link AgentProfile} to update.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/organism/agent/export/ModifyList.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class ModifyList extends AbstractImport {

	private static final Logger logger = LogManager.getLogger(ModifyList.class);

	@Component(id = "filter_form")
	private AgentListFilter agentListFilter;

	@Component(id = "list_agents_form")
	private Form form;

	@Inject
	@Named("synchronizationQueueService")
	private SynchronizationQueueService synchronizationQueueService;

	@Inject
	@Named("organismService")
	private OrganismService organismService;

	@Inject
	@Named("profileService")
	private ProfileService profileService;

	@Inject
	@Named("agentProfileService")
	private AgentProfileService agentProfileService;

	@Persist
	private List<AgentProfile> agentsToModify;

	@Persist
	private Organism organism;

	/* GRID ATTRIBUTES */
	@SuppressWarnings("unused")
	@Property
	private Integer rowIndex;

	@Property
	private AgentProfile currentRow;

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private PropertyConduitSource propertyConduitSource;

	// CHECKBOX
	@SuppressWarnings("unused")
	@Property
	private boolean selectAll;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private HashSet<AgentProfile> selectedRows;

	@SuppressWarnings("unused")
	@InjectComponent
	@Property
	private Checkbox masterCheckbox;

	// FILTER
	@Persist(PersistenceConstants.FLASH)
	private List<AgentProfile> agentFilterResult;

	private boolean updateAll = false;

	/* EVENTS */

	void onSelectedFromUpdateAll() {
		this.updateAll = true;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.organism = this.organismService.findOne(id);

		if (this.organism == null) {
			return Index.class;
		}

		this.agentsToModify = this.getReportPage().getAgentsToModify();

		if (this.agentsToModify == null) {
			return Report.class;
		}

		// Check if loggedUser can access at this page
		return this.hasRights(this.organism);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "list_agents_form")
	public void onValidate() {
		if (!this.updateAll) {
			/* return an error if no checkbox are checked */
			if (this.selectedRows.isEmpty()) {
				this.form.recordError(this.getMessages().get("empty-selected-error-message"));
			}
		}
	}

	@OnEvent(value = EventConstants.SUCCESS, component = "list_agents_form")
	public Object onSuccess() {
		if (this.updateAll) {
			return this.onUpdateAll();
		} else {
			try {
				// We look into the hashset to retrieve all agents to delete
				Iterator<AgentProfile> it = this.selectedRows.iterator();

				while (it.hasNext()) {
					AgentProfile agent = it.next();
					this.prepareAndUpdateAgent(agent);

					// Add user into synchronization queue
					this.synchronizationQueueService
							.create(new SynchronizationQueue(agent.getId(), ResourceType.AGENT_PROFILE,
									SynchronizationJobType.UPDATE, MyEc3EsbConstants.getApplicationSendingJmsName()));

					// We remove users updated from the list of users to
					// update
					this.agentsToModify.remove(agent);
				}

				// In case of all agents are updated successfully we return to
				// the
				// report page
				if (this.agentsToModify.isEmpty()) {
					this.getReportPage().setOrganism(this.organism);
					this.getReportPage().setSuccessMessage(this.getMessages().get("update-users-success-message"));
					this.clearPersistentParams();
					return this.getReportPage();
				}

				this.setSuccessMessage(this.getMessages().get("update-users-selected-success-message"));

				// return current page
				return this;

			} catch (Exception e) {
				logger.error("An unexpected error has occured during update selected users", e);
				this.setErrorMessage(this.getMessages().get("update-users-selected-error-message"));
				return this;
			}
		}
	}

	/**
	 * Prepare the agent before the update operation and perform the update
	 * 
	 * @param agent : the agent to update
	 * @throws Exception
	 */
	public void prepareAndUpdateAgent(AgentProfile agent) throws Exception {
		// We check if email = username.
		if (!agent.getEmail().equalsIgnoreCase(agent.getUser().getUsername())) {

			// We must check if the username isn't already used
			if (!this.profileService.usernameAlreadyExists(agent.getEmail(), agent)) {
				// If username not already used we set usermane = email
				agent.getUser().setUsername(agent.getEmail());
			}
		}

		// Delayed synchronization task
		// agent.setOperationDelayed(Boolean.TRUE);

		// Update the agent
		this.agentProfileService.update(agent);
	}

	/* ACTION LINK */

	/**
	 * Action performed when an user wants to filter a list of agents
	 * 
	 * @see AgentListFilter
	 */
	@OnEvent(value = EventConstants.SUCCESS, component = "filter_form")
	public Object onFilterSuccess() {
		this.agentFilterResult = this.agentListFilter.getAgentsMatching();
		return this;
	}

	/**
	 * Action performed when an user wants to update all agents
	 */
	public Object onUpdateAll() {
		try {
			// Delete all users
			for (AgentProfile agent : this.agentsToModify) {
				this.prepareAndUpdateAgent(agent);

				// Add user into synchronization queue
				this.synchronizationQueueService
						.create(new SynchronizationQueue(agent.getId(), ResourceType.AGENT_PROFILE,
								SynchronizationJobType.UPDATE, MyEc3EsbConstants.getApplicationSendingJmsName()));
			}
		} catch (Exception ex) {
			logger.error("An error has occured during update all agents.", ex);
			this.setErrorMessage(this.getMessages().get("update-users-error-message"));
			return this;
		}

		// Clear all agents contained in list
		this.agentsToModify.clear();

		// In case of success we return to the report page
		this.getReportPage().setOrganism(this.organism);
		this.getReportPage().setSuccessMessage(this.getMessages().get("update-users-success-message"));
		this.clearPersistentParams();
		return this.getReportPage();
	}

	/***********************/
	/** GETTER AND SETTER **/
	/***********************/

	/**
	 * @return a list of agent profiles to delete (fill the grid displayed)
	 */
	public List<AgentProfile> getAgentProfileList() {
		// If filter used
		if (this.agentFilterResult != null) {
			return this.agentFilterResult;
		}

		// ELSE we use all the list
		if (this.agentsToModify != null) {
			return this.agentsToModify;
		}

		return new ArrayList<AgentProfile>();
	}

	/**
	 * @return : bean model to fill the grid
	 */
	public BeanModel<AgentProfile> getGridModel() {
		BeanModel<AgentProfile> model = this.beanModelSource.createDisplayModel(AgentProfile.class, this.getMessages());

		PropertyConduit propCdtAttributeLastname = this.propertyConduitSource.create(AgentProfile.class,
				"user.lastname");
		PropertyConduit propCdtAttributeFirstname = this.propertyConduitSource.create(AgentProfile.class,
				"user.firstname");

		model.add("lastname", propCdtAttributeLastname).sortable(true);
		model.add("firstname", propCdtAttributeFirstname).sortable(true);
		model.add("checkBoxField", null);
		model.include("firstname", "lastname", "email", "checkBoxField");
		return model;
	}

	/**
	 * Check if the agentProfileRow is the current LoggedUser
	 * 
	 * @return TRUE if the agentProfileRow is the currentLoggedUser, else FALSE
	 */
	public Boolean getIsAgentRowLogged() {
		if (this.getLoggedProfileExists()) {
			if ((null != this.getLoggedProfile()) && (null != this.getLoggedProfile().getId())) {
				if (this.getLoggedProfile().equals(this.currentRow)) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	/* CHECKBOX */
	/**
	 * Test if the current row is selected
	 */
	public boolean getCurrentRowSelected() {
		if (selectedRows == null) {
			selectedRows = new HashSet<AgentProfile>();
		}
		return selectedRows.contains(currentRow);
	}

	/**
	 * Add into hashset all users selected
	 * 
	 * @param value
	 */
	public void setCurrentRowSelected(boolean value) {
		if (selectedRows == null) {
			selectedRows = new HashSet<AgentProfile>();
		}
		if (value) {
			selectedRows.add(currentRow);
		} else {
			selectedRows.remove(currentRow);
		}
	}

	public List<AgentProfile> getAgentsToModify() {
		return agentsToModify;
	}

	public void setAgentsToModify(List<AgentProfile> agentsToModify) {
		this.agentsToModify = agentsToModify;
	}

	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}
}
