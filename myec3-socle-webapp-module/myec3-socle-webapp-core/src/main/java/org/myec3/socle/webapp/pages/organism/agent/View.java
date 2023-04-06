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

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.constants.MyEc3EsbConstants;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.core.tools.EbDate;
import org.myec3.socle.synchro.api.SynchronizationConfiguration;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Page used to display agent profile details {@link AgentProfile}<br />
 *
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/organism/agent/View.tml<br />
 *
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 *
 *
 * @author Anthony Colas <anthony.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class View extends AbstractPage {

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
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Inject
	@Named("userService")
	private UserService userService;

	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	@InjectPage
	@Property
	private View viewPage;

	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Persist(PersistenceConstants.FLASH)
	private String successNewPassword;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage2;

	private AgentProfile agentProfile;

	@Property
	private boolean isHimselfAndNotAdmin = false;

	@Persist(PersistenceConstants.FLASH)
	private Boolean passwordRegeneration;

	@Inject
	private Messages messages;

	@Property
	private String competences;

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		super.initUser();
		return Index.class;
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

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		super.initUser();

		this.agentProfile = this.agentProfileService.findOne(id);
		if (null == this.agentProfile) {
			return Boolean.FALSE;
		}

		if (null == this.agentProfile.getAddress()) {
			this.agentProfile.setAddress(new Address());
		}

		//Convert competences to string
		if (agentProfile.getCompetences() != null) {
			competences = agentProfile.getCompetences().stream().map(Competence::getName).collect(Collectors.joining(", "));
		}

		// Check if loggedUser can access at this page
		return this.hasRights(this.agentProfile);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.agentProfile != null) ? this.agentProfile.getId() : null;
	}

	@OnEvent(value = EventConstants.ACTION, component = "regeneratePassword")
	public Object regeneratePasswordOfAgentProfile() {
		try {
			// redirect to the main page if the user is not Enable
			if (!this.agentProfile.getUser().isEnabled()) {
				return viewPage;
			}

			// Password to display
			String password = this.userService.generatePassword();

			// Generate HASH
			this.agentProfile.getUser().setPassword(this.userService.generateHashPassword(password));

			this.agentProfile.getUser().setModifDatePassword(EbDate.getDateNow());

			this.agentProfile.getUser()
					.setExpirationDatePassword(EbDate.addDays(this.agentProfile.getUser().getModifDatePassword(),
							GuWebAppConstants.expirationTimeAgentRegeneratePassword));

			this.userService.update(this.agentProfile.getUser());

			// We have to build a List of application, to send the synchronization to the
			// GRC only
			List<Long> listOfApplicationIdToSynchronize = new ArrayList<Long>();
			Application grcApplication = applicationService.findByName(MyEc3ApplicationConstants.GRC_SERVICE);

			if (grcApplication != null) {
				listOfApplicationIdToSynchronize.add(grcApplication.getId());
			}

			// build the synchronizationConfiguration
			SynchronizationConfiguration synchronizationConfiguration = new SynchronizationConfiguration();
			synchronizationConfiguration.setSendingApplication(MyEc3EsbConstants.getApplicationSendingJmsName());
			synchronizationConfiguration.setSynchronizationType(SynchronizationType.SYNCHRONIZATION);
			synchronizationConfiguration.setListApplicationIdToResynchronize(listOfApplicationIdToSynchronize);

			// Synchronize the resource
			this.synchronizationService.notifyUpdate(this.agentProfile.getUser(), synchronizationConfiguration);

			this.setPasswordRegeneration(Boolean.TRUE);

			this.successMessage = this.messages.get("regenerate-password-success-agent-1");

			this.successNewPassword = password;

			this.successMessage2 = this.messages.get("regenerate-password-success-agent-2") + " "
					+ GuWebAppConstants.expirationTimeAgentRegeneratePassword + " "
					+ this.messages.get("regenerate-password-success-agent-2-2");

		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("regenerate-password-error");
		}
		return this;
	}

	public Boolean getHasRightsOnOrganism() {
		if (this.getIsAdmin() || this.hasRightsOnOrganism(this.agentProfile.getOrganismDepartment().getOrganism())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	private String getMessage(Object messages, String key) {
		String value = "";
		if (messages instanceof Messages) {
			Messages message = (Messages) messages;
			value = message.get(key);
		} else {
			Properties message = (Properties) messages;
			value = message.getProperty(key);
		}
		return value;
	}

	// Getters n Setters
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	/**
	 * @return the successNewPassword
	 */
	public String getSuccessNewPassword() {
		return successNewPassword;
	}

	/**
	 * @param successNewPassword the successNewPassword to set
	 */
	public void setSuccessNewPassword(String successNewPassword) {
		this.successNewPassword = successNewPassword;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getSuccessMessage2() {
		return successMessage2;
	}

	public void setSuccessMessage2(String successMessage2) {
		this.successMessage2 = successMessage;
	}

	public void setAgentProfile(AgentProfile profile) {
		this.agentProfile = profile;
	}

	public AgentProfile getAgentProfile() {
		return this.agentProfile;
	}

	public SimpleDateFormat getDateFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat;
	}

	public SimpleDateFormat getTimestampFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return dateFormat;
	}

	public boolean isHimselfAndNotAdmin() {
		return isHimselfAndNotAdmin;
	}

	public void setHimselfAndNotAdmin(boolean isHimselfAndNotAdmin) {
		this.isHimselfAndNotAdmin = isHimselfAndNotAdmin;
	}

	public boolean getPasswordRegeneration() {
		return this.passwordRegeneration;
	}

	public void setPasswordRegeneration(boolean passwordRegeneration) {
		this.passwordRegeneration = passwordRegeneration;
	}

	public Boolean getHasPasswordRegeneration() {
		if (this.passwordRegeneration == Boolean.TRUE) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Check if current agent is enabled
	 * @return
	 */
	public Boolean getIsEnabled() {
		if (agentProfile.getUser() == null) {
			return Boolean.FALSE;
		}
		return agentProfile.getUser().isEnabled();
	}
}