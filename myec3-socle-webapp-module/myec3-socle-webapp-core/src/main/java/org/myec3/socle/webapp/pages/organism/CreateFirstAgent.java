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
import java.util.List;

import javax.inject.Named;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.constants.MyEc3EmailConstants;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.domain.model.enums.PrefComMedia;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.EmailService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.ProfileTypeService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.entities.MessageEmail;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used during organism{@link Organism} creation process.<br />
 * 
 * It's the third and last step to create an organism. In this step your must
 * fill<br />
 * organism's administrator attributes.<br />
 *
 * 
 *      Corresponding tapestry template file is :
 *      src/main/resources/org/myec3/socle
 *      /webapp/pages/organism/CreateFirstAgent.tml
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
public class CreateFirstAgent extends AbstractPage {

	private static final Log logger = LogFactory.getLog(CreateFirstAgent.class);

	@Property
	private String errorMessage;

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
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

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
	private AgentProfileService agentprofileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Inject
	@Named("roleService")
	private RoleService roleService;

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Inject
	@Named("userService")
	private UserService userService;

	/**
	 * Business Service providing methods and specifics operations on Email objects
	 */
	@Inject
	@Named("emailService")
	private EmailService emailService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects
	 */
	@Inject
	@Named("profileService")
	private ProfileService profileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link ProfileType} objects
	 */
	@Inject
	@Named("profileTypeService")
	private ProfileTypeService profileTypeService;

	@InjectPage
	private View viewPage;

	@Property
	private User user;

	private Organism organism;

	@Property
	private AgentProfile agentProfile;

	@Property
	private OrganismDepartment organismDepartment;

	@Property
	private String email;

	@Component(id = "modification_form")
	private Form form;

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return viewPage;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		user = new User();
		this.organism = this.organismService.findOne(id);
		if (null == this.organism) {
			return false;
		}

		// Check if loggedUser can access to this organism
		return this.hasRights(this.organism);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Object onPassivate() {
		List<Long> result = new ArrayList<Long>();
		if (this.organism != null) {
			result.add(this.organism.getId());
		}
		if (this.agentProfile != null) {
			result.add(this.agentProfile.getId());
		}
		return result;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "email")
	public void checkEmail(String email) {
		if (this.agentprofileService.emailAlreadyExists(email, new AgentProfile())) {
			this.form.recordError(this.getMessages().get("recording-duplicate-error-message"));
			logger.info(this.getMessages().get("recording-duplicate-error-message"));
		}

		if (this.profileService.usernameAlreadyExists(email, new AgentProfile())) {
			this.form.recordError(this.getMessages().get("recording-username-duplicate-error-message"));
			logger.info(this.getMessages().get("recording-username-duplicate-error-message"));
		}
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {
			user.setLabel(this.user.getFirstname() + " " + this.user.getLastname());
			user.setName(this.user.getLabel());
			user.setUsername(this.email.toLowerCase());

			// password for mail
			String password = this.userService.generatePassword();
			user.setPassword(this.userService.generateHashPassword(password));
			user.setExpirationDatePassword(this.userService.generateExpirationDatePassword());
			this.userService.create(user);

			this.organismDepartment = this.organismDepartmentService.findRootOrganismDepartment(this.organism);

			AgentProfile userAgentProfile = new AgentProfile();
			userAgentProfile.setName(this.user.getFirstname() + " " + this.user.getLastname());
			userAgentProfile.setLabel(userAgentProfile.getName());
			userAgentProfile.setAddress(this.organismDepartment.getAddress());
			userAgentProfile.setEmail(this.email);
			userAgentProfile.setEnabled(Boolean.TRUE);
			userAgentProfile.setElected(Boolean.FALSE);
			userAgentProfile.setOrganismDepartment(this.organismDepartment);
			userAgentProfile.setUser(this.user);
			userAgentProfile.setPrefComMedia(PrefComMedia.EMAIL);

			// ProfileType
			ProfileType profileType = this.profileTypeService.findByValue(ProfileTypeValue.AGENT);
			userAgentProfile.setProfileType(profileType);

			List<Role> listRole = new ArrayList<Role>();
			// find all application of organism
			List<Application> applications = new ArrayList<Application>();
			applications = this.applicationService.findAllApplicationByStructure(this.organism);

			// add admin role for each application
			for (Application application : applications) {
				Role role2 = this.roleService
						.findAdminRoleByProfileTypeAndApplication(userAgentProfile.getProfileType(), application);
				if (role2 != null) {
					listRole.add(role2);
				}
			}
			userAgentProfile.setRoles(listRole);

			// Create first admin agent profile
			this.agentprofileService.create(userAgentProfile);
			this.agentProfile = userAgentProfile;

			// Synchronize creation to external applications
			this.notifyAllApplications();

			if (this.emailService.authorizedToSendMail(this.organism)) {
				// send the mail containing the login and password at the new
				// user
				this.sendMail(password);
			}

		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("recording-error-message");
			logger.error("error during successForm CreateFirstAgent" + e);
			return null;
		}

		if (this.emailService.authorizedToSendMail(this.organism)) {
			this.viewPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
		} else {
			this.viewPage.setSuccessMessage(this.getMessages().get("recording-success-message-no-mail"));
		}
		this.viewPage.setOrganism(this.organism);
		this.viewPage.setAgentProfile(this.agentProfile);

		return this.viewPage;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		return Index.class;
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
		message.append(email);
		message.append("\n");
		message.append(this.getMessages().get("password-message"));
		message.append(password);
		message.append("\n\n");
		message.append(this.getMessages().get("courtesy-message"));

		MessageEmail messageEmail = new MessageEmail(ProfileTypeValue.ADMIN,
				MessageEmail.EmailContext.ADMIN_ORGANISME_CREATE, email, password, this.organism.getLabel());
		String[] recipients = new String[1];
		recipients[0] = email;

		this.emailService.silentSendMail(MyEc3EmailConstants.getSender(), MyEc3EmailConstants.getFrom(), recipients,
				this.getMessages().get("subject-message"),
				messageEmail.generateContent(this.getMessages(), this.organism.getCustomer()));
	}

	public void notifyAllApplications() {

//		// Synchronize the new Organism
//		this.synchronizationService.notifyCreation(this.agentProfile
//				.getOrganismDepartment().getOrganism());
//
//		// Synchronize the organismDepartment
//		this.synchronizationService.notifyCreation(this.agentProfile
//				.getOrganismDepartment());

		// Synchronize the new agent Profile
		this.synchronizationService.notifyCreation(this.agentProfile);
	}

	// Getters n Setters
	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

}
