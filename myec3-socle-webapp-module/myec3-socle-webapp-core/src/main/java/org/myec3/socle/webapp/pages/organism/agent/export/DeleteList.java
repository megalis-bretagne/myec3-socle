package org.myec3.socle.webapp.pages.organism.agent.export;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.PropertyConduit;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;
import org.myec3.socle.core.constants.MyEc3EsbConstants;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationQueue;
import org.myec3.socle.synchro.core.service.SynchronizationQueueService;
import org.myec3.socle.webapp.components.AgentListFilter;
import org.myec3.socle.webapp.pages.Index;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Page used to display the list of {@link AgentProfile} to delete.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/organism/agent/export/DeleteList.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class DeleteList extends AbstractImport {

	private static final Logger logger = LogManager.getLogger(DeleteList.class);

	@InjectService("organismService")
	private OrganismService organismService;

	@InjectService("synchronizationQueueService")
	private SynchronizationQueueService synchronizationQueueService;

	@InjectService("agentProfileService")
	private AgentProfileService agentProfileService;

	@Component(id = "filter_form")
	private AgentListFilter agentListFilter;

	@Component(id = "list_agents_form")
	private Form form;

	@Persist
	private List<AgentProfile> agentsToDelete;

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

	private boolean deleteAll = false;

	void onSelectedFromDeleteAll() {
		this.deleteAll = true;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.organism = this.organismService.findOne(id);

		if (this.organism == null) {
			return Index.class;
		}

		this.agentsToDelete = this.getReportPage().getAgentsToDelete();

		if (this.agentsToDelete == null || this.agentsToDelete.isEmpty()) {
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
		if (!deleteAll) {
			if (this.selectedRows.isEmpty()) {
				this.form.recordError(this.getMessages().get("empty-selected-error-message"));
			}
		}
	}

	@OnEvent(value = EventConstants.SUCCESS, component = "list_agents_form")
	public Object onSuccess() {
		if (this.deleteAll) {
			return this.onDeleteAll();
		} else {
			try {
				// We look into the hashset to retrieve all agents to delete
				Iterator<AgentProfile> it = this.selectedRows.iterator();

				while (it.hasNext()) {
					AgentProfile agentToDelete = it.next();

					// agentToDelete.setOperationDelayed(Boolean.TRUE);
					this.agentProfileService.softDelete(agentToDelete);

					// Add user into synchronization queue
					this.synchronizationQueueService
							.create(new SynchronizationQueue(agentToDelete.getId(), ResourceType.AGENT_PROFILE,
									SynchronizationJobType.DELETE, MyEc3EsbConstants.getApplicationSendingJmsName()));

					// We remove the list of users deleted from to list of users
					// to
					// delete
					this.agentsToDelete.remove(agentToDelete);
				}

			} catch (Exception e) {
				logger.error("An error has occured during delete selected agents.", e);

				this.setErrorMessage(this.getMessages().get("delete-users-selected-error-message"));
				return this;
			}

			// In case of all agents are deleted we return to report page
			if (this.agentsToDelete.isEmpty()) {
				this.getReportPage().setOrganism(this.organism);
				this.getReportPage().setSuccessMessage(this.getMessages().get("delete-users-success-message"));
				this.clearPersistentParams();
				return this.getReportPage();
			}

			this.setSuccessMessage(this.getMessages().get("delete-users-selected-success-message"));

			return this;
		}
	}

	/**
	 * @return a list of agent profiles to delete (fill the grid displayed)
	 */
	public List<AgentProfile> getAgentProfileList() {
		// If filter used
		if (this.agentFilterResult != null) {
			return this.agentFilterResult;
		}

		// ELSE we use all the list
		if (this.agentsToDelete != null) {
			return this.agentsToDelete;
		}

		return new ArrayList<AgentProfile>();
	}

	/**
	 * @return : bean model
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
	 * Action performed when an user wants to delete all agents
	 */
	// @OnEvent(value = "action", component = "deleteAll")
	public Object onDeleteAll() {
		try {
			// Delete all users
			for (AgentProfile agent : this.agentsToDelete) {
				// agent.setOperationDelayed(Boolean.TRUE);
				this.agentProfileService.softDelete(agent);

				// Add user into synchronization queue
				this.synchronizationQueueService
						.create(new SynchronizationQueue(agent.getId(), ResourceType.AGENT_PROFILE,
								SynchronizationJobType.DELETE, MyEc3EsbConstants.getApplicationSendingJmsName()));
			}
		} catch (Exception ex) {
			logger.error("An error has occured during delete all agents.", ex);
			this.setErrorMessage(this.getMessages().get("delete-users-error-message"));
			return this;
		}

		// Clear all agents contained in list
		this.agentsToDelete.clear();

		// In case of success we return to the report page
		this.getReportPage().setOrganism(this.organism);
		this.getReportPage().setSuccessMessage(this.getMessages().get("delete-users-success-message"));
		this.clearPersistentParams();
		return this.getReportPage();
	}

	/* GETTER AND SETTER */
	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	public List<AgentProfile> getAgentsToDelete() {
		return agentsToDelete;
	}

	public void setAgentsToDelete(List<AgentProfile> agentsToDelete) {
		this.agentsToDelete = agentsToDelete;
	}
}
