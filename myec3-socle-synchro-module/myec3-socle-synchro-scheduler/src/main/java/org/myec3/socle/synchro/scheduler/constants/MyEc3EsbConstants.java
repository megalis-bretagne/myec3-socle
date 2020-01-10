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
package org.myec3.socle.synchro.scheduler.constants;

import java.util.ResourceBundle;

/**
 * Class of constants used for connection to the ESB in order to retrieve JMS
 * messages which are contained into the JMS queue OUT.
 * 
 * Your propertie file must be named "synchronization.properties"
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public final class MyEc3EsbConstants {
	// ESB
	private static final String ESB_BUNDLE_NAME = "synchronization";
	private static final ResourceBundle ESB_BUNDLE = ResourceBundle
			.getBundle(ESB_BUNDLE_NAME);

	public static String getOutUrl() {
		return ESB_BUNDLE.getString("esb.out.url");
	}

	public static String getOutUsername() {
		return ESB_BUNDLE.getString("esb.out.username");
	}

	public static String getInUsername() {
		return ESB_BUNDLE.getString("esb.in.username");
	}

	public static String getOutPassword() {
		return ESB_BUNDLE.getString("esb.out.password");
	}
}
