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
package org.myec3.socle.webapp.pages.organism.department;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Page used to display details of an organism department
 * {@link OrganismDepartment}.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/organism/department/DetailDepartment.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
public class DetailDepartment extends AbstractPage {

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link OrganismDepartment} objects
	 */
	@Inject
	@Named("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	private OrganismDepartment organismDepartment;

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return DetailDepartment.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.organismDepartment = this.organismDepartmentService.findOne(id);

		if (null == this.organismDepartment) {
			return Boolean.FALSE;
		}

		// Check if loggedUser can access at this page
		return this.hasRights(this.organismDepartment.getOrganism());
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organismDepartment != null) ? this.organismDepartment.getId() : null;
	}

	// Getters n Setters
	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}

	public OrganismDepartment getOrganismDepartment() {
		return organismDepartment;
	}

	public void setOrganismDepartment(OrganismDepartment organismDepartment) {
		this.organismDepartment = organismDepartment;
	}
}
