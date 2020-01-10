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

import java.text.SimpleDateFormat;
import java.util.*;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.CollegeCategorie;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismStatus;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to display organism's informations {@link Organism}<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/DetailOrganism.tml<br
 * />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
public class DetailOrganism extends AbstractPage {

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

	private Organism organism;

	// Only used in Tapestry
	@Property
	private CollegeCategorie collegeCategorie;

	@Property
	private Map<String, OrganismStatus> organismStatusList;
	
	@Property
	private String currentKeyOrganismStatus;

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.organism = this.organismService.findOne(id);

		organismStatusList = new HashMap<String, OrganismStatus>();
		for (OrganismStatus organismStatus : this.organism.getOrganismStatus()) {
			organismStatusList.put(this.getMessages().get("OrganismMemberStatus." + organismStatus.getStatus()), organismStatus);
		}
		organismStatusList.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(
						(o1, o2) -> o1.getDate().compareTo(o2.getDate()) 
				));

		if (null == this.organism) {
			return Index.class;
		}

		// Check if loggedUser can access at this organism details
		return this.hasRights(this.organism);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
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

	public String getLegalCategoryLabel() {
		return this.getMessages().get(this.organism.getLegalCategory().name());
	}

	public String getApeCodeLabel() {
		return this.getMessages().get(this.organism.getApeCode().name());
	}

	public SimpleDateFormat getDateFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat;
	}

	public void setupRender() {
		for (String key : this.getMessages().getKeys()) {
			if (key.contains("OrganismCollegeCat") && key.split("\\.")[1].equals(this.organism.getCollege())) {
				String keyValue = key.split("\\.")[1];
				this.collegeCategorie = new CollegeCategorie(keyValue, this.getMessages().get(key));
			}
		}
	}

	public OrganismStatus getCurrentValue() {
		return this.organismStatusList.get(this.currentKeyOrganismStatus);
	}
	
}
