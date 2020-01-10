/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p>
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.core.constants;

/**
 * Constants used to managed some values in myec3 socle
 *
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public final class MyEc3Constants {

	// Default acronym used for companies
	public static final String ACRONYM_ENT = "ENT";

	// Default root deparment name for structures (company or organism)
	public static final String ROOT_DEPARTMENT_NAME = "root";

	// Default root deparment label for structures (company or organism)
	public static final String ROOT_DEPARTMENT_LABEL = "Niveau racine";

	// Default postfix for head office establishment name
	public static final String HEAD_OFFICE_ESTABLISHMENT_NAME_POSTFIX = " HO";

	// Default postfix for head office establishment label
	public static final String HEAD_OFFICE_ESTABLISHMENT_LABEL_POSTFIX = " - Siège social";

	// opensso default logout URL
	public static final String J_SPRING_SECURITY_LOGOUT = "/j_spring_security_logout";

	// Default agent dashboard
	public static final String DEFAULT_DASHBOARD = "[[1],[6], [5, 7]]";

}
