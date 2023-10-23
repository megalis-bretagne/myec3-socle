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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.constants.MyEc3EmailConstants;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.service.*;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.components.AgentForm;
import org.myec3.socle.webapp.entities.MessageEmail;
import org.myec3.socle.webapp.pages.AbstractPage;

import javax.inject.Named;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Page used to create new agent{@link AgentProfile}.<br />
 *
 * In this step your must fill agent attributes.<br />
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/agent/Create.tml
 *
 * @see "WebSecurityConfig" to know profiles authorized to display this
 *      page<br />
 *
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
public class Create extends AbstractPage {

	private static final Log logger = LogFactory.getLog(Create.class);

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
	 * Business Service providing methods and specifics operations on Email objects
	 */
	@Inject
	@Named("emailService")
	private EmailService emailService;

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Inject
	@Named("userService")
	private UserService userService;

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Inject
	@Named("profileService")
	private ProfileService profileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link OrganismDepartment} objects
	 */
	@Inject
	@Named("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@InjectPage
	@Property
	private ListAgents listAgentsPage;

	@InjectPage
	private ModifyRoles modifyRolesPage;

	@Component(id = "agent_form")
	private AgentForm agentForm;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Property
	private AgentProfile agentProfile;

	@Persist
	private AgentProfile oldAgentProfile;

	private OrganismDepartment organismDepartment;

	@Persist(PersistenceConstants.FLASH)
	private String certificateErrorMessage;

	@Persist(PersistenceConstants.FLASH)
	private String certificateSuccessMessage;

	@Persist(PersistenceConstants.FLASH)
	private Boolean certificateValid;

	@Persist(PersistenceConstants.FLASH)
	private Boolean activateOnce;

	// Page Activation n Passivation
	@OnEvent(EventConstants.ACTIVATE)
	public void activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id, Long idDepartment) {

		if (this.agentProfile == null && this.oldAgentProfile != null) {
			this.agentProfile = this.oldAgentProfile;
			if (null == this.agentProfile.getUser()) {
				this.agentProfile.setUser(new User());
			}

			this.organismDepartment = this.organismDepartmentService.findOne(idDepartment);

			if (null == this.organismDepartment) {
				return Boolean.FALSE;
			}

			this.agentProfile.setOrganismDepartment(this.organismDepartment);

			if ((null == this.agentProfile.getOrganismDepartment().getOrganism())
					|| (0 != id.compareTo(this.agentProfile.getOrganismDepartment().getOrganism().getId()))) {
				return Boolean.FALSE;
			}

			this.fillAgentprofileAddressFields();

			if (null == this.agentProfile.getAddress()) {
				this.agentProfile.setAddress(new Address());
			}
		}

		if (this.activateOnce == null || this.activateOnce.equals(Boolean.FALSE)) {

			this.agentProfile = new AgentProfile();
			if (null == this.agentProfile.getUser()) {
				this.agentProfile.setUser(new User());
			}

			this.organismDepartment = this.organismDepartmentService.findOne(idDepartment);

			if (null == this.organismDepartment) {
				return Boolean.FALSE;
			}

			this.agentProfile.setCreatedUserId(this.getUserIdLogged());
			this.agentProfile.setOrganismDepartment(this.organismDepartment);

			if ((null == this.agentProfile.getOrganismDepartment().getOrganism())
					|| (0 != id.compareTo(this.agentProfile.getOrganismDepartment().getOrganism().getId()))) {
				return Boolean.FALSE;
			}

			this.fillAgentprofileAddressFields();

			if (null == this.agentProfile.getAddress()) {
				this.agentProfile.setAddress(new Address());
			}
		}

		// Check if loggedUser can access at this page
		return this.hasRights(this.agentProfile);
	}

	public Object onPassivate() {
		List<Long> result = new ArrayList<>();
		if (this.organismDepartment != null) {
			result.add(this.organismDepartment.getOrganism().getId());
			result.add(this.organismDepartment.getId());
		}
		return result;
	}


	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		logger.debug("CertificateValid Boolean ? " + this.certificateValid);
		if (this.certificateValid != null && this.certificateValid.equals(Boolean.TRUE)) {
			// User clicked on check certificate
			logger.debug("Submit from certificate validation");

			// certificate cannot be null
			if (this.agentProfile.getUser().getCertificate() == null) {
				logger.debug("User certificate is null !");
				this.certificateErrorMessage = this.getMessages().get("no-certificate");
				// set this boolean to true so we don't initiate AgentProfile again
				// and loose informations
				this.activateOnce = Boolean.TRUE;
				return null;
			}

			// set this boolean to true so we don't initiate AgentProfile again
			// and loose informations
			this.activateOnce = Boolean.TRUE;
			this.oldAgentProfile = this.agentProfile;
			// reset so we don't loop in this function
			this.certificateValid = Boolean.FALSE;
			return this;
		} else {
			try {
				this.agentProfile.setName(
						this.agentProfile.getUser().getLastname() + " " + this.agentProfile.getUser().getFirstname());
				this.agentProfile.setLabel(this.agentProfile.getName());

				// AGENT PROFILE USER
				this.agentProfile.getUser().setName(
						this.agentProfile.getUser().getLastname() + " " + this.agentProfile.getUser().getFirstname());
				this.agentProfile.getUser().setLabel(this.agentProfile.getUser().getName());

				if (this.agentProfile.getUser().getUsername() != null) {
					this.agentProfile.getUser().setUsername(this.agentProfile.getUser().getUsername());
				} else {
					this.agentProfile.getUser().setUsername(this.agentProfile.getEmail());
				}

				// password for mail
				String password = this.userService.generatePassword();
				this.agentProfile.getUser().setTemporaryPassword(password);
				this.userService.create(this.agentProfile.getUser());
				User user = this.userService.findByName(this.agentProfile.getUser().getName());
				this.agentProfile.setUser(user);

				// Create agentProfile
				this.agentProfileService.create(this.agentProfile);

				this.synchronizationService.notifyCreation(this.agentProfile);
				// message mail
				if (BooleanUtils.isTrue(this.emailService.authorizedToSendMail(this.agentProfile.getOrganismDepartment().getOrganism()))) {
					this.sendMail(password);
				}

			} catch (Exception e) {
				this.errorMessage = this.getMessages().get("recording-error-message");
				return null;
			}

			this.modifyRolesPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
			this.modifyRolesPage.setAgentProfile(this.agentProfile);
			this.modifyRolesPage.setAgentProfileCreation(Boolean.TRUE);
			return this.modifyRolesPage;
		}
	}

	/**
	 * This methods allows to send login and password for the new user
	 *
	 * @param password : the password generated for the new user
	 * @throws MessagingException
	 */
	public void sendMail(String password) {
		// message mail
		StringBuilder message = new StringBuilder();
		message.append(this.getMessages().get("login-message"));
		message.append(this.agentProfile.getEmail());
		message.append("\n");
		message.append(this.getMessages().get("password-message"));
		message.append(password);
		message.append("\n\n");
		message.append(this.getMessages().get("courtesy-message"));

		MessageEmail messageEmail = new MessageEmail(ProfileTypeValue.AGENT,
				MessageEmail.EmailContext.AGENT_ORGANISME_CREATE, this.agentProfile.getUser().getUsername(), password,
				this.agentProfile.getOrganismDepartment().getOrganism().getLabel());

		String[] recipients = new String[1];
		recipients[0] = this.agentProfile.getEmail();
		this.emailService.silentSendMail(MyEc3EmailConstants.getSender(), MyEc3EmailConstants.getFrom(), recipients,
				this.getMessages().get("subject-message"),
				messageEmail.generateContent(this.getMessages(), this.organismDepartment.getOrganism().getCustomer()));
	}

	/**
	 * Fill agent's address using organism's address
	 */
	public void fillAgentprofileAddressFields() {
		if ((this.organismDepartment != null) && (this.organismDepartment.getOrganism() != null)) {

			this.agentProfile.setAddress(new Address());
			Address departmentAddress = this.organismDepartment.getAddress().clone();
			Address organismAddress = this.organismDepartment.getOrganism().getAddress().clone();

			// FILL ADDRESS FIELDS
			if (departmentAddress != null && !this.organismDepartment.getName().equals("root")) {
				// City field
				if (!departmentAddress.getCity().isEmpty()) {
					this.agentProfile.getAddress().setCity(departmentAddress.getCity());
				} else {
					this.agentProfile.getAddress().setCity(organismAddress.getCity());
				}

				// Country field
				if (departmentAddress.getCountry() != null) {
					this.agentProfile.getAddress().setCountry(departmentAddress.getCountry());
				} else {
					this.agentProfile.getAddress().setCountry(organismAddress.getCountry());
				}

				// PostalAddress field
				if (!departmentAddress.getPostalAddress().isEmpty()
						&& !(departmentAddress.getPostalAddress().equals(" "))) {
					this.agentProfile.getAddress().setPostalAddress(departmentAddress.getPostalAddress());
				} else {
					this.agentProfile.getAddress().setPostalAddress(organismAddress.getPostalAddress());
				}

				// Postal code field
				if (!departmentAddress.getPostalCode().isEmpty()) {
					this.agentProfile.getAddress().setPostalCode(departmentAddress.getPostalCode());
				} else {
					this.agentProfile.getAddress().setPostalCode(organismAddress.getPostalCode());
				}
			} else {
				if (organismAddress != null) {
					this.agentProfile.setAddress(organismAddress);
				}
			}
		}
	}

	public void setCertificateValid(Boolean certificateValid) {
		this.certificateValid = certificateValid;
	}

	public Boolean getCertificateValid() {
		return this.certificateValid;
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
