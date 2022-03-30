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
package org.myec3.socle.webapp.entities;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.Messages;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.pages.organism.CreateFirstAgent;
import org.myec3.socle.webapp.pages.user.RegeneratePassword;

/**
 * Generate the content of email which will be sent to the new admin or new
 * user<br />
 * when creating new admin of organism {@link CreateFirstAgent}, new user
 * of<br />
 * organim{@link Organism} or company{@link Company}<br />
 * and regenerate the new password{@link RegeneratePassword}.<br />
 *
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 */
public class MessageEmail {

	// Attributes constants
	public static final String ATTR_USER_ID = "USER_ID";
	public static final String ATTR_PROFIL_MAIL = "PROFIL_MAIL";
	public static final String ATTR_STRUCTURE_NAME = "STRUCTURE_NAME";
	public static final String ATTR_EXPIRATION_DATE_PASSWORD = "EXPIRATION_DATE_PASSWORD";
	public static final String ATTR_NB_DAYS_EXPIRATION_DATE_PASSWORD = "NB_DAYS_EXPIRATION_DATE_PASSWORD";

	// Webapp constants
	private static final String GU_BUNDLE_NAME = "webapp";
	private static final ResourceBundle GU_BUNDLE = ResourceBundle
			.getBundle(GU_BUNDLE_NAME);

	private EmailContext emailContext;

	private String username;

	private String password;

	private User user;

	private String name;

	private String establishmentName;

	private Map<String, String> mapAttributes;

	/**
	 *
	 * Enum used to know in which context the email must be sent
	 *
	 */
	public enum EmailContext {
		/**
		 * To use when the organism's admin is created
		 */
		ADMIN_ORGANISME_CREATE(1),
		/**
		 * To use when an agent is created
		 */
		AGENT_ORGANISME_CREATE(2),
		/**
		 * To use when the company's admin is created
		 */
		EMPLOYEE_COMPANY_CREATE(3),
		/**
		 * To use when you add an employee into a company
		 */
		EMPLOYEE_COMPANY_ADD(4),
		/**
		 * To use when a profile regenerate his password
		 */
		REGENERATE_PASSWORD(5),
		/**
		 * To use when you create a new employee by using the public company creation
		 * page and that the company already exists.
		 */
		EMPLOYEE_COMPANY_ALREADY_EXISTS(6),

		/**
		 * To use when a profil password is about to expire
		 */
		PASSWORD_ABOUT_TO_EXPIRED(7),

		/**
		 * To use when a profil password is expired
		 */
		EXPIRED_PASSWORD(8);

		private final int code;

		/**
		 * Default constructor. Do nothing.
		 */
		private EmailContext(int code) {
			this.code = code;
		}

		/**
		 * @return the code associated at the email context
		 */
		public int getCode() {
			return code;
		}
	}

	/**
	 * Contructor. Initialize the profile type value{@link ProfileTypeValue}
	 * (Agent,Employee or Admin), the email context, profile's username, profile's
	 * password and structure name.
	 *
	 * @param profileTypeValue : the profile type which must receive the email
	 * @param emailContext     : the context in which the mail is sent
	 * @param username         : the username of the profile who should receive the
	 *                         email
	 * @param password         : the password of the profile who should receive the
	 *                         email
	 * @param label            : the name of the profile's structure
	 */
	public MessageEmail(ProfileTypeValue profileTypeValue,
			EmailContext emailContext, String username, String password,
			String label) {
		this.emailContext = emailContext;

		if (null != label) {
			this.name = label;
		} else
			this.name = "label";

		this.username = username;
		this.password = password;
	}

	public MessageEmail(ProfileTypeValue profileTypeValue,
			EmailContext emailContext, String username, String password,
			String label, Establishment establishment) {
		this.emailContext = emailContext;

		if (null != label) {
			this.name = label;
		} else
			this.name = "label";

		if (null != establishment.getName()) {
			this.establishmentName = establishment.getName();
		} else {
			this.establishmentName = "établissement";
		}

		this.username = username;
		this.password = password;
	}

