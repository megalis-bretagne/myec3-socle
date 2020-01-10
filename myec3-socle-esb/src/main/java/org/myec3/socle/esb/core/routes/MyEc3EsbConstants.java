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
package org.myec3.socle.esb.core.routes;

import java.util.ResourceBundle;

/**
 * Constants for ESB application
 * 
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * 
 */
public class MyEc3EsbConstants {

	private static final String SYNC_BUNDLE_NAME = "sync";
	private static final ResourceBundle SYNC_BUNDLE = ResourceBundle
			.getBundle(SYNC_BUNDLE_NAME);

	/**
	 * @return the syncBundle
	 */
	private static ResourceBundle getSyncBundle() {
		return SYNC_BUNDLE;
	}

	/**
	 * 
	 * @return the value of jmsIn in SYNC_BUNDLE
	 */
	public static String getSyncJmsIn() {
		return getSyncBundle().getString("jmsIn");
	}

	/**
	 * 
	 * @return the value of jmsOut in SYNC_BUNDLE
	 */
	public static String getSyncJmsOut() {
		return getSyncBundle().getString("jmsOut");
	}

}
