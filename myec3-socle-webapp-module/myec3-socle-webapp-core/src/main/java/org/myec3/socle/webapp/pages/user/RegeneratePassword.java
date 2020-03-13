/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p/>
 * This file is part of MyEc3.
 * <p/>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p/>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.pages.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.inject.Named;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.services.URLEncoderImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.URLEncoder;
import org.myec3.socle.core.constants.MyEc3Constants;
import org.myec3.socle.core.constants.MyEc3EmailConstants;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.ProjectAccount;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.service.EmailService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.ProjectAccountService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.entities.MessageEmail;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Page used to regenerate user's password{@link User} or
 * {@link ProjectAccount}<br />
 * <p/>
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/user/RegeneratePassword.tml<br
 * />
 * <p/>
 * This page is visible by all users.<br />
 *
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class RegeneratePassword extends AbstractPage {

	private static final Log logger = LogFactory
			.getLog(RegeneratePassword.class);

	private static final int MAX_CONNECTION_ATTEMPTS = 6;

	@Component(id = "regenerate_password_form")
	private Form form;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects
	 */
	@Inject
	@Named("profileService")
	private ProfileService profileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link ProjectAccount} objects
	 */
	@Inject
	@Named("projectAccountService")
	private ProjectAccountService projectAccountService;

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
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	@Inject
	private ComponentResources componentResources;

	@Property
	private String logoutUrl = MyEc3Constants.J_SPRING_SECURITY_LOGOUT;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Property
	@Persist
	private String username;

	@Property
	@Persist
	private String answer;

	@Property
	@Persist
	private User user;

	@Property
	@Persist
	private Integer questionKey;

	@Property
	@Persist
	private String question;

	@Property
	@Persist
	private List<Profile> profiles;

	@Persist
	private Set<ProjectAccount> selectedProjectAccounts;

	@Persist
	private Set<Resource> selectedResource;

	@Persist
	private Set<Profile> selectedProfiles;

	private Profile profileLoop;

	private GenericListEncoder<Profile> profileEncoder;

	private List<Profile> availableProfiles;

	private List<Profile> allExistingProfiles;

	private boolean findNotEnabled;

	private boolean oneAccount;

	@Property
	private Boolean multipleAccount;

	private GenericListEncoder<ProjectAccount> projectAccountEncoder;

	private List<ProjectAccount> availableProjectAccounts;

	private List<ProjectAccount> allExistingProjectAccounts;

	private ProjectAccount projectAccountLoop;

	// Activate n Passivate
	@OnEvent(value = EventConstants.ACTIVATE)
	public Object activation() {
		return Boolean.TRUE;
	}

	public boolean beginRender() {
		if (getSelectedProfiles() == null) {
			setSelectedProfiles(new HashSet<Profile>());
		}
		return true;
	}

	@SetupRender
	public void onSetupRender() {

		this.oneAccount = false;
		this.findNotEnabled = false;
		this.multipleAccount = Boolean.FALSE;

		if (this.selectedProfiles == null) {
			this.selectedProfiles = new HashSet<Profile>();
		}
		if (this.selectedProjectAccounts == null) {
			this.selectedProjectAccounts = new HashSet<ProjectAccount>();
		}
		if (null != this.username) {

			this.availableProfiles = this.profileService
					.findAllProfileEnabledByEmailOrUsername(this.username);

			// Now we search if there is some account which are not activated
			this.allExistingProfiles = this.profileService.findAllByEmail(this.username);

			if (this.allExistingProfiles.size() == 0) {
				Profile profileTemp = this.profileService.findByUsername(this.username);
				if (profileTemp != null) {
					this.allExistingProfiles.add(profileTemp);
				}
			}

			if (this.allExistingProfiles.size() > this.availableProfiles.size()) {
				this.allExistingProfiles.removeAll(this.availableProfiles);

				if (this.allExistingProfiles.size() != 0) {
					this.availableProfiles.addAll(this.allExistingProfiles);
					this.findNotEnabled = true;
				}
			}

			// End of the research

			List<Profile> listRemovedProfile = new ArrayList<Profile>();
			for (Profile profile : availableProfiles) {
				if (profile instanceof AgentProfile) {
					if (!emailService
							.authorizedToSendMail(((AgentProfile) profile)
									.getOrganismDepartment().getOrganism())) {
						listRemovedProfile.add(profile);
					}
				}
			}

			if (listRemovedProfile.size() > 0) {
				this.availableProfiles.removeAll(listRemovedProfile);
			}

			this.availableProjectAccounts = this.projectAccountService
					.findAllEnabledByEmailOrUsername(this.username);

			// Now we search if there is some account which are not activated
			this.allExistingProjectAccounts = this.projectAccountService.findAllByEmail(this.username);

			if (this.allExistingProjectAccounts.size() == 0) {
				ProjectAccount projectAccountTemp = this.projectAccountService.findByLogin(this.username);
				if (projectAccountTemp != null) {
					this.allExistingProjectAccounts.add(projectAccountTemp);
				}
			}

			if (this.allExistingProjectAccounts.size() > this.availableProjectAccounts.size()) {
				this.allExistingProjectAccounts.removeAll(this.availableProjectAccounts);

				if (this.allExistingProjectAccounts.size() != 0) {
					this.availableProjectAccounts.addAll(this.allExistingProjectAccounts);
					this.findNotEnabled = true;
				}
			}

			// End of the research

			if ((this.availableProfiles.size() == 1
					&& this.availableProjectAccounts.size() == 0)
					|| (this.availableProfiles.size() == 0
							&& this.availableProjectAccounts.size() == 1)) {
				this.oneAccount = true;
			} else if ((this.availableProfiles.size() > 1 || this.availableProjectAccounts.size() > 1)
					&& this.findNotEnabled) {
				this.multipleAccount = Boolean.TRUE;
			}
		}
		this.getGenerateQuestion();
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "regenerate_password_form")
	public Boolean validationForm() {

		this.findNotEnabled = false;
		this.oneAccount = false;
		this.multipleAccount = Boolean.FALSE;

		// wrong answer
		if (null != this.answer) {
			if (!this.answer.equalsIgnoreCase(retrieveAnswer())) {
				form.recordError(this.getMessages().get("kaptcha-error"));
				return Boolean.FALSE;
			}
		} else {
			return Boolean.FALSE;
		}

		this.availableProfiles = this.profileService
				.findAllProfileEnabledByEmailOrUsername(this.username);

		// projectaccount is not a profile
		this.availableProjectAccounts = this.projectAccountService
				.findAllEnabledByEmailOrUsername(this.username);

		// Now we search if there is some account which are not activated
		this.allExistingProfiles = this.profileService.findAllByEmail(this.username);
		this.allExistingProjectAccounts = this.projectAccountService.findAllByEmail(this.username);

		if (this.allExistingProfiles.size() == 0) {
			Profile profileTemp = this.profileService.findByUsername(this.username);
			if (profileTemp != null) {
				this.allExistingProfiles.add(profileTemp);
			}
		}

		if (this.allExistingProjectAccounts.size() == 0) {
			ProjectAccount projectAccountTemp = this.projectAccountService.findByLogin(this.username);
			if (projectAccountTemp != null) {
				this.allExistingProjectAccounts.add(projectAccountTemp);
			}
		}

		if (this.allExistingProfiles.size() > this.availableProfiles.size()) {
			this.allExistingProfiles.removeAll(this.availableProfiles);

			if (this.allExistingProfiles.size() != 0) {
				this.availableProfiles.addAll(this.allExistingProfiles);

				for (Profile profile : allExistingProfiles) {
					if (isDisableByAdmin(profile)) {
						this.findNotEnabled = true;
					}
				}
			}
		}

		if (this.allExistingProjectAccounts.size() > this.availableProjectAccounts.size()) {
			this.allExistingProjectAccounts.removeAll(this.availableProjectAccounts);

			if (this.allExistingProjectAccounts.size() != 0) {
				this.availableProjectAccounts.addAll(this.allExistingProjectAccounts);
				this.findNotEnabled = true;
			}
		}

		// End of the research

		if (this.availableProfiles.size() > 1) {
			List<Profile> listRemovedProfile = new ArrayList<Profile>();
			for (Profile profile : availableProfiles) {
				if (profile instanceof AgentProfile) {
					if (!emailService
							.authorizedToSendMail(((AgentProfile) profile)
									.getOrganismDepartment().getOrganism())) {
						listRemovedProfile.add(profile);
					}
				}
			}
			if (listRemovedProfile.size() > 0) {
				this.availableProfiles.removeAll(listRemovedProfile);
				if (this.availableProfiles.size() == 0
						&& availableProjectAccounts.size() == 0) {
					this.errorMessage = this.getMessages().get(
							"no-activation-profile-error");
					return Boolean.FALSE;
				}
			}
		}

		// not displaying profile table
		if (this.availableProfiles.size() == 1
				&& this.availableProjectAccounts.size() == 0) {
			if (this.availableProfiles.get(0).getProfileType().getValue()
					.equals(ProfileTypeValue.AGENT)
					|| this.availableProfiles.get(0).getProfileType()
							.getValue().equals(ProfileTypeValue.EMPLOYEE)) {
				if (this.availableProfiles.get(0).isAgent()) {
					AgentProfile agent = (AgentProfile) this.availableProfiles
							.get(0);

					if (!this.emailService.authorizedToSendMail(agent
							.getOrganismDepartment().getOrganism())) {
						this.errorMessage = this.getMessages().get(
								"no-activation-profile-error");
						return Boolean.FALSE;
					}
				}
				if (this.findNotEnabled) {
					this.errorMessage = this.getMessages().get(
							"mono-no-activation-profile-error");
					this.oneAccount = true;
				} else {
					if (null == this.user) {
						this.user = this.availableProfiles.get(0).getUser();
						if (null == this.user) {
							this.errorMessage = this.getMessages().get(
									"no-corresponding-email-error");
						}
					}
				}
			} else {
				if (null == this.user) {
					this.user = this.availableProfiles.get(0).getUser();
					if (null == this.user) {
						this.errorMessage = this.getMessages().get(
								"no-corresponding-email-error");
					} else if (this.findNotEnabled) {
						this.errorMessage = this.getMessages().get(
								"mono-no-activation-profile-error");
						this.oneAccount = true;
					}
				} else if (this.findNotEnabled) {
					this.errorMessage = this.getMessages().get(
							"mono-no-activation-profile-error");
					this.oneAccount = true;
				}
			}
			return Boolean.FALSE;
		}

		// not displaying projectAccount table
		if (this.availableProjectAccounts.size() == 1
				&& this.availableProfiles.size() == 0) {
			if (null == this.user) {
				this.user = this.availableProjectAccounts.get(0).getUser();
				if (null == this.user) {
					this.errorMessage = this.getMessages().get(
							"no-corresponding-email-error");
				} else if (this.findNotEnabled) {
					this.errorMessage = this.getMessages().get(
							"mono-no-activation-profile-error");
				}
			} else if (this.findNotEnabled) {
				this.errorMessage = this.getMessages().get(
						"mono-no-activation-profile-error");

			}
			return Boolean.FALSE;
		}

		if ((this.availableProfiles.size() == 0)
				&& (this.availableProjectAccounts.size() == 0)) {
			this.errorMessage = this.getMessages().get(
					"no-corresponding-email-error");
			return Boolean.FALSE;
		}

		if (this.findNotEnabled) {
			this.multipleAccount = Boolean.TRUE;
		}

		this.successMessage = null;
		return Boolean.TRUE;
	}

	@OnEvent(value = EventConstants.SUCCESS)
	public Object successForm() {
		// dont display table
		if ((null == this.user) && (this.selectedProfiles.size() == 0)
				&& (this.selectedProjectAccounts.size() == 0)) {
			return null;
		} else if (this.selectedProfiles.size() != 0
				|| this.selectedProjectAccounts.size() != 0) {
			// send mail to selected profiles
			if (this.selectedProfiles.size() != 0) {
				for (Profile profile : this.selectedProfiles) {
					String controlKeyNewPassword = this.userService
							.generateControlKeyNewPassword();
					profile.getUser().setControlKeyNewPassword(
							controlKeyNewPassword);

					this.userService.update(profile.getUser());
					this.synchronizationService.notifyUpdate(profile);

					if (profile instanceof AgentProfile) {
						AgentProfile agentProfile = (AgentProfile) profile;

						sendMail(controlKeyNewPassword,
								String.valueOf(agentProfile.getUser().getExternalId()),
								agentProfile.getUser().getUsername(),
								profile.getEmail(), agentProfile
										.getOrganismDepartment().getOrganism()
										.getCustomer());
					} else {
						sendMail(controlKeyNewPassword,
								String.valueOf(profile.getUser().getExternalId()), profile.getUser().getUsername(),
								profile.getEmail(), null);
					}
				}
			}

			// send mail to selected project accounts
			if (this.selectedProjectAccounts.size() != 0) {
				for (ProjectAccount projectAccount : this.selectedProjectAccounts) {
					String controlKeyNewPassword = this.userService
							.generateControlKeyNewPassword();

					projectAccount.getUser().setControlKeyNewPassword(
							controlKeyNewPassword);

					this.userService.update(projectAccount.getUser());
					sendMail(controlKeyNewPassword,
							String.valueOf(projectAccount.getUser().getExternalId()), this.user.getUsername(),
							projectAccount.getEmail(), null);
				}
			}

		} else {
			// normal case (only one profile or project account)
			String controlKeyNewPassword = this.userService
					.generateControlKeyNewPassword();

			this.user.setControlKeyNewPassword(controlKeyNewPassword);
			this.userService.update(this.user);

			List<Profile> profiles = this.profileService
					.findAllProfilesByUser(this.user);

			if (profiles.size() != 0) {
				for (Profile profile : profiles) {
					if (profile instanceof AgentProfile) {
						AgentProfile agentProfile = (AgentProfile) profile;
						sendMail(controlKeyNewPassword,
								String.valueOf(this.user.getExternalId()), this.user.getUsername(),
								profile.getEmail(), agentProfile
										.getOrganismDepartment().getOrganism()
										.getCustomer());

					} else {
						sendMail(controlKeyNewPassword,
								String.valueOf(this.user.getExternalId()), this.user.getUsername(),
								profile.getEmail(), null);
					}
					this.synchronizationService.notifyUpdate(profile);
				}
			} else {
				// In case of project account table
				ProjectAccount projectAccount = this.projectAccountService
						.findByLogin(this.user.getUsername());
				if (null != projectAccount) {
					sendMail(controlKeyNewPassword,
							String.valueOf(this.user.getExternalId()), this.user.getUsername(),
							projectAccount.getEmail(), null);
				}
			}

		}

		// flush lists
		this.selectedProfiles.clear();
		this.profileLoop = null;
		this.user = null;
		if (null != this.availableProfiles) {
			this.availableProfiles.clear();
			this.availableProfiles = null;
		}
		this.selectedProjectAccounts.clear();
		this.projectAccountLoop = null;
		if (null != this.availableProjectAccounts) {
			this.availableProjectAccounts.clear();
			this.availableProjectAccounts = null;
		}

		// Clear all persistant fields
		componentResources.discardPersistentFieldChanges();

		this.successMessage = this.getMessages().get("sending-mail-success");

		return null;
	}

	/**
	 * Send an email with url to change password
	 *
	 * @param controlKeyNewPassword : the control key
	 * @param externalId            : externalId of User
	 * @param username              : username of User
	 * @param email                 : email to send
	 */
	private void sendMail(String controlKeyNewPassword, String externalId, String username,
			String email, Customer customer) {

		String url = null;

		try {
			URLEncoder urlEncoder = new URLEncoderImpl();

			// HOT FIX for PHTEBEXPLOIT-206
			url = GuWebAppConstants.MYEC3_BASE_URL + "/user/newpassword"
					+ "/"
					+ urlEncoder.encode(externalId)
					+ "/"
					+ urlEncoder.encode(java.net.URLEncoder
							.encode(this.userService.generateHashPassword(controlKeyNewPassword), "UTF-8"));

			logger.debug("URL for mail : " + url);
		} catch (Exception e) {
			logger.error(e);
		}

		MessageEmail messageEmail = new MessageEmail(null,
				MessageEmail.EmailContext.REGENERATE_PASSWORD, username, url,
				null);

		String[] recipients = new String[1];
		recipients[0] = email;

		try {
			this.emailService.sendMail(MyEc3EmailConstants.getSender(),
					MyEc3EmailConstants.getFrom(), recipients,
					this.getMessages().get("subject-regenerate-password-url"),
					messageEmail.generateContent(this.getMessages(), customer));
		} catch (MessagingException e) {
			this.errorMessage = this.getMessages().get("sending-mail-error");
			logger.error(this.getMessages().get("sending-mail-error") + e);
		}

	}

	/**
	 * Generate captcha
	 *
	 * @return a question
	 */
	public String getGenerateQuestion() {
		if (null == this.question) {
			Map<Integer, String> questions = new HashMap<Integer, String>();
			questions.put(0, this.getMessages().get("question-message")
					+ this.getMessages().get("question-0"));
			questions.put(1, this.getMessages().get("question-message")
					+ this.getMessages().get("question-1"));
			questions.put(2, this.getMessages().get("question-message")
					+ this.getMessages().get("question-2"));
			questions.put(3, this.getMessages().get("question-message")
					+ this.getMessages().get("question-3"));
			Random random = new Random();
			Integer rand = random.nextInt(questions.size());
			this.questionKey = rand;
			this.question = questions.get(rand);
		}
		return this.question;
	}

	/**
	 * @return a list of possible answers
	 */
	public String retrieveAnswer() {
		Map<Integer, String> answers = new HashMap<Integer, String>();
		answers.put(0, this.getMessages().get("answer-0"));
		answers.put(1, this.getMessages().get("answer-1"));
		answers.put(2, this.getMessages().get("answer-2"));
		answers.put(3, this.getMessages().get("answer-3"));

		return answers.get(this.questionKey);
	}

	/**
	 * Add Profile to the selected set if selected.
	 */
	public void setSelected(boolean selected) {
		if (selected) {
			getSelectedProfiles().add(getProfileLoop());
		} else {
			getSelectedProfiles().remove(getProfileLoop());
		}
	}

	/**
	 * @return true if selected profiles list contain the select user
	 */
	public boolean isSelected() {
		return getSelectedProfiles().contains(getProfileLoop());
	}

	/**
	 * @return true if selected project account list contain the select project
	 *         account
	 */
	public boolean isSelectedPP() {
		return getSelectedProjectAccounts().contains(getProjectAccountLoop());
	}

	/**
	 * Add projectAccount to the selected set if selected.
	 */
	public void setSelectedPP(boolean selected) {
		if (selected) {
			getSelectedProjectAccounts().add(getProjectAccountLoop());
		} else {
			getSelectedProjectAccounts().remove(getProjectAccountLoop());
		}
	}

	public GenericListEncoder<Profile> getProfileEncoder() {
		return profileEncoder;
	}

	/**
	 * @return the list of profiles which have this email or username
	 */
	public List<Profile> getAvailableProfiles() {
		return availableProfiles;
	}

	public Profile getProfileLoop() {
		// just for display, no set in db
		if (profileLoop.getProfileType().getValue()
				.equals(ProfileTypeValue.EMPLOYEE)) {
			EmployeeProfile employeeProfile = (EmployeeProfile) profileLoop;
			profileLoop.setLabel(employeeProfile.getCompanyDepartment()
					.getCompany().getLabel());
			profileLoop.setFunction("Utilisateur entreprise");
		} else if (profileLoop.getProfileType().getValue()
				.equals(ProfileTypeValue.AGENT)) {
			AgentProfile agentProfile = (AgentProfile) profileLoop;
			profileLoop.setLabel(agentProfile.getOrganismDepartment()
					.getOrganism().getLabel());
			profileLoop.setFunction("Agent public");
		} else {
			profileLoop.setFunction("Porteur de projet");
		}
		return profileLoop;
	}

	public void setProfileLoop(Profile profileLoop) {
		this.profileLoop = profileLoop;
	}

	public Set<ProjectAccount> getSelectedProjectAccounts() {
		return selectedProjectAccounts;
	}

	public void setSelectedProjectAccounts(
			Set<ProjectAccount> selectedProjectAccounts) {
		this.selectedProjectAccounts = selectedProjectAccounts;
	}

	public Set<Profile> getSelectedProfiles() {
		return selectedProfiles;
	}

	public void setSelectedProfiles(Set<Profile> selectedProfiles) {
		this.selectedProfiles = selectedProfiles;
	}

	public GenericListEncoder<ProjectAccount> getProjectAccountEncoder() {
		return projectAccountEncoder;
	}

	public List<ProjectAccount> getAvailableProjectAccounts() {
		return availableProjectAccounts;
	}

	public ProjectAccount getProjectAccountLoop() {
		// just for display, no set in db
		projectAccountLoop.setName("Porteur de projet");
		return projectAccountLoop;
	}

	public void setProjectAccountLoop(ProjectAccount projectAccountLoop) {
		this.projectAccountLoop = projectAccountLoop;
	}

	public Boolean getFoundAccounts() {
		if (this.getAvailableProfiles() != null && this.getAvailableProjectAccounts() != null
				&& (this.getAvailableProfiles().size() > 0
						|| this.getAvailableProjectAccounts().size() > 0)
				&& !this.oneAccount) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public Set<Resource> getSelectedResource() {
		return selectedResource;
	}

	public void setSelectedResource(Set<Resource> selectedResource) {
		this.selectedResource = selectedResource;
	}

	public Boolean getCanChangePassword() {
		return isDisableByAdmin(profileLoop);
	}

	private boolean isDisableByAdmin(Profile profile) {
		if (!profile.getUser().isEnabled() && profile.getUser().getConnectionAttempts() < MAX_CONNECTION_ATTEMPTS) {
			return true;
		}
		return false;
	}
}
