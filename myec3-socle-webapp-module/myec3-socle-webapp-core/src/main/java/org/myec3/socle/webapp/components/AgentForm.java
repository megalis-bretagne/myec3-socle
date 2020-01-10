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
package org.myec3.socle.webapp.components;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.internal.SelectModelImpl;
import org.apache.tapestry5.internal.services.ComponentResultProcessorWrapper;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentEventResultProcessor;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Competence;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.CompetenceService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.organism.agent.Create;
import org.myec3.socle.webapp.pages.organism.agent.Modify;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Component class used to manage (add or modify) agent profile
 * {@link AgentProfile}<br>
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/components/AgentForm.tml
 * 
 * @author Anthony Colas <anthony.colas@atos.net>
 * @author Denis Cucchietti <denis.cucchietti@atos.net>
 */
public class AgentForm extends AbstractPage {

	private static final Log logger = LogFactory.getLog(AgentForm.class);

	@InjectPage
	private Modify modify;

	@InjectPage
	private Create create;

	@Property
	private Boolean CheckCertificate;

	// Services n pages
	@SuppressWarnings("rawtypes")
	@Environmental
	private ComponentEventResultProcessor componentEventResultProcessor;

	@Inject
	private ComponentResources componentResources;

	@Parameter(required = true)
	@Property
	private AgentProfile agentProfile;

	@Parameter(required = true, defaultPrefix = "prop")
	@Property
	private Object cancelRedirect;

