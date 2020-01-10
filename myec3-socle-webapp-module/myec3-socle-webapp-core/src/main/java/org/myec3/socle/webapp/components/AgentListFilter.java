package org.myec3.socle.webapp.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.services.ComponentResultProcessorWrapper;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentEventResultProcessor;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.webapp.pages.AbstractPage;

public class AgentListFilter extends AbstractPage {

	// Services n pages
	@SuppressWarnings("rawtypes")
	@Environmental
	private ComponentEventResultProcessor componentEventResultProcessor;

	@Inject
	private ComponentResources componentResources;

	@Parameter(required = true)
	@Property
	private List<AgentProfile> agentProfileList;

	@SuppressWarnings("unused")
	@Component(id = "filter_form")
	private Form form;

	private String searchFirstName;

	private String searchLastname;

	private String searchEmail;

	@Persist(PersistenceConstants.FLASH)
	private List<AgentProfile> agentsMatching;

	// Template properties
	@OnEvent(value = EventConstants.VALIDATE, component = "filter_form")
	public void onValidate() {
		this.agentsMatching = new ArrayList<AgentProfile>();

		if ((null == searchFirstName) && (null == searchLastname)
				&& (null == searchEmail)) {
			this.agentsMatching = this.agentProfileList;
		} else {
			for (AgentProfile agent : this.agentProfileList) {
				String agentFirstname = agent.getUser().getFirstname()
						.toLowerCase();
				String agentLastName = agent.getUser().getLastname()
						.toLowerCase();
				String agentEmail = agent.getEmail().toLowerCase();

				if ((null != this.searchFirstName)
						&& (null != this.searchLastname)
						&& (null != this.searchEmail)) {
					if ((agentFirstname.contains(this.searchFirstName))
							&& (agentLastName.contains(this.searchLastname))
							&& (agentEmail.contains(this.searchEmail))) {
						this.agentsMatching.add(agent);
					}
				}

				if ((null != this.searchFirstName)
						&& (null != this.searchLastname)
						&& (null == this.searchEmail)) {
					if ((agentFirstname.contains(this.searchFirstName))
							&& (agentLastName.contains(this.searchLastname))) {
						this.agentsMatching.add(agent);
					}
				}

				if ((null != this.searchFirstName)
						&& (null == this.searchLastname)
						&& (null == this.searchEmail)) {
					if (agentFirstname.contains(this.searchFirstName)) {
						this.agentsMatching.add(agent);
					}
				}

				if ((null == this.searchFirstName)
						&& (null != this.searchLastname)
						&& (null == this.searchEmail)) {
					if (agentLastName.contains(this.searchLastname)) {
						this.agentsMatching.add(agent);
					}
				}

				if ((null == this.searchFirstName)
						&& (null != this.searchLastname)
						&& (null != this.searchEmail)) {
					if ((agentLastName.contains(this.searchLastname))
							&& (agentEmail.contains(this.searchEmail))) {
						this.agentsMatching.add(agent);
					}
				}

				if ((null == this.searchFirstName)
						&& (null == this.searchLastname)
						&& (null != this.searchEmail)) {
					if (agentEmail.contains(searchEmail)) {
						this.agentsMatching.add(agent);
					}
				}

			}
		}
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public void onSuccess() {
		final ComponentResultProcessorWrapper callback = new ComponentResultProcessorWrapper(
				componentEventResultProcessor);
		this.componentResources.triggerEvent("participativeprocessformok",
				null, callback);
	}

	public List<AgentProfile> getAgentsMatching() {
		return this.agentsMatching;
	}

	public void setAgentsMatching(ArrayList<AgentProfile> agentsMatching) {
		this.agentsMatching = agentsMatching;
	}

	public String getSearchFirstName() {
		return searchFirstName;
	}

	public void setSearchFirstName(String searchFirstName) {
		if (searchFirstName != null) {
			this.searchFirstName = searchFirstName.toLowerCase();
		} else {
			this.searchFirstName = null;
		}

	}

	public String getSearchLastname() {
		return searchLastname;
	}

	public void setSearchLastname(String searchLastname) {
		if (searchLastname != null) {
			this.searchLastname = searchLastname.toLowerCase();
		} else {
			this.searchLastname = null;
		}
	}

	public String getSearchEmail() {
		return searchEmail;
	}

	public void setSearchEmail(String searchEmail) {
		if (searchEmail != null) {
			this.searchEmail = searchEmail.toLowerCase();
		} else {
			this.searchEmail = null;
		}
	}
}
