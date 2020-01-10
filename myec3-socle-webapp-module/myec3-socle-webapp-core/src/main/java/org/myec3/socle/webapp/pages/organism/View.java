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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.EmailService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to display a success message at the end of organism creation
 * process {@link Organism}<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/View.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public class View extends AbstractPage {

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Inject
	@Named("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service providing methods and specifics operations on Email objects
	 */
	@Inject
	@Named("emailService")
	private EmailService emailService;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	private Organism organism;

	private AgentProfile agentProfile;

	// Activate n Passivate
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long idOrganism, Long idAgentProfile) {
		this.organism = this.organismService.findOne(idOrganism);

		if (null == this.organism) {
			return Boolean.FALSE;
		}

		this.agentProfile = this.agentProfileService.findOne(idAgentProfile);

		if (null == this.agentProfile) {
			return Boolean.FALSE;
		}

		// Check if loggedUser can access to this organism
		return this.hasRights(this.organism);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Object onPassivate() {
		List<Long> result = new ArrayList<Long>();

		if (this.organism != null) {
			result.add(this.organism.getId());
		}
		if (this.agentProfile != null) {
			result.add(this.agentProfile.getId());
		}
		return result;
	}

	// Getters n Setters
	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}

	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	public Boolean getIsAuthorizedMail() {
		return this.emailService.authorizedToSendMail(this.organism);
	}

	public AgentProfile getAgentProfile() {
		return agentProfile;
	}

	public void setAgentProfile(AgentProfile agentProfile) {
		this.agentProfile = agentProfile;
	}
}
