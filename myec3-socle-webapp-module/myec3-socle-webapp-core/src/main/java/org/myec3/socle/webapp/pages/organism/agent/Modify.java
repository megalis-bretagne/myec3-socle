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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.pages.AbstractPage;

import javax.inject.Named;
import java.util.List;

/**
 * Page used to modify an agent profile{@link AgentProfile}.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/agent/Modify.tml
 * 
 * @see "WebSecurityConfig" to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
public class Modify extends AbstractPage {

	private static final Log logger = LogFactory.getLog(Modify.class);

	@Inject
	@Service("userService")
	private UserService userService;

	@Inject
	private Messages messages;

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
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects
	 */
	@Inject
	@Named("profileService")
	private ProfileService profileService;

	@InjectPage
	@Property
	private View viewPage;

	@Property
	private String errorMessage;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private AgentProfile agentProfile;

	@Persist
	private AgentProfile oldAgentProfile;

	private String emailOldValue;

	private Boolean usernameAlreadyExists;

	@Property
	private boolean isHimselfAndNotAdmin = false;

	@Persist(PersistenceConstants.FLASH)
	private String certificateErrorMessage;

	@Persist(PersistenceConstants.FLASH)
	private String certificateSuccessMessage;

	private Boolean certificateValid;

	private Boolean checkCertificate;

	@Persist(PersistenceConstants.FLASH)
	private Boolean activateOnce;

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return viewPage;
	}

	/**
	 * @param id : organism id
	 * @return : Object
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {

		if (this.activateOnce == null || this.activateOnce.equals(Boolean.FALSE)) {
			super.initUser();

			this.agentProfile = this.agentProfileService.findOne(id);
			if (null == this.agentProfile) {
				return false;
			}

			// redirect to the main page if the user is not Enable
			if (!this.agentProfile.getUser().isEnabled()) {
				return viewPage;
			}
			// fill address fields of the agent
			this.fillOrganismDepartmentAddressFields();

			// Save email old value
			if (null == this.getEmailOldValue()) {
				this.setEmailOldValue(this.agentProfile.getEmail());
			}

			if (null == this.agentProfile.getAddress()) {
				this.agentProfile.setAddress(new Address());
			}
		}
		if (this.agentProfile == null) {
			this.agentProfile = this.oldAgentProfile;
		}
		// Check if loggedUser can access at this page
		return this.hasRights(this.agentProfile);
	}

	@Override
	public Object hasRights(Profile profile) {
		Profile loggedProfile = super.getLoggedProfile();
		if (loggedProfile != null && loggedProfile.equals(profile) && !super.getIsFunctionalAdmin()
				&& !super.getIsGlobalManagerAgent()) {
			this.isHimselfAndNotAdmin = true;
			return Boolean.TRUE;
		}
		return super.hasRights(profile);
	}

	private void checkCertificate() {

		// initialize the check to false
		this.checkCertificate = Boolean.FALSE;

		// get the user who have the same certificate
		List<User> users = this.userService.findUsersByCertificate(this.agentProfile.getUser().getCertificate());
		logger.debug("Users matching the same certificate : " + users.toString());
		// If we have at least 1 user with same certificate
		String userInfos = " ";
		if (users.size() > 0) {

			// check the current users
			for (User user : users) {

				// Is it current user ? If yes, that's not okay
				if ((user.getId() != this.agentProfile.getUser().getId())) {

					// Get the user associate Profile
					List<Profile> profiles = this.profileService.findAllProfilesByUser(user);

					// We have at least a profile !
					if (profiles != null && profiles.size() == 1) {
						for (Profile userProfile : profiles) {

							// Profile is deleted or not (FALSE means deleted)
							logger.debug("Is Profile enabled : " + userProfile.getEnabled());
							if (!userProfile.getEnabled().equals(Boolean.FALSE)) {

								logger.debug("Certificate Already in use !");

								// Display error message
								this.checkCertificate = Boolean.TRUE;
								Structure userStructure = this.userService.findUserOrganismStructure(user);

								// Construction or error message
								if (user.getCivility() != null) {
									userInfos += user.getCivility() + " " + user.getFirstname() + " "
											+ user.getLastname() + " ";
								} else {
									userInfos += user.getFirstname() + " " + user.getLastname() + " ";
								}

								if (userStructure != null) {
									userInfos += "(Organisme : " + userStructure.getLabel() + "), ";
								}

								logger.debug("UserInfos : " + userInfos);
							}
						}
					}
				}
			}
		}

		if (this.checkCertificate.equals(Boolean.TRUE)) {
			logger.debug("Certificate already used !");
			userInfos = userInfos.substring(0, userInfos.length() - 2);
			this.certificateErrorMessage = this.getMessages().get("already-used-certificate") + userInfos;
		} else {
			logger.debug("No user is using the same certificate.");
			this.certificateSuccessMessage = this.getMessages().get("not-used-certificate");
		}

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

			this.checkCertificate();

			// set this boolean to true so we don't initiate AgentProfile again
			// and loose informations
			this.activateOnce = Boolean.TRUE;
			this.oldAgentProfile = this.agentProfile;
			// reset so we don't loop in this function
			this.certificateValid = Boolean.FALSE;
			return this;
		} else {
			try {
				//cas enregistrer si il y a un certificat présent on force la vérification avant l'update
				if(this.agentProfile.getUser().getCertificate() != null ){
					this.checkCertificate();
					if (this.checkCertificate.equals(Boolean.TRUE)) {
						logger.debug("Certificate already used !");
						// set this boolean to true so we don't initiate AgentProfile again
						// and loose informations
						this.activateOnce = Boolean.TRUE;
						this.oldAgentProfile = this.agentProfile;
						// reset so we don't loop in this function
						this.certificateValid = Boolean.FALSE;
						return this;
					}
				}
				this.agentProfileService.update(this.agentProfile);
				this.synchronizationService.notifyUpdate(this.agentProfile);

				this.viewPage.setSuccessMessage(this.getMessages().get("recording-success-message"));

				this.viewPage.setAgentProfile(this.agentProfile);
				return this.viewPage;
			} catch (Exception e) {
				this.errorMessage = this.getMessages().get("recording-error-message");
				return null;
			}
		}

	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.agentProfile != null) ? this.agentProfile.getId() : null;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		this.viewPage.setAgentProfile(this.agentProfile);
		return View.class;
	}

	public void fillOrganismDepartmentAddressFields() {
		if ((this.agentProfile.getOrganismDepartment() != null)
				&& (this.agentProfile.getOrganismDepartment().getOrganism() != null)) {

			Address departmentAddress = this.agentProfile.getOrganismDepartment().getAddress();
			Address organismAddress = this.agentProfile.getOrganismDepartment().getOrganism().getAddress();
			Address currentAgentAddress = this.agentProfile.getAddress();

			// FILL ADDRESS FIELDS
			if ((departmentAddress != null) && (currentAgentAddress != null)) {
				// City field
				if (currentAgentAddress.getCity().isEmpty()) {
					if (!departmentAddress.getCity().isEmpty()) {
						this.agentProfile.getAddress().setCity(departmentAddress.getCity());
					} else {
						this.agentProfile.getAddress().setCity(organismAddress.getCity());
					}
				}
				// Country field
				if (currentAgentAddress.getCountry() == null) {
					if (departmentAddress.getCountry() != null) {
						this.agentProfile.getAddress().setCountry(departmentAddress.getCountry());
					} else {
						this.agentProfile.getAddress().setCountry(organismAddress.getCountry());
					}
				}
				// PostalAddress field
				if ((currentAgentAddress.getPostalAddress().isEmpty())
						|| (currentAgentAddress.getPostalAddress().equals(" "))) {
					if (!departmentAddress.getPostalAddress().isEmpty()
							&& !(departmentAddress.getPostalAddress().equals(" "))) {
						this.agentProfile.getAddress().setPostalAddress(departmentAddress.getPostalAddress());
					} else {
						this.agentProfile.getAddress().setPostalAddress(organismAddress.getPostalAddress());
					}
				}
				// Postal code field
				if (currentAgentAddress.getPostalCode().isEmpty()) {
					if (!departmentAddress.getPostalCode().isEmpty()) {
						this.agentProfile.getAddress().setPostalCode(departmentAddress.getPostalCode());
					} else {
						this.agentProfile.getAddress().setPostalCode(organismAddress.getPostalCode());
					}
				}
			} else {
				if ((organismAddress != null) && (currentAgentAddress == null)) {
					this.agentProfile.setAddress(organismAddress);
				}
			}
		}
	}

	// Getters n Setters
	public String getEmailOldValue() {
		return emailOldValue;
	}

	public void setEmailOldValue(String emailOldValue) {
		this.emailOldValue = emailOldValue;
	}

	public Boolean getUsernameAlreadyExists() {
		if (this.usernameAlreadyExists == null) {
			return Boolean.FALSE;
		}
		return usernameAlreadyExists;
	}

	public void setUsernameAlreadyExists(Boolean usernameAlreadyExists) {
		this.usernameAlreadyExists = usernameAlreadyExists;
	}

	public boolean isHimselfAndNotAdmin() {
		return isHimselfAndNotAdmin;
	}

	public void setHimselfAndNotAdmin(boolean isHimselfAndNotAdmin) {
		this.isHimselfAndNotAdmin = isHimselfAndNotAdmin;
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

	public void setCertificateValid(Boolean certificateValid) {
		this.certificateValid = certificateValid;
	}

	public Boolean getCertificateValid() {
		return this.certificateValid;
	}
}
