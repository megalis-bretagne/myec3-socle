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
package org.myec3.socle.core.domain.model.enums;

/**
 * Describe the model of a Role Profile
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * 
 */
public enum RoleProfile {

	ROLE_DEFAULT("Rôle par défaut"),

	ROLE_SUPER_ADMIN("Super administrateur"),
	ROLE_ADMIN("Administrateur"),

	ROLE_MANAGER_AGENT("Agent Responsable d'organisme"),
	ROLE_APPLICATION_MANAGER_AGENT("Agent Responsable de service"),
	ROLE_AGENT("Agent"),

	ROLE_EMPLOYEE("Employé"),
	ROLE_MANAGER_EMPLOYEE("Employé responsable");

	private final String label;

	/**
	 * 
	 * @param label
	 *            : label of the role profile
	 */
	private RoleProfile(String label) {
		this.label = label;
	}

	/**
	 * @return the label associated
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.name();
	}

}
