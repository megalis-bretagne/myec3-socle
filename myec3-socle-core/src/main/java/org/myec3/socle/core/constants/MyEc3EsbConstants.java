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

import java.util.ResourceBundle;

/**
 * Constants class used for the connection between an application and the ESB (Enterprise Service Bus)
 * 
 * @see SchedulerServiceImpl.class
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public final class MyEc3EsbConstants {

	private static final String ESB_BUNDLE_NAME = "socleCore";
	private static final ResourceBundle ESB_BUNDLE = ResourceBundle
			.getBundle(ESB_BUNDLE_NAME);
	public static final int MESSAGE_LIFESPAN = 1800000;

	/**
	 * @return the entry point URL of the activeMQ contained in ESB
	 */
	public static String getInUrl() {
		return ESB_BUNDLE.getString("esb.in.url");
	}

	/**
	 * @return the username of activeMQ entry point
	 */
	public static String getInUsername() {
		return ESB_BUNDLE.getString("esb.in.username");
	}

	/**
	 * @return the password of activeMQ entry point
	 */
	public static String getInPassword() {
		return ESB_BUNDLE.getString("esb.in.password");
	}

	/**
	 * @return the current application's name sendind the JMS to activeMQ entry
	 *         point
	 */
	public static String getApplicationSendingJmsName() {
		return ESB_BUNDLE.getString("esb.application.sending.name");
	}

	/**
	 * @return the number of maximum attempts allowed to send the JMS to the
	 *         activeMQ entry point
	 */
	public static int getMaxAttempts() {
		return Integer.valueOf(ESB_BUNDLE.getString("quartz.maxAttempts"))
				.intValue();
	}

	/**
	 * @return the delay used between two attemps for sending the JMS to the
	 *         activeMQ entry point
	 */
	public static Long getQuartzResynchronizationDelay() {
		return Long.valueOf(ESB_BUNDLE
				.getString("quartz.resynchronization.delay"));
	}

	/**
	 * @return the delay before lunch the job sending the JMS to the activeMQ
	 *         entry point
	 */
	public static Long getQuartzSynchronizationDelay() {
		return Long.valueOf(ESB_BUNDLE
				.getString("quartz.synchronization.delay"));
	}
}
