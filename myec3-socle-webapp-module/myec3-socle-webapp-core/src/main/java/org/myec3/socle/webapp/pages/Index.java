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
package org.myec3.socle.webapp.pages;

import javax.inject.Named;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.constants.MyEc3Constants;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.EmployeeProfileService;

/**
 * Class used to display Index page.<br />
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/index.tml
 *
 * @author Loïc Frering <loic.frering@atosorigin.com>
 */
public class Index extends AbstractPage {

	@Inject
	private Block technicalAdminBlock;

	@Inject
	private Block functionalAdminBlock;

	@Inject
	private Block globalManagerAgentBlock;

	@Inject
	private Block applicationManagerAgentBlock;

	@Inject
	private Block managerAgentLimitedBlock;

	@Inject
	private Block managerEmployeeBlock;

	@Inject
	private Block agentBlock;

	@Inject
	private Block employeeBlock;

	@Inject
	private Block defaultBlock;

	@SuppressWarnings("unused")
	@Property
	private String logoutUrl = MyEc3Constants.J_SPRING_SECURITY_LOGOUT;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Inject
	@Named("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	@Inject
	@Named("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	@Property
	private AgentProfile agentProfile;

	@SuppressWarnings("unused")
	@Property
	private EmployeeProfile employeeProfile;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		super.initUser();
		return Boolean.TRUE;
	}

	/**
	 * @return the block corresponding at the logged user
	 */
	public Block getChooseBlock() {
		if (this.getLoggedProfileExists()) {
			if (super.getIsTechnicalAdmin()) {
				return this.technicalAdminBlock;
			} else if (super.getIsFunctionalAdmin()) {
				return this.functionalAdminBlock;
			} else if (super.getIsGlobalManagerAgent() || super.getIsApplicationManagerAgent()) {
				agentProfile = this.agentProfileService.findOne(this.getLoggedProfile().getId());
				// If the organism is not a member the admin must not manage
				// it
				if (!super.getIsMember(agentProfile)) {
					return this.managerAgentLimitedBlock;
				} else {
					if (super.getIsGlobalManagerAgent()) {
						return this.globalManagerAgentBlock;
					} else {
						return this.applicationManagerAgentBlock;
					}
				}

			} else if (super.getIsAgent()) {
				// TODO : load agent profile Infos
				return this.agentBlock;
			} else if (super.getIsManagerEmployee()) {
				employeeProfile = this.employeeProfileService.findOne(this.getLoggedProfile().getId());
				return this.managerEmployeeBlock;
			} else if (super.getIsEmployee()) {
				// load EmployeeProfile to create establishments
				employeeProfile = this.employeeProfileService.findOne(this.getLoggedProfile().getId());
				return this.employeeBlock;
			}
		}
		return defaultBlock;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String message) {
		this.errorMessage = message;
	}
}
