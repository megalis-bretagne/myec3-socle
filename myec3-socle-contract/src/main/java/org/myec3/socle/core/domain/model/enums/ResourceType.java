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

import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;

/**
 * List of the different types that can be hold by a resource
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public enum ResourceType {

	/**
	 * concrete resource is an {@link AdminProfile}
	 */
	ADMIN_PROFILE("AdminProfile"),

	/**
	 * concrete resource is an {@link AgentProfile}
	 */
	AGENT_PROFILE("AgentProfile"),

	/**
	 * concrete resource is an {@link EmployeeProfile}
	 */
	EMPLOYEE_PROFILE("EmployeeProfile"),

	/**
	 * concrete resource is a {@link Company}
	 */
	COMPANY("Company"),

	/**
	 * concrete resource is a {@link Customer}
	 */
	CUSTOMER("Customer"),

	/**
	 * concrete resource is a {@link CompanyDepartment}
	 */
	COMPANY_DEPARTMENT("CompanyDepartment"),
	
	/**
	 * concrete resource is a {@link Establishment}
	 */
	ESTABLISHMENT("Establishment"),

	/**
	 * concrete resource is an {@link Organism}
	 */
	ORGANISM("Organism"),

	/**
	 * concrete resource is an {@link OrganismDepartment}
	 */
	ORGANISM_DEPARTMENT("OrganismDepartment");

	private final String label;

	private ResourceType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
