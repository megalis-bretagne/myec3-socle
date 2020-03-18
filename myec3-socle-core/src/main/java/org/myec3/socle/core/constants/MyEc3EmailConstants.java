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
package org.myec3.socle.core.constants;

import org.apache.commons.lang3.BooleanUtils;

import java.util.ResourceBundle;

/**
 * Constants class used to manage email
 * 
 * @see org.myec3.socle.core.service.EmailService
 * @see org.myec3.socle.core.service.impl.EmailServiceImpl
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public final class MyEc3EmailConstants {

	private static final String EMAIL_BUNDLE_NAME = "email";
	private static final ResourceBundle EMAIL_BUNDLE = ResourceBundle
			.getBundle(EMAIL_BUNDLE_NAME);

	public static boolean getEmailActivate(){
		return BooleanUtils.toBoolean(EMAIL_BUNDLE.getString("email.activate"));
	}

	/**
	 * @return the SMPT relay url used to send the email
	 */
	public static String getSmtpRelay() {
		return EMAIL_BUNDLE.getString("email.smtp");
	}

	/**
	 * @return the default FROM value used when you send an email
	 */
	public static String getFrom() {
		return EMAIL_BUNDLE.getString("email.from");
	}

	/**
	 * @return the default sender value used when you send an email
	 */
	public static String getSender() {
		return EMAIL_BUNDLE.getString("email.sender");
	}

	/**
	 * @return the header added in order to use papper boy smtp
	 */
	public static String getPapperBoyHeader() {
		return EMAIL_BUNDLE.getString("email.papperboy.header");
	}
}
