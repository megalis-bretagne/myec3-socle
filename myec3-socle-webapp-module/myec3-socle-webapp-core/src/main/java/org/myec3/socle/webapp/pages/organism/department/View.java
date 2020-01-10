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
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to display the organization chart of an
 * organism{@link Organism}.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/department/View.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
public class View extends AbstractPage {

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

	private Organism organism;

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		return Index.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation(Long id) {
		super.initUser();

		this.organism = this.organismService.findOne(id);

		if (this.organism == null) {
			return Index.class;
		}

		// Check if loggedUser can access at this page
		return this.hasRights(this.organism);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
	}

	@OnEvent(value = "delete_success")
	public void removeOrganismDepartementSuccess() {
		this.setSuccessMessage(this.getMessages().get("delete-department-success"));
	}

	@OnEvent(value = "delete_error")
	public void removeOrganismDepartementError() {
		this.setErrorMessage(this.getMessages().get("delete-department-error"));
	}

	// Getters n Setters
	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}
}