	@Component(id = "modification_form")
	private Form form;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects
	 */
	@Inject
	@Service("profileService")
	private ProfileService profileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Inject
	@Service("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Inject
	@Service("competenceService")
	private CompetenceService competenceService;

	/**
	 * List with all compences availables
	 */
	private List<Competence> competenceList;

	public List<Competence> getCompetenceList() {
		if (competenceList == null) {
			competenceList = competenceService.findAll();
		}
		return competenceList;
	}

	public void setCompetenceList(List<Competence> selected) {
		competenceList = selected;
	}

	/**
	 * Create selectModel from competenceList
	 * @return
	 */
	public SelectModel getModelCompetence() {
		List<OptionModel> optionModels = new ArrayList<>();
		for (Competence c : getCompetenceList()) {
			optionModels.add(new OptionModelImpl(c.getName(), c));
		}
		return new SelectModelImpl(null, optionModels);
	}

	/**
	 * Convert Competences <-> String
	 */
	@Property
	private ValueEncoder<Competence> competenceValueEncoder = new ValueEncoder<Competence>() {
		@Override
		public String toClient(Competence competence) {
			if (competence == null) {
				return "";
			}
			return competence.getName();
		}

		@Override
		public Competence toValue(String s) {
			for (Competence c : getCompetenceList()) {
				if (c.getName().equals(s)) {
					return c;
				}
			}
			return null;
		}
	};

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link OrganismDepartment} objects
	 */
	@Inject
	@Service("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@Inject
	@Service("userService")
	private UserService userService;

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	@Persist
	private Boolean usernameDisabled;


	public Boolean getUsernameDisabled() {
		return this.usernameDisabled;
	}

	public void setUsernameDisabled(Boolean Bool) {
		this.usernameDisabled = Bool;
	}

	@Persist
	private String checkEmail;

	@Persist
	private String displayEmailInUsernameField;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Persist(PersistenceConstants.FLASH)
	private String certificateErrorMessage;

	@Persist(PersistenceConstants.FLASH)
	private String certificateSuccessMessage;

	public String getCheckEmail() {
		return this.checkEmail;
	}

	@Override
	public Object hasRights(Profile profile) {
		Profile loggedProfile = super.getLoggedProfile();
		if (loggedProfile != null && loggedProfile.equals(profile)) {
			return Boolean.TRUE;
		}
		return super.hasRights(profile);
	}

	public void setCheckEmail(String string) {
		this.checkEmail = string;
	}

	// Form events

	public String getDisplayEmailInUsernameField() {
		return displayEmailInUsernameField;
	}

	public void setDisplayEmailInUsernameField(
			String displayEmailInUsernameField) {
		this.displayEmailInUsernameField = displayEmailInUsernameField;
	}

	// Setup render is executed before the template is loading
	// it allows here to know if the username field is enabled or not
	@SetupRender
	private void setup() {
		if (this.getCheckEmail() == null) {
			this.setUsernameDisabled(Boolean.TRUE);
		} else {
			this.setCheckEmail(null);
		}
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
	public void onValidate() {

		// In case of creation of new agentProfile (email equals username)
		// if (this.getNewAgentProfile()) {
		// Check if username not already exists
		if (this.agentProfile.getEmail() != null
				&& this.profileService.usernameAlreadyExists(this.agentProfile.getEmail(), this.agentProfile)) {
			// if it exist, check if the mail is the same
			if (this.agentProfile.getUser().getUsername() == null
					|| this.agentProfile.getUser().getUsername().equals(this.agentProfile.getEmail())) {
				this.form.recordError(this.getMessages().get("recording-error-message-username"));
				this.setUsernameDisabled(Boolean.FALSE);
				this.setCheckEmail(this.agentProfile.getEmail());
				this.setDisplayEmailInUsernameField(this.agentProfile.getEmail());
			}

			// if username is set
			if (this.agentProfile.getUser().getUsername() != null) {
				// check if the new id already exist or not
				if (this.profileService.usernameAlreadyExists(this.agentProfile.getUser().getUsername(),
						this.agentProfile)) {
					this.form.recordError(this.getMessages().get("recording-error-message-usernameCheck"));
					this.setCheckEmail(this.agentProfile.getEmail());
				}
			}
		} else if (this.agentProfile.getUsername() != null
				&& this.profileService.usernameAlreadyExists(this.agentProfile.getUsername(), this.agentProfile)) {
			this.form.recordError(this.getMessages().get("recording-error-message-usernameCheck"));
			this.setCheckEmail(this.agentProfile.getEmail());
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public void onSuccess() {
		final ComponentResultProcessorWrapper callback = new ComponentResultProcessorWrapper(
				componentEventResultProcessor);
		this.componentResources.triggerEvent("participativeprocessformok", null, callback);
	}

	@OnEvent(EventConstants.CANCELED)
	public Object cancelRedirect() {
		return this.cancelRedirect;
	}

	// Getters
	public ValueEncoder<OrganismDepartment> getDepartmentEncoder() {
		return new GenericListEncoder<OrganismDepartment>(
				this.organismDepartmentService.findAllDepartmentByOrganism(this.agentProfile.getOrganismDepartment()
						.getOrganism()));
	}

	public Map<OrganismDepartment, String> getDepartments() {
		OrganismDepartment rootDepartments = this.organismDepartmentService
				.findRootOrganismDepartment(this.agentProfile.getOrganismDepartment().getOrganism());

		Map<OrganismDepartment, String> departments = new LinkedHashMap<OrganismDepartment, String>();
		departments = constructDepartementMap(departments, rootDepartments, 0);

		return departments;
	}

	/**
	 * Construct a map containing the list of departments for the corresponding
	 * structure
	 * 
	 * @param map    :the map to fill
	 * @param parent : the parent organism department of the current organism
	 * @param depth  : the level of organism department
	 * 
	 * @return a map containing the parent department and it's label
	 */
private Map<OrganismDepartment, String> constructDepartementMap(Map<OrganismDepartment, String> map,
			OrganismDepartment parent, int depth) {

		StringBuffer indent = new StringBuffer("");
		for (int i = 0; i < depth; i++) {
			indent.append("-- \\ ");
		}
		map.put(parent, indent.toString() + parent.getLabel());

		List<OrganismDepartment> departmentsList = this.organismDepartmentService.findAllChildrenDepartment(parent);
		depth++;
		for (OrganismDepartment organismDepartment : departmentsList) {
			map = constructDepartementMap(map, organismDepartment, depth);
		}

		return map;
	}

	/**
	 * @return true if is a creation of new agent profile
	 */

	public Boolean getNewAgentProfile() {
		if (null == this.agentProfile.getId())
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}

	/**
	 * @return true when the username field has to be displayed
	 */
	public Boolean getUsernameDisplay() {
		if (this.getUsernameDisabled() == Boolean.FALSE) {
			javaScriptSupport.addScript("$('username').activate();");
			if (this.agentProfile.getUser().getUsername() == null) {
				this.agentProfile.getUser().setUsername(this.getDisplayEmailInUsernameField());
			}
			return Boolean.TRUE;
		} else {
			javaScriptSupport.addScript("$('firstname').activate();");
			return Boolean.FALSE;
		}

	}

	/**
	 * @return true if the checkbox "representative" must be disabled
	 */
	public Boolean getIsRepresentativeDisabled() {
		List<AgentProfile> listOfRepresentativesOfOrganism = this.agentProfileService
				.findAllRepresentativesByOrganism(this.agentProfile
						.getOrganismDepartment().getOrganism());

		if (listOfRepresentativesOfOrganism != null) {
			if (listOfRepresentativesOfOrganism.isEmpty()
					|| (listOfRepresentativesOfOrganism.contains(this.agentProfile))) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * @return true if the checkbox "substitute" must be disabled
	 */
	public Boolean getIsSubstituteDisabled() {
		List<AgentProfile> listOfSubstitutesOfOrganism = this.agentProfileService
				.findAllSubstitutesByOrganism(this.agentProfile.getOrganismDepartment()
						.getOrganism());

		if (listOfSubstitutesOfOrganism != null) {
			if (listOfSubstitutesOfOrganism.isEmpty() || (listOfSubstitutesOfOrganism.contains(this.agentProfile))) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	void onSelectedFromCheckCertificate() {

		// Tells the Modify page that we have to check certificate
		this.modify.setCertificateValid(Boolean.TRUE);

		// Tells the Create page that we have to check certificate
		this.create.setCertificateValid(Boolean.TRUE);
	}

	public String getCertificateErrorMessage() {
		return this.certificateErrorMessage;
	}

	public void setCertificateErrorMessage(String errorMessage) {
		this.certificateErrorMessage = errorMessage;
	}

	public String getCertificateSuccessMessage() {
		return this.certificateSuccessMessage;
	}

	public void setCertificateSuccessMessage(String successMessage) {
		this.certificateSuccessMessage = successMessage;
	}

}
