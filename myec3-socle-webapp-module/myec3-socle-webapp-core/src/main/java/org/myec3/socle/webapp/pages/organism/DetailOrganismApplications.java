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
package org.myec3.socle.webapp.pages.organism;

import java.util.List;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Page used to display the applications{@link Application} subscribed by the
 * organism{@link Organism}<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/organism/DetailOrganismApplications.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
public class DetailOrganismApplications extends AbstractPage {

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

	private Organism organism;

	private List<Application> availableApplications;

	private Application applicationLoop;

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.organism = this.organismService.findOne(id);
		if (null == this.organism) {
			return false;
		}

		if (null == this.organism.getAddress()) {
			this.organism.setAddress(new Address());
		}

		this.availableApplications = this.organism.getApplications();

		// Remove default applications that are mandatories
		List<Application> defaultApplications = this.applicationService
				.findAllDefaultApplicationsByStructureTypeAndCustomer(this.organism.getStructureType(),
						this.organism.getCustomer());
		this.availableApplications.removeAll(defaultApplications);

		return this.hasRights(this.organism);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
	}

	public Application getApplicationLoop() {
		return applicationLoop;
	}

	public void setApplicationLoop(Application applicationLoop) {
		this.applicationLoop = applicationLoop;
	}

	public List<Application> getAvailableApplications() {
		return availableApplications;
	}

	public void setAvailableApplications(List<Application> availableApplications) {
		this.availableApplications = availableApplications;
	}

	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}
}
