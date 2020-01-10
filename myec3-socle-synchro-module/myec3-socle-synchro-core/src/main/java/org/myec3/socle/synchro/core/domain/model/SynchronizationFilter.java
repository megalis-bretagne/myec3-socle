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
package org.myec3.socle.synchro.core.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Structure;

/**
 * This class represents a filter used to format the XML to send at the remote
 * applications depending on {@link Resource}.<br />
 * This filter is used during the synchronization process into the differents
 * resource managers.<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
public class SynchronizationFilter implements Serializable, PE {

	private static final long serialVersionUID = 7574760089245217074L;

	private Long id;
	private Boolean allRolesDisplayed;
	private Boolean allApplicationsDisplayed;

	/**
	 * Default constructor. Do nothing.
	 */
	public SynchronizationFilter() {
	}

	/**
	 * Contructor. Define if all roles of a given {@link Profile} and if all
	 * applications of a given {@link Structure} must be displayed.
	 * 
	 * @param allRolesDisplayed        : Define if all roles of a given
	 *                                 {@link Profile} must be displayed into the
	 *                                 XML sent to the distant application.
	 * 
	 * @param allApplicationsDisplayed : Define if all applications of a given
	 *                                 {@link Structure} must be displayed into the
	 *                                 XML sent to the distant application.
	 */
	public SynchronizationFilter(Boolean allRolesDisplayed, Boolean allApplicationsDisplayed) {
		this.allRolesDisplayed = allRolesDisplayed;
		this.allApplicationsDisplayed = allApplicationsDisplayed;
	}

	/**
	 * Technical id of the SynchronizationError
	 * 
	 * @return the id for this SynchronizationError
	 */
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return true if if all roles of a given {@link Profile} must be displayed
	 *         into the XML sent to the distant application.
	 */
	@Column(nullable = false)
	public Boolean isAllRolesDisplayed() {
		if (this.allRolesDisplayed == null) {
			this.allRolesDisplayed = Boolean.FALSE;
		}
		return allRolesDisplayed;
	}

	public void setAllRolesDisplayed(Boolean allRolesDisplayed) {
		this.allRolesDisplayed = allRolesDisplayed;
	}

	/**
	 * @return true if all applications of a given {@link Structure} must be
	 *         displayed into the XML sent to the distant application.
	 */
	@Column(nullable = false)
	public Boolean isAllApplicationsDisplayed() {
		if (this.allApplicationsDisplayed == null) {
			this.allApplicationsDisplayed = Boolean.FALSE;
		}
		return allApplicationsDisplayed;
	}

	public void setAllApplicationsDisplayed(Boolean allApplicationsDisplayed) {
		this.allApplicationsDisplayed = allApplicationsDisplayed;
	}

}
