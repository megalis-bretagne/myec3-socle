/**
 * 
 */
package org.myec3.socle.webapp.pages.user;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.constants.MyEc3Constants;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.ProjectAccount;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.ProjectAccountService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.core.tools.EbDate;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * @author FR19949
 * 
 */
public class NewPassword extends AbstractPage {

	private static final Log logger = LogFactory
			.getLog(RegeneratePassword.class);

	@SuppressWarnings("unused")
	@Property
	private String logoutUrl = MyEc3Constants.J_SPRING_SECURITY_LOGOUT;

	@Component(id = "new_password_form")
	private Form form;

	@SuppressWarnings("unused")
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@SuppressWarnings("unused")
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@SuppressWarnings("unused")
	@Property
	private Boolean formBlock;

	@Property
	@Persist
	private String newPassword;

	@Property
	@Persist
	private String newPasswordConfirm;

	@Property
	@Persist
	private String urlAfterRegen;

	@Inject
	private ComponentResources componentResources;

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
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link org.myec3.socle.core.domain.model.Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	private User user;
	private String hash;

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		this.formBlock = Boolean.FALSE;
		return Boolean.TRUE;
	}

	// Activate n Passivate
	@OnEvent(value = EventConstants.ACTIVATE)
	public Object onActivate(Long externalId, String hash) throws UnsupportedEncodingException {
		logger.debug("--- externalId [" + externalId + "]");
		logger.debug("--- hash [" + hash + "]");

		this.formBlock = Boolean.TRUE;

		this.user = userService.findByExternalId(externalId);
		this.hash = hash;
		if (null == this.user) {
			this.formBlock = Boolean.FALSE;
			this.errorMessage = this.getMessages().get("url-error");
		} else {
			// Verify hashcode
			String controlKeyNewPassword = user.getControlKeyNewPassword();
			if (controlKeyNewPassword != null
					&& !controlKeyNewPassword.isEmpty()) {
				// HOT FIX for PHTEBEXPLOIT-206
				String tmpHash = URLDecoder.decode(this.hash, "UTF-8");
				if (!userService.isPasswordOk(controlKeyNewPassword, tmpHash)) {
					this.user = null;
					this.formBlock = Boolean.FALSE;
					this.errorMessage = this.getMessages().get("url-error");
				} else {
					// Verify url timestamp
					String[] parts = controlKeyNewPassword.split(",");

					String strTimestamp = parts[0]; // timestamp
					Date date = new Date(Long.parseLong(strTimestamp));
					logger.debug("Date création url : " + date);

					Date expDate = EbDate.addDays(date,
							GuWebAppConstants.expirationTimeUrlModifPassword);
					logger.debug("Date expiration url : " + expDate);

					Date now = EbDate.getDateNow();
					if (now.after(expDate)) {
						this.user = null;
						this.formBlock = Boolean.FALSE;
						this.errorMessage = this.getMessages().get("url-error");
					}
				}
			} else {
				this.user = null;
				this.formBlock = Boolean.FALSE;
				this.errorMessage = this.getMessages().get("url-error");
			}
		}
		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.PASSIVATE)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object onPassivate() {
		List activationParameters = new ArrayList();
		if (this.user != null) {
			activationParameters.add(this.user.getExternalId());
			activationParameters.add(hash);
		}
		return activationParameters;
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "new_password_form")
	public Boolean validationForm() {

		if (this.user == null || null == this.user.getControlKeyNewPassword()
				|| this.user.getControlKeyNewPassword().isEmpty()) {
			this.form.recordError(this.getMessages().get(
					"url-error"));
			return Boolean.FALSE;
		}

		if (null != newPassword && null != newPasswordConfirm) {
			if (!newPassword.equals(newPasswordConfirm)) {
				this.form.recordError(this.getMessages().get(
						"password-confirmation-error"));
				return Boolean.FALSE;
			}

			if (!userService.isPasswordConform(newPassword)) {
				this.form.recordError(this.getMessages().get(
						"password-conformity-error"));
				return Boolean.FALSE;
			}

		} else if (null != newPassword) {
			this.form.recordError(this.getMessages().get(
					"confirmation-missing-error"));
			return Boolean.FALSE;
		} else if (null != newPasswordConfirm) {
			this.form.recordError(this.getMessages().get(
					"new-password-missing-error"));
			return Boolean.FALSE;
		}

		this.successMessage = null;
		return Boolean.TRUE;
	}

	@OnEvent(value = EventConstants.SUCCESS)
	public Object successForm() {

		// Update user informations
		String hashPwd = this.userService
				.generateHashPassword(this.newPassword);

		// password
		this.user.setPassword(hashPwd);
		// dates
		Date now = EbDate.getDateNow();
		this.user.setModifDatePassword(now);
		this.user.setExpirationDatePassword(EbDate.addDays(now,
				GuWebAppConstants.expirationTimePassword));
		// update control key
		this.user.setControlKeyNewPassword(null);

		// Enable the user
		this.user.setEnabled(true);
		this.user.setConnectionAttempts(0);

		this.userService.update(this.user);

		List<Profile> profiles = this.profileService
				.findAllProfilesByUser(this.user);

		if (profiles.size() != 0) {
			for (Profile profile : profiles) {
				this.synchronizationService.notifyUpdate(profile);
			}
		} else {
			// In case of its a project account we must update the password
			// contained in project account table
			ProjectAccount projectAccount = this.projectAccountService
					.findByLogin(this.user.getUsername());
			if (null != projectAccount) {
				projectAccount.setPassword(hashPwd);
				this.projectAccountService.update(projectAccount);
			}
		}

		// Clear all persistant fields
		componentResources.discardPersistentFieldChanges();

		this.successMessage = "OK";
		this.user = null;
		this.formBlock = Boolean.FALSE;
		this.urlAfterRegen = GuWebAppConstants.PORTAIL_BASE_URL;
		return Boolean.TRUE;
	}
}