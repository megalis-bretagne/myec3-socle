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
package org.myec3.socle.core.domain.model.constants;

import java.util.ResourceBundle;

/**
 * Constants used by the XML adapter
 * 
 * @see ChildStructuresAdapter.class and ParentStructuresAdapter.class
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public final class MyEc3AdapterConstants {

	private static final String SOCLE_SERVER_BUNDLE_NAME = "socleCore";
	private static final ResourceBundle SOCLE_SERVER_BUNDLE = ResourceBundle
			.getBundle(SOCLE_SERVER_BUNDLE_NAME);

	/**
	 * @return the ws server's URL used to retrieve the company
	 */
	public static String getSocleServerCompanyUrl() {
		return SOCLE_SERVER_BUNDLE.getString("server.company.url");
	}

	/**
	 * @return the ws server's URL used to retrieve the organism
	 */
	public static String getSocleServerOrganismUrl() {
		return SOCLE_SERVER_BUNDLE.getString("server.organism.url");
	}
}