	/**
	 * Contructor. Initialize the profile type value{@link ProfileTypeValue}
	 * (Agent,Employee or Admin), the email context, profile's username, profile's
	 * user and structure name.
	 *
	 * @param profileTypeValue : the profile type which must receive the email
	 * @param emailContext     : the context in which the mail is sent
	 * @param username         : the username of the profile who should receive the
	 *                         email
	 * @param user             : the profile's user
	 * @param label            : the name of the profile's structure
	 */
	public MessageEmail(EmailContext emailContext, String username, User user,
			String label) {

		this.emailContext = emailContext;

		if (null != label) {
			this.name = label;
		} else
			this.name = "label";

		this.username = username;
		this.user = user;
	}

	public MessageEmail(EmailContext emailContext, String username, User user,
			String label, Establishment establishment) {

		this.emailContext = emailContext;

		if (null != label) {
			this.name = label;
		} else
			this.name = "label";

		if (null != establishment.getName()) {
			this.establishmentName = establishment.getName();
		} else {
			this.establishmentName = "établissement";
		}

		this.username = username;
		this.user = user;
	}

	/**
	 * Contructor. Initialize the profile type value{@link ProfileTypeValue}
	 * (Agent,Employee or Admin), the email context, profile's username, profile's
	 * user and structure name.
	 *
	 * @param profileTypeValue : the profile type which must receive the email
	 * @param emailContext     : the context in which the mail is sent
	 * @param username         : the username of the profile who should receive the
	 *                         email
	 * @param user             : the profile's user
	 * @param label            : the name of the profile's structure
	 */
	public MessageEmail(EmailContext emailContext,
			Map<String, String> mapAttributes) {

		this.emailContext = emailContext;
		this.mapAttributes = mapAttributes;
	}

	/**
	 * Generate the content of email which will be sent to the new admin or new user
	 * when creating new admin of organisme(CreateFirstAgent), new user of organimse
	 * or company and regenerate the new password
	 *
	 * @param messages : the text is defined in app.properties
	 *
	 * @return the content of the email to send
	 */
	public String generateContent(Messages messages, Customer customer) {
		return generateContent((Object) messages, customer);
	}

	/**
	 * Generate the content of email which will be sent to the new admin or new user
	 * when creating new admin of organisme(CreateFirstAgent), new user of organimse
	 * or company and regenerate the new password
	 *
	 * @param messages : the text is defined in app.properties
	 *
	 * @return the content of the email to send
	 */
	public String generateContent(Properties messages, Customer customer) {

		return generateContent((Object) messages, customer);
	}

