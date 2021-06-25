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
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.pages.AbstractPage;

import javax.inject.Named;

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
