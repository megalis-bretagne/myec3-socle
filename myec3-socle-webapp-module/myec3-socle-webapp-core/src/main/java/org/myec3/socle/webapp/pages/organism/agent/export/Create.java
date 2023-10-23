package org.myec3.socle.webapp.pages.organism.agent.export;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.constants.MyEc3EmailConstants;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.service.*;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.encoder.MultipleValueEncoder;
import org.myec3.socle.webapp.entities.MessageEmail;
import org.myec3.socle.webapp.pages.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.mail.MessagingException;
import java.util.*;

/**
 * Page used to create the {@link AgentProfile} selected from the list of agents
 * to create.<br />
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/organism/agent/export/Create.tml
 *
 *
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class Create extends AbstractImport {

	private static final Logger logger = LoggerFactory.getLogger(Create.class);

	@Component(id = "agent_create_form")
	private Form form;

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	@Inject
	@Named("agentProfileService")
	private AgentProfileService agentProfileService;

	@Inject
	@Named("organismService")
	private OrganismService organismService;

	@Inject
	@Named("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@Inject
	@Named("roleService")
	private RoleService roleService;

	@Inject
	@Named("profileTypeService")
	private ProfileTypeService profileTypeService;

	@Inject
	@Named("profileService")
	private ProfileService profileService;

	@Inject
	@Named("userService")
	private UserService userService;

	@Inject
	@Service("emailService")
	private EmailService emailService;

	@Persist
	private Organism organism;

	@Persist
	private AgentProfile agentToCreate;

	@InjectPage
	private CreateList createListPage;

	@Property
	private Boolean sendMailEnabled = Boolean.TRUE;

	/**
	 * List of selected roles
	 */
	private List<Role> selectedRoles;

	@Property
	private List<Role> multipleRoleSelected;

	@Property
	private List<Application> availableApplications;

	@Property
	private Application applicationLoop;

	@SuppressWarnings("unused")
	@Property
	private GenericListEncoder<Application> applicationEncoder;

	private List<Application> defaultApplications;

	private List<Application> applicationsAllowingMultipleRoles;

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

		if (this.agentToCreate == null) {
			return Report.class;
		}

		if (this.selectedRoles == null) {
			this.selectedRoles = new ArrayList<>();
		}

		if (this.multipleRoleSelected == null) {
			this.multipleRoleSelected = new ArrayList<>();
		}

		if (this.agentToCreate.getProfileType() == null) {
			this.agentToCreate.setProfileType(this.profileTypeService.findByValue(ProfileTypeValue.AGENT));
		}

		this.availableApplications = this.constructListOfApplications();

		this.applicationsAllowingMultipleRoles = this.applicationService
				.findAllApplicationsAllowingMultipleRolesByStructureTypeValue(StructureTypeValue.ORGANISM);

		this.applicationEncoder = new GenericListEncoder<>(this.availableApplications);

		// Check if loggedUser can access at this page
		return this.hasRights(this.organism);
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "agent_create_form")
	public void onValidate() {
		// Check that the email not already exists (only for agent profile)
		if (this.agentProfileService.emailAlreadyExists(this.agentToCreate.getEmail(), this.agentToCreate)) {
			this.form.recordError(this.getMessages().get("recording-duplicate-error-message"));
		}

		// Check that username not already exists
		if (this.profileService.usernameAlreadyExists(this.agentToCreate.getEmail(), this.agentToCreate)) {
			this.form.recordError(this.getMessages().get("recording-error-message-username"));
		}
	}

	// Form events
	@OnEvent(value = EventConstants.SUCCESS, component = "agent_create_form")
	public Object onSuccess() {
		try {
			// set roles selected to the agent
			this.agentToCreate.setRoles(this.retrieveRolesOfAgent());

			this.agentToCreate.setName(
					this.agentToCreate.getUser().getLastname() + " " + this.agentToCreate.getUser().getFirstname());
			this.agentToCreate.setLabel(this.agentToCreate.getName());

			// AGENT PROFILE USER
			this.agentToCreate.getUser().setName(
					this.agentToCreate.getUser().getLastname() + " " + this.agentToCreate.getUser().getFirstname());
			this.agentToCreate.getUser().setLabel(this.agentToCreate.getUser().getName());
			this.agentToCreate.getUser().setUsername(this.agentToCreate.getEmail());

			// SET AGENT ADDRESS
			this.setAddressOfAgentToCreate();

			// password for mail
			String password = this.userService.generatePassword();
			this.agentToCreate.getUser().setTemporaryPassword(password);
			this.userService.create(this.agentToCreate.getUser());
			User user = this.userService.findByName(this.agentToCreate.getUser().getName());
			this.agentToCreate.setUser(user);

			// Create agentProfile
			this.agentProfileService.create(this.agentToCreate);

			this.synchronizationService.notifyCreation(this.agentToCreate);

			if ((this.sendMailEnabled) && (this.emailService.authorizedToSendMail(this.organism))) {
				this.sendMail(password);
			}

			// Remove user from the list of agents to crete
			this.getReportPage().getAgentsToCreate().remove(this.agentToCreate);

			return this.nextPage();
		} catch (Exception e) {
			logger.error("An error has occured during create the agent " + agentToCreate.getUser().getFirstname() + " "
					+ agentToCreate.getUser().getLastname() + " from imported file", e);
			this.setErrorMessage(this.getMessages().get("recording-error-message"));
			return null;
		}
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
	}

	/**
	 * This methods allows to send login and password for the new user
	 *
	 * @param password : the password generated for the new user
	 * @throws MessagingException
	 */
	public void sendMail(String password) {
		// message mail
		StringBuffer message = new StringBuffer();
		message.append(this.getMessages().get("login-message"));
		message.append(this.agentToCreate.getEmail());
		message.append("\n");
		message.append(this.getMessages().get("password-message"));
		message.append(password);
		message.append("\n\n");
		message.append(this.getMessages().get("courtesy-message"));

		MessageEmail messageEmail = new MessageEmail(ProfileTypeValue.AGENT,
				MessageEmail.EmailContext.AGENT_ORGANISME_CREATE, this.agentToCreate.getEmail(), password,
				this.agentToCreate.getOrganismDepartment().getOrganism().getLabel());

		String[] recipients = new String[1];
		recipients[0] = this.agentToCreate.getEmail();
		this.emailService.silentSendMail(MyEc3EmailConstants.getSender(), MyEc3EmailConstants.getFrom(), recipients,
				this.getMessages().get("subject-message"), messageEmail.generateContent(this.getMessages(),
						this.agentToCreate.getOrganismDepartment().getOrganism().getCustomer()));
	}

	/**
	 * @return the correct page depending on the list of agents to create
	 */
	public Object nextPage() {
		// if there are several agents to create we return the user to the list
		// of users to create
		List<AgentProfile> listOfAgentsToCreate = this.getReportPage().getAgentsToCreate();

		if ((listOfAgentsToCreate == null) || (listOfAgentsToCreate.isEmpty())) {
			this.getReportPage().setOrganism(this.organism);
			this.getReportPage().setSuccessMessage(this.getMessages().get("create-agent-success-message"));
			return this.getReportPage();
		}

		// Set succes message to previous page
		this.createListPage.setSuccessMessage(this.getMessages().get("create-agent-success-message"));
		// Redirect user to previous page
		return this.createListPage;
	}

	/**
	 * @return the list of applications subscribables by the user
	 */
	public List<Application> constructListOfApplications() {
		// Retrieve all applications of the organism
		List<Application> allApplications = this.applicationService.findAllApplicationByStructure(this.organism);

		// Remove default applications
		this.defaultApplications = this.applicationService
				.findAllDefaultApplicationsByStructureType(this.organism.getStructureType());

		// Remove default applications from all applications
		allApplications.removeAll(this.defaultApplications);
		// Add GU because that's a specific application.
		Application gu = this.applicationService.findByName(MyEc3ApplicationConstants.GU);
		allApplications.add(gu);

		return allApplications;
	}

	/**
	 * @return the list of roles selected by the user
	 */
	public List<Role> retrieveRolesOfAgent() {
		List<Role> listRoles = new ArrayList<>();

		// Retrieve roles of default applications
		for (Application application : this.defaultApplications) {
			if (!application.getName().equalsIgnoreCase(MyEc3ApplicationConstants.GU)) {
				Role role = this.roleService
						.findBasicRoleByProfileTypeAndApplication(this.agentToCreate.getProfileType(), application);
				if ((role != null) && (!listRoles.contains(role))) {
					listRoles.add(role);
				}
			}
		}

		// Retrieve selected roles
		for (Role role : this.selectedRoles) {
			if ((role != null) && (!listRoles.contains(role))) {
				listRoles.add(role);
			}
		}

		// Retrieve multiple roles selected
		for (Role role : this.multipleRoleSelected) {
			if ((role != null) && (!listRoles.contains(role))) {
				listRoles.add(role);
			}
		}

        listRoles.removeIf(r -> r.getId() == null);

		return listRoles;
	}

	/**
	 * This method allows to fill correctly agent's address. 2 cases : - if address
	 * of agent is empty we use the address of department selected - if the address
	 * of the department selected is also empty we use organism's address. We are
	 * sure that the organism address is not empty because we check values in the
	 * import page (import.java)
	 *
	 */
	public void setAddressOfAgentToCreate() {

		// Agent address
		String agentPostalAddress = this.agentToCreate.getAddress().getPostalAddress();
		String agentPostalCode = this.agentToCreate.getAddress().getPostalCode();
		String agentCity = this.agentToCreate.getAddress().getCity();

		// Department selected address
		String agentDepartmentPostalAddress = null;
		String agentDepartmentPostalCode = null;
		String agentDepartmentCity = null;

		if (this.agentToCreate.getOrganismDepartment().getAddress() != null) {
			agentDepartmentPostalAddress = this.agentToCreate.getOrganismDepartment().getAddress().getPostalAddress();
			agentDepartmentPostalCode = this.agentToCreate.getOrganismDepartment().getAddress().getPostalCode();
			agentDepartmentCity = this.agentToCreate.getOrganismDepartment().getAddress().getCity();
		}

		// SET ADRESSE
		if ((agentPostalAddress == null) || (agentPostalAddress.isEmpty())) {
			// In this case we retrieve the department postal address
			if ((agentDepartmentPostalAddress != null) && !(agentDepartmentPostalAddress.isEmpty())) {
				this.agentToCreate.getAddress().setPostalAddress(agentDepartmentPostalAddress);
			} else {
				// We set the organism address
				this.agentToCreate.getAddress().setPostalAddress(this.organism.getAddress().getPostalAddress());
			}
		}

		// SET POSTAL CODE
		if ((agentPostalCode == null) || (agentPostalCode.isEmpty())) {
			// In this case we retrieve the department postal code
			if ((agentDepartmentPostalCode != null) && !(agentDepartmentPostalCode.isEmpty())) {
				this.agentToCreate.getAddress().setPostalCode(agentDepartmentPostalCode);
			} else {
				// We set the organism postal code
				this.agentToCreate.getAddress().setPostalCode(this.organism.getAddress().getPostalCode());
			}
		}

		// SET CITY
		if ((agentCity == null) || (agentCity.isEmpty())) {
			// In this case we retrieve the department city
			if ((agentDepartmentCity != null) && !(agentDepartmentCity.isEmpty())) {
				this.agentToCreate.getAddress().setCity(agentDepartmentCity);
			} else {
				// We set the organism city
				this.agentToCreate.getAddress().setCity(this.organism.getAddress().getCity());
			}
		}

		// SET COUNTRY
		this.agentToCreate.getAddress().setCountry(Country.FR);
	}

	/* GETTER AND SETTER */

	/* ORGANISM DEPARTMENT */
	public ValueEncoder<OrganismDepartment> getDepartmentEncoder() {
		return new GenericListEncoder<>(this.organismDepartmentService
				.findAllDepartmentByOrganism(this.agentToCreate.getOrganismDepartment().getOrganism()));
	}

	public Map<OrganismDepartment, String> getDepartments() {
		OrganismDepartment rootDepartment = this.organismDepartmentService
				.findRootOrganismDepartment(this.agentToCreate.getOrganismDepartment().getOrganism());

		Map<OrganismDepartment, String> departments = new LinkedHashMap<>();
		departments = constructDepartementMap(departments, rootDepartment, 0);

		return departments;
	}

	private Map<OrganismDepartment, String> constructDepartementMap(Map<OrganismDepartment, String> map,
			OrganismDepartment parent, int depth) {
		StringBuffer indent = new StringBuffer("");
		for (int i = 0; i < depth; i++) {
			indent.append("-- \\ ");
		}
		map.put(parent, indent.toString() + parent.getLabel());

		List<OrganismDepartment> departmentsList = this.organismDepartmentService.findAllChildrenDepartment(parent);

		// Sort list by department label see method compareTo(OrganismDepartment
		// o) into OrganismDepartment.java
		Collections.sort(departmentsList);

		depth++;
		for (OrganismDepartment organismDepartment : departmentsList) {
			map = constructDepartementMap(map, organismDepartment, depth);
		}

		return map;
	}

	/* ROLES */
	public MultipleValueEncoder<Role> getMultipleRolesEncoder() {
		return new MultipleValueEncoder<Role>() {

			public String toClient(Role object) {
				return object.getId().toString();
			}

			public List<Role> toValue(String[] values) {
				List<Role> availableRoles = roleService
						.findAllRoleByProfileTypeAndApplication(agentToCreate.getProfileType(), applicationLoop);
				List<Role> objects = new ArrayList<>();
				for (String value : values) {
					for (Role myObject : availableRoles)
						if (myObject.getId().equals(Long.valueOf(value))) {
							objects.add(myObject);
						}
				}
				return objects;
			}

		};
	}

	public GenericListEncoder<Role> getRoleEncoder() {
		List<Role> availableRoles = this.roleService
				.findAllRoleByProfileTypeAndApplication(this.agentToCreate.getProfileType(), this.applicationLoop);
		availableRoles.add(new Role(null,null));
		return new GenericListEncoder<>(availableRoles);
	}

	public Map<Role, String> getRolesModel() {
		List<Role> availableRoles = this.roleService
				.findAllRoleByProfileTypeAndApplication(this.agentToCreate.getProfileType(), this.applicationLoop);

		Map<Role, String> roles = new LinkedHashMap<>();

		if (!this.defaultApplications.contains(this.applicationLoop)
				&& (!this.applicationsAllowingMultipleRoles.contains(this.applicationLoop))) {
			roles.put(new Role(null,null), this.getMessages().get("select-role-application"));
		}

		for (Role role : availableRoles) {
			roles.put(role, role.getLabel());
		}

		return roles;
	}

	public Role getRoleSelected() {
		for (Role role : this.agentToCreate.getRoles()) {
			if (role.getApplication().equals(this.applicationLoop)) {
				return role;
			}
		}
		return null;
	}

	public void setRoleSelected(Role roleSelected) {
		if ((roleSelected != null) && (!this.selectedRoles.contains(roleSelected))) {
			this.selectedRoles.add(roleSelected);
		}
	}

	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	public AgentProfile getAgentToCreate() {
		return agentToCreate;
	}

	public void setAgentToCreate(AgentProfile agentToCreate) {
		this.agentToCreate = agentToCreate;
	}

	public String getCivilityOfAgent() {
		if (this.agentToCreate.getUser().getCivility() != null) {
			return this.agentToCreate.getUser().getCivility().toString();
		}
		return this.getMessages().get("unspecified-feminine-label");
	}

	/**
	 * @return TRUE if current application allows user to select several roles
	 */
	public Boolean getIsApplicationAllowingMultipleRoles() {
		if (this.applicationsAllowingMultipleRoles != null) {
			if (this.applicationsAllowingMultipleRoles.contains(this.applicationLoop)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
}
