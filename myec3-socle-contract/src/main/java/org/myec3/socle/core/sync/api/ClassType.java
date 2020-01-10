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
package org.myec3.socle.core.sync.api;

import javax.xml.bind.annotation.XmlEnum;

/**
 * An enumeration of class type contained into MyEc3 model
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
@XmlEnum
public enum ClassType {

	ADMIN_PROFILE("AdminProfile"), 
	AGENT_PROFILE("AgentProfile"), 
	EMPLOYEE_PROFILE("EmployeeProfile"), 
	ORGANISM("Organism"), 
	ORGANISM_DEPARTMENT("OrganismDepartment"), 
	COMPANY("Company"),
	COMPANY_DEPARTMENT("CompanyDepartment"),
	ESTABLISHMENT("Establishment"), 
	CUSTOMER("customer");

	private final String label;

	private ClassType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