	/**
	 * Generate the content of email which will be sent to the new admin or new user
	 * when creating new admin of organisme(CreateFirstAgent), new user of organimse
	 * or company and regenerate the new password
	 *
	 * @param messages : the text is defined in app.properties
	 *
	 * @return the content of the email to send
	 */
	private String generateContent(Object messages, Customer customer) {
		StringBuffer content = new StringBuffer();

		// common part: Logo, Hello message...
		// if customer is null we are in case of an employee or a project
		// account (no customer linked). In this case we are looking into file
		// app.properties to fill
		// logoUrl and hotlinePhone values.
		String logoUrl = GuWebAppConstants.MYEC3_BASE_URL + "/static/images/logoformail.png";
		String hotlinePhone = null;

		// set logo url and hotlinePhone
		if (customer != null) {
			hotlinePhone = customer.getHotlinePhone();
		} else {
			hotlinePhone = getMessage(messages, "telephone-content-email");
		}

		// if logo exists
		if ((logoUrl != null) && (!logoUrl.isEmpty())) {
			content.append("" + "<img src='" + logoUrl + "' />");
			content.append("<br/>");
		}

		content.append(getMessage(messages, "hello-message"));
		content.append("<br/><br/>");

		switch (this.emailContext) {
		case ADMIN_ORGANISME_CREATE:
			content.append(getMessage(messages, "welcome-admin"));
			content.append("<strong> ");
			content.append(name);
			content.append("</strong>");

			content.append(" " + getMessage(messages, "content-email"));
			content.append("<br/>");

			content.append(getMessage(messages, "content-email-login-info"));
			content.append("<br/><br/>");
			content.append(getMessage(messages, "identifiant-label"));
			content.append(" " + username);
			content.append("<br/>");
			content.append(getMessage(messages, "password-label"));
			content.append(" " + password);
			content.append("<br/><br/>");

			content.append(getMessage(messages, "content-email-info1"));
			content.append("<br/><small>");
			content.append(getMessage(messages, "content-email-info2"));
			content.append("</small><br/><br/>");

			content.append(getMessage(messages,
					"info-login-email-admin-organism"));
			content.append("<br/>");
			content.append("<a href=" + "\"").
                    append(String.format(getMessage(messages, "link-admin"), GuWebAppConstants.MYEC3_BASE_URL)).
                    append("\"").append(">").
                    append(String.format(getMessage(messages, "link-admin"), GuWebAppConstants.MYEC3_BASE_URL)).
                    append("</a>");
			content.append("<br/><br/>");
			break;

		case AGENT_ORGANISME_CREATE:
			content.append(getMessage(messages, "welcome-user-organism"));
			content.append("<strong> ");
			content.append(name);
			content.append("</strong> ");

			content.append(getMessage(messages, "content-email-user"));
			content.append("<br/><br/>");
			content.append(getMessage(messages, "content-email-user-info"));
			content.append("<br/></br>");
			content.append(getMessage(messages, "identifiant-label"));
			content.append(" " + username);
			content.append("<br/>");
			content.append(getMessage(messages, "password-label"));
			content.append(" " + password);
			content.append("<br/><br/>");

			content.append(getMessage(messages, "content-email-info1"));
			content.append("<br/><small>");
			content.append(getMessage(messages, "content-email-info2"));
			content.append("</small><br/><br/>");
			break;

		case EMPLOYEE_COMPANY_CREATE:
			content.append(getMessage(messages, "welcome-user-company"));
			content.append("<strong> ");
			content.append(name);
			content.append("</strong> ");
			content.append(getMessage(messages, "content-email"));
			content.append("<br/><br/>");

			content.append(getMessage(messages, "identifiant-label-company"));
			content.append(" " + username);
			content.append("<br/>");
			content.append(getMessage(messages, "password-label-company"));
			content.append(" " + password);
			content.append("<br/><br/>");

			content.append(getMessage(messages, "content-email-info1"));
			content.append("<br/><small>");
			content.append(getMessage(messages, "content-email-info2"));
			content.append("</small><br/><br/>");
			break;

		case EMPLOYEE_COMPANY_ADD:
			content.append(getMessage(messages, "welcome-user-company"));
			content.append("<strong> ");
			content.append(name);
			content.append("</strong> ");

			content.append(getMessage(messages, "content-email-establishment"));
			content.append("<strong>");
			content.append(establishmentName);
			content.append("</strong> ");

			content.append(getMessage(messages, "content-email-employe"));
			content.append("<br/>");

			content.append(getMessage(messages, "content-email-employe-login"));
			content.append("<br/>");

			content.append(getMessage(messages, "identifiant-label"));
			content.append(" " + username);
			content.append("<br/>");
			content.append(getMessage(messages, "password-label"));
			content.append(" " + password);
			content.append("<br/><br/>");

			content.append(getMessage(messages, "content-email-info1"));
			content.append("<br/><small>");
			content.append(getMessage(messages, "content-email-info2"));
			content.append("</small><br/><br/>");
			break;

		case EMPLOYEE_COMPANY_ALREADY_EXISTS:
			content.append(getMessage(messages, "notification-welcome-user"));
			content.append("<strong> ");
			content.append(name);
			content.append("</strong> ");

			content.append(getMessage(messages, "content-email-establishment"));
			content.append("<strong> ");
			content.append(establishmentName);
			content.append("</strong> ");

			content.append(getMessage(messages,
					"notification-content-email-employe"));
			content.append("<br/>");

			content.append(getMessage(messages,
					"notification-content-email-employe-login"));
			content.append("<br/>");

			content.append(getMessage(messages,
					"notification-identifiant-label"));
			content.append(" " + username);
			content.append("<br/>");
			content.append(getMessage(messages, "notification-firstname-label"));
			content.append(this.user.getFirstname());
			content.append("<br/>");
			content.append(getMessage(messages, "notification-lastname-label"));
			content.append(this.user.getLastname());
			content.append("<br/><br/>");

			content.append(getMessage(messages,
					"notification-content-email-info1"));
			content.append("<br/>");
			content.append(String.format(getMessage(messages,
					"notification-content-email-info2"), GuWebAppConstants.MYEC3_BASE_URL));
			content.append("<br/><br/>");
			break;

		case REGENERATE_PASSWORD:
			content.append(getMessage(messages, "content-regenerate-password-url-info1"));
			content.append("<br/>");

			content.append(getMessage(messages, "content-regenerate-password-url-info2"));
			content.append("<br/><br/>");

			content.append(getMessage(messages, "content-regenerate-password-url-info3"));
			content.append("<br/><br/><br/>");
			content.append(password);
			content.append("<br/><br/><br/>");

			content.append(getMessage(messages, "content-regenerate-password-url-info4"));
			content.append("<br/><br/>");

			content.append(getMessage(messages, "content-regenerate-password-url-info5"));
			content.append(" " + GuWebAppConstants.expirationTimeUrlModifPassword + " ");
			content.append(getMessage(messages, "content-regenerate-password-url-info6"));
			content.append("<br/><br/>");

			// Add username temporaly
			content.append(getMessage(messages,
					"content-regenerate-password-url-info7"));
			content.append(username);
			content.append("<br/><br/><br/>");

			break;

		case PASSWORD_ABOUT_TO_EXPIRED:

			content.append(MessageFormat
					.format(getMessage(
							messages,
							"content-email-password-about-to-expired-"
									+ mapAttributes.get(ATTR_NB_DAYS_EXPIRATION_DATE_PASSWORD)),
							mapAttributes.get(ATTR_USER_ID),
							mapAttributes.get(ATTR_STRUCTURE_NAME),
							mapAttributes.get(ATTR_EXPIRATION_DATE_PASSWORD)));

			content.append("<br/><br/>");
			content.append(getMessage(messages,
					"content-email-password-about-to-expired-info1"));
			content.append("<br/><br/>");

			String imgModifPwdURL = GU_BUNDLE
					.getString("filer.img.modif.pwd.mail.url");
			if ((imgModifPwdURL != null) && (!imgModifPwdURL.isEmpty())) {
				content.append("" + "<img src='" + imgModifPwdURL + "' />");
				content.append("<br/><br/>");
			}

			content.append(getMessage(messages,
					"content-email-password-about-to-expired-info2"));
			content.append("<br/><br/>");
			break;

		case EXPIRED_PASSWORD:
			content.append(MessageFormat.format(
					getMessage(messages, "content-email-password-expired"),
					mapAttributes.get(ATTR_USER_ID),
					mapAttributes.get(ATTR_STRUCTURE_NAME)));

			content.append("<br/><br/>");

			content.append(String.format(getMessage(messages,
					"content-email-password-expired-info1"), GuWebAppConstants.MYEC3_BASE_URL));
			content.append("<br/><br/>");

			content.append(getMessage(messages,
					"content-email-password-expired-info2"));
			content.append("<br/><br/>");

			break;
		}

		// the common part : footer

		// Check if hotline phone exists
		if (!StringUtils.isEmpty(hotlinePhone)) {
			content.append(getMessage(messages, "content-email-footer1") + " ");
			content.append("<strong>");
			content.append(" " + hotlinePhone + " ");
			content.append("</strong>");
			content.append(getMessage(messages, "content-email-footer3"));
			content.append("<br/><i>");
		}

		content.append(getMessage(messages, "content-email-footer2"));
		content.append("</i><br/>");

		return content.toString();
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
}
