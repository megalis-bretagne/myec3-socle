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
package org.myec3.socle.core.service;

import javax.mail.MessagingException;

import org.myec3.socle.core.domain.model.Organism;

/**
 * Interface defining Business Services methods and providing email methods.
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 */
public interface EmailService {

	/**
	 * Send a email message
	 * 
	 * @param sender
	 *            : sender mail address for the message
	 * @param from
	 *            : sender mail address for the message
	 * @param recipients
	 *            : array of recipients mail addres for the message
	 * @param subject
	 *            : subject of this message
	 * @param message
	 *            : message body
	 * @throws IllegalArgumentException
	 *             if one of these parameters is null
	 * @throws MessagingException
	 *             in case of technical error sending the message
	 */
	void sendMail(String sender, String from, String[] recipients,
			String subject, String message) throws MessagingException;

	/**
	 * Send a email message.
	 * Fails silently.
	 *
	 * @param sender
	 *            : sender mail address for the message
	 * @param from
	 *            : sender mail address for the message
	 * @param recipients
	 *            : array of recipients mail addres for the message
	 * @param subject
	 *            : subject of this message
	 * @param message
	 *            : message body
	 * @throws IllegalArgumentException
	 *             if one of these parameters is null
	 */
	void silentSendMail(String sender, String from, String[] recipients,
				  String subject, String message);

	/**
	 * This method allows to check if we are authorized to send a mail
	 * containing login and password at a user depending on the statut of user's
	 * organism
	 * 
	 * @return Boolean
	 */
	Boolean authorizedToSendMail(Organism organism);
}
