package org.myec3.socle.webapp.batch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.constants.MyEc3EmailConstants;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.service.EmailService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.tools.EbDate;
import org.myec3.socle.core.util.CronUtils;
import org.myec3.socle.webapp.entities.MessageEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("PasswordExpirationNotification")
public class PasswordExpirationNotification {

	/**
	 * Date format
	 */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(PasswordExpirationNotification.class);

	/**
	 * Business Service providing methods and specifics operations on {@link Profil}
	 * objects
	 */
	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	/**
	 * Business Service providing methods mail
	 */
	@Autowired
	@Qualifier("emailService")
	private EmailService emailService;

	@Autowired
	@Qualifier("configProperties")
	Properties messages;

	/**
	 * 
	 * @param nbDaysBeforeExpiration
	 */
	public void launchNotification(List<String> nbDaysBeforeExpiration) {

		if (CronUtils.isCronServer()) {
			logger.info("**************** START LAUNCH EXPIRATION PASSWORD NOTIFICATION *****************");

			for (String strNbDay : nbDaysBeforeExpiration) {

				int nbDay = Integer.parseInt(strNbDay);

				logger.info("Launch notification " + nbDay + " day(s) before password expiration");
				Date expirationDate = EbDate.addDays(EbDate.getDateNow(), nbDay);

				// Find Profile by expiration date
				List<Profile> listProfile = profileService
						.findAllProfileEnabledByExpirationDatePassword(expirationDate);
				logger.info("  --> Number of profile with an expiration date to " + sdf.format(expirationDate) + " : "
						+ listProfile.size());

				// for each profil
				for (Profile profile : listProfile) {

					String[] recipients = new String[1];
					recipients[0] = profile.getEmail();

					MessageEmail messageEmail = null;
					Map<String, String> mapAttributes = new HashMap<String, String>();

					// Expiration date
					mapAttributes.put(MessageEmail.ATTR_NB_DAYS_EXPIRATION_DATE_PASSWORD, strNbDay);

					// Expiration date
					mapAttributes.put(MessageEmail.ATTR_EXPIRATION_DATE_PASSWORD, sdf.format(expirationDate));

					// Identifiant
					mapAttributes.put(MessageEmail.ATTR_USER_ID, profile.getUsername());

					// Profil mail
					mapAttributes.put(MessageEmail.ATTR_PROFIL_MAIL, profile.getEmail());

					// Structure name
					if (profile instanceof AgentProfile) {
						AgentProfile agent = (AgentProfile) profile;
						mapAttributes.put(MessageEmail.ATTR_STRUCTURE_NAME,
								agent.getOrganismDepartment().getOrganism().getLabel());
					} else if (profile instanceof EmployeeProfile) {
						EmployeeProfile employee = (EmployeeProfile) profile;
						mapAttributes.put(MessageEmail.ATTR_STRUCTURE_NAME,
								employee.getCompanyDepartment().getCompany().getLabel());
					} else {
						mapAttributes.put(MessageEmail.ATTR_STRUCTURE_NAME, " ");
					}

					if (nbDay == 0) {

						messageEmail = new MessageEmail(MessageEmail.EmailContext.EXPIRED_PASSWORD, mapAttributes);
					} else {
						messageEmail = new MessageEmail(MessageEmail.EmailContext.PASSWORD_ABOUT_TO_EXPIRED,
								mapAttributes);
					}

					// Envoi d'un mail de notification
					try {
						// send mail
						emailService.sendMail(MyEc3EmailConstants.getSender(), MyEc3EmailConstants.getFrom(),
								recipients, messages.getProperty("subject-password-expired-message-" + nbDay),
								messageEmail.generateContent(messages, null));

						logger.info("Send mail at  : " + profile.getEmail());
					} catch (MessagingException e) {
						logger.error(e);
					}
				}
			}
			logger.info("**************** END LAUNCH EXPIRATION PASSWORD NOTIFICATION *****************");
		} else {
			logger.info("Not start PasswordExpirationNotification : Server hostname different");
		}
	}
}
