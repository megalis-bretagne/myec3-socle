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
package org.myec3.socle.core.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.constants.MyEc3EmailConstants;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismStatus;
import org.myec3.socle.core.domain.model.enums.OrganismMemberStatus;
import org.myec3.socle.core.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete implementation of methods related to email management.
 *
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti<denis.cucchietti@atosorigin.com>
 */
@Service("emailService")
public class EmailServiceImpl implements EmailService {

	private static final Log logger = LogFactory.getLog(EmailServiceImpl.class);

	private static final String SMTP_HOST = "mail.smtp.host";
	private static final String EMAIL_HEADER_CHARSET = "charset";
	private static final String EMAIL_HEADER_CHARSET_VALUE = "UTF-8";
	private static final String EMAIL_CONTENT_TYPE = "text/html; charset=utf-8";
	private static final String PAPPERBOY_HEADER = "x-ppbbearer";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void sendMail(String sender, String from, String[] recipients, String subject, String message)
			throws MessagingException {

		// validate parameters
		Assert.notNull(from, "from is mandatory. null value is forbidden.");
		Assert.notNull(recipients, "recipients is mandatory. null value is forbidden.");
		Assert.notNull(subject, "subject is mandatory. null value is forbidden.");
		Assert.notNull(message, "message is mandatory. null value is forbidden.");

		boolean debug = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put(SMTP_HOST, MyEc3EmailConstants.getSmtpRelay());

		// create some properties and get the default Session
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		// create a message
		Message msg = new MimeMessage(session);

		// set the from and to address
		// InternetAddress addressFrom = new InternetAddress(from);
		try {
			InternetAddress addressFrom = new InternetAddress(from, MyEc3EmailConstants.getSender());
			msg.setFrom(addressFrom);
		} catch (UnsupportedEncodingException e) {
			logger.error("Error while setting sender of email : " + e);
		}

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Email header
		msg.addHeader(EMAIL_HEADER_CHARSET, EMAIL_HEADER_CHARSET_VALUE);

		if (!StringUtils.isEmpty(MyEc3EmailConstants.getPapperBoyHeader())) {
			msg.addHeader(PAPPERBOY_HEADER, MyEc3EmailConstants.getPapperBoyHeader());
		}

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(message, EMAIL_CONTENT_TYPE);
		logger.info("Sending email with subject : '" + subject + "' to " + Arrays.toString(recipients));
		try {
			Transport.send(msg);
		} catch (MessagingException e) {
			logger.error("Error while sending email : " + e);
			throw e;
		}

	}

	@Override
	public void silentSendMail(String sender, String from, String[] recipients, String subject, String message) {
		try {
			this.sendMail(sender, from, recipients, subject, message);
		} catch (MessagingException e) {
			// Silent fail
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean authorizedToSendMail(Organism organism) {
		Assert.notNull(organism,
				"organism is mandatory. null value is forbidden.");

		if (organism.getOrganismStatus() != null) {
			// If the organism is a prospect or disabled or don we don't send
			// the mail containing
			// login and password for the new user
			Set<OrganismStatus> organismStatuses = organism.getOrganismStatus();
			Date date = organismStatuses.iterator().next().getDate();
			String label = organismStatuses.iterator().next().getStatus().getLabel();
			for(OrganismStatus organismStatus : organismStatuses){
				Date date_tmp = organismStatus.getDate();
				if(date_tmp.after(date)){
					date = date_tmp;
					label = organismStatus.getStatus().getLabel();
				}
			}
			if (!(label.equals(OrganismMemberStatus.PROSPECT.getLabel()))
					&& !(label.equals(OrganismMemberStatus.REFUS_D_ADHERER.getLabel()))
					&& !(label.equals(OrganismMemberStatus.RESILIE.getLabel()))) {
				return Boolean.TRUE;
			}
		} else {
			// If organism's member status is null we allow to send a mail
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}
}
