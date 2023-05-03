package org.myec3.socle.webapp.pages.organism.agent.export;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.beanmodel.PropertyConduit;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.beanmodel.services.PropertyConduitSource;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.webapp.components.AgentListFilter;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to display the list of {@link AgentProfile} to create.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/organism/agent/export/CreateList.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class CreateList extends AbstractImport {

	@Component(id = "filter_form")
	private AgentListFilter agentListFilter;

	@Inject
	@Named("organismService")
	private OrganismService organismService;

	@Inject
	@Named("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@SuppressWarnings("unused")
	@Component(id = "list_agents_form")
	private Form form;

	@Persist
	private List<AgentProfile> agentsToCreate;

	@Persist
	private Organism organism;

	@Property
	private OrganismDepartment organismDepartment;

	/* GRID ATTRIBUTES */
	@SuppressWarnings("unused")
	@Property
	private Integer rowIndex;

	@SuppressWarnings("unused")
	@Property
	private AgentProfile currentRow;

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private PropertyConduitSource propertyConduitSource;

	// FILTER
	@Persist(PersistenceConstants.FLASH)
	private List<AgentProfile> agentFilterResult;

	// NEXT PAGE
	@InjectPage
	private Create createAgentPage;

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

		this.organismDepartment = this.organismDepartmentService.findRootOrganismDepartment(this.organism);

		this.agentsToCreate = this.getReportPage().getAgentsToCreate();

		if ((this.organismDepartment == null) || (this.agentsToCreate == null)) {
			return Report.class;
		}

		// Clear params of next page
		this.createAgentPage.clearPersistentParams();

		// Check if loggedUser can access at this page
		return this.hasRights(this.organism);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
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
		if (this.agentsToCreate != null) {
			return this.agentsToCreate;
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
		model.add("action", null);
		model.include("firstname", "lastname", "email", "action");
		return model;
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
	 * Method use to redirect user to create agent page
	 */
	@OnEvent(value = "action", component = "create")
	public Object createAgents(int rowIndex) {
		AgentProfile agentToCreate = this.agentsToCreate.get(rowIndex);

		if (agentToCreate != null) {
			agentToCreate.setOrganismDepartment(this.organismDepartment);
			this.createAgentPage.setOrganism(this.organism);
			this.createAgentPage.setAgentToCreate(agentToCreate);
			return this.createAgentPage;
		}

		return this;
	}

	/* GETTER AND SETTER */
	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	public List<AgentProfile> getAgentsToCreate() {
		return agentsToCreate;
	}

	public void setAgentsToCreate(List<AgentProfile> agentsToCreate) {
		this.agentsToCreate = agentsToCreate;
	}
}
