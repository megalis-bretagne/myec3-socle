/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p>
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.core.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.constants.MyEc3Constants;
import org.myec3.socle.core.domain.dao.AgentProfileDao;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.service.*;
import org.myec3.socle.core.service.exceptions.ProfileCreationException;
import org.myec3.socle.core.service.exceptions.ProfileDeleteException;
import org.myec3.socle.core.service.exceptions.ProfileUpdateException;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Concrete Servicve implementation providing specific methods to
 * {@link AgentProfile} objects. These methods complete or override parent
 * methods from {@link ProfileServiceImpl} or {@link ResourceServiceImpl}
 * services
 *
 * @author Matthieu Dubromez <matthieu.dubromez@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Service("agentProfileService")
public class AgentProfileServiceImpl extends GenericProfileServiceImpl<AgentProfile, AgentProfileDao>
		implements AgentProfileService {

	private static final Log logger = LogFactory.getLog(AgentProfileServiceImpl.class);

	/**
	 * Business service providing methods and specifics operations on
	 * {@link OrganismDepartment} objects. The concrete service implementation is
	 * injected by Spring container
	 */
	@Autowired
	@Qualifier("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	/**
	 * Business service providing methods and specifics operations on {@link User}
	 * objects. The concrete service implementation is injected by Spring container
	 */
	@Autowired
	@Qualifier("userService")
	private UserService userService;

	/**
	 * Business service providing methods and specifics operations on
	 * {@link Application} objects. The concrete service implementation is injected
	 * by Spring container
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business service providing methods and specifics operations on {@link Role}
	 * objects. The concrete service implementation is injected by Spring container
	 */
	@Autowired
	@Qualifier("roleService")
	private RoleService roleService;

	/**
	 * Business service providing methods and specifics operations on
	 * {@link ProfileType} objects. The concrete service implementation is injected
	 * by Spring container
	 */
	@Autowired
	@Qualifier("profileTypeService")
	private ProfileTypeService profileTypeService;

	/**
	 * Business service providing methods and specifics operations on
	 * {@link AgentManagedApplication} objects. The concrete service implementation
	 * is injected by Spring container
	 */
	@Autowired
	@Qualifier("agentManagedApplicationService")
	private AgentManagedApplicationService agentManagedApplicationService;

	/**
	 * Business service providing methods and specifics operations on {@link Competence}
	 * objects. The concrete service implementation is injected by Spring container
	 */
	@Autowired
	@Qualifier("competenceService")
	private CompetenceService competenceService;
	
	/**
	 * Business service providing methods and specifics operations on {@link Organism}
	 * objects. The concrete service implementation is injected by Spring container
	 */
	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AgentProfile> findAllAgentProfilesByOrganismDepartment(OrganismDepartment organismDepartment) {

		Assert.notNull(organismDepartment, "organismDepartment is mandatory. null value is forbidden");
		return this.dao.findAllAgentProfilestByOrganismDepartment(organismDepartment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AgentProfile> findAllAgentProfilesByOrganism(Organism organism) {
		Assert.notNull(organism, "organism is mandatory. null value is forbidden");
		return this.dao.findAllAgentProfilesByOrganism(organism);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void create(AgentProfile agentProfile) throws ProfileCreationException {
		Assert.notNull(agentProfile, "Cannot create agentProfile: agent cannot be null");
		Assert.isNull(agentProfile.getId(), "Cannot create agentProfile that have already be persisted (id not null)");

		try {
			// Set unique name for Agent
			if (agentProfile.getName() != null) {
				agentProfile.setName("Agent " + agentProfile.getName() + Calendar.getInstance().getTimeInMillis());
			}

			// Set Profile Type Agent
			ProfileType profileType = profileTypeService.findByValue(ProfileTypeValue.AGENT);
			agentProfile.setProfileType(profileType);

			if (agentProfile.getRoles().isEmpty()) {
				// Set at least basics roles for the agent
				List<Role> basicRoles = new ArrayList<Role>();
				List<Application> systemApplications = this.applicationService
						.findAllDefaultApplicationsByStructureTypeAndCustomer(
								agentProfile.getOrganismDepartment().getOrganism().getStructureType(),
								agentProfile.getOrganismDepartment().getOrganism().getCustomer());
				// Remove GU because that's a specific application.
				Application gu = this.applicationService.findByName(MyEc3ApplicationConstants.GU);
				systemApplications.remove(gu);

				for (Application application : systemApplications) {
					Role aRole = this.roleService
							.findBasicRoleByProfileTypeAndApplication(agentProfile.getProfileType(), application);
					if (aRole != null) {
						basicRoles.add(aRole);
					}
				}
				agentProfile.setRoles(basicRoles);
			}

			super.create(agentProfile);
			if (agentProfile.getAlfUserName() == null) {
				agentProfile.setAlfUserName(agentProfile.getId() + "@"
						+ agentProfile.getOrganismDepartment().getOrganism().getTenantIdentifier());
				agentProfile = super.update(agentProfile);
			}

		} catch (RuntimeException re) {
			throw new ProfileCreationException("Cannot create Agent " + agentProfile, re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void softDelete(Long id) throws ProfileDeleteException {
		Assert.notNull(id, "Agent's id argument cannot be null");

		// just soft delete agent
		try {
			AgentProfile agentProfile = this.findOne(id);
			// agentProfile.setSyncDelayed(agentProfile.isOperationDelayed());

			// associate agent to the root department
			OrganismDepartment departmentRoot = this.organismDepartmentService
					.findRootOrganismDepartment(agentProfile.getOrganismDepartment().getOrganism());
			agentProfile.setOrganismDepartment(departmentRoot);

			List<AgentManagedApplication> agentManagedApplications = this.agentManagedApplicationService
					.findAgentManagedApplicationsByAgent(agentProfile);

			this.agentManagedApplicationService.deleteAllByAgentProfile(agentProfile);

			// desactivate agent
			agentProfile = deactivateProfile(agentProfile);
			update(agentProfile);
		} catch (RuntimeException re) {
			throw new ProfileDeleteException("Cannot perform soft delete on Agent with id " + id, re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public AgentProfile update(AgentProfile agentProfile) throws ProfileUpdateException {
		// validate parameters
		Assert.notNull(agentProfile, "Cannot update agentProfile: agent cannot be null");

		try {
			
			// validate bean. If this is not performed here, exception is thrown
			// and ignored !
			validateResource(agentProfile);

			ProfileType profileType = this.profileTypeService.findByValue(ProfileTypeValue.AGENT);

			agentProfile.setProfileType(profileType);

			// Get the user from the database
			User foundUser = this.userService.findOne(agentProfile.getUser().getId());

			// reattach user object to the agent else the user is not updated
			// agentProfile.setSyncDelayed(agentProfile.isOperationDelayed());
			// agentProfile.getUser().setSyncDelayed(agentProfile.isSyncDelayed());
			foundUser.reattach(agentProfile.getUser());
			agentProfile.setUser(foundUser);
			
			competenceService.update(agentProfile);
			
			return super.update(agentProfile);
		} catch (RuntimeException re) {
			throw new ProfileUpdateException("Cannot update Agent " + agentProfile, re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateCollections(AgentProfile agentProfile) {
		Assert.notNull(agentProfile, "Cannot populate collections of AgentProfile : agent cannot be null");

		User clonedResource = null;
		try {
			clonedResource = (User) agentProfile.getUser().clone();
			userService.populateCollections(clonedResource);

			agentProfile.setRoles(roleService.findAllRoleByProfile(agentProfile));
		} catch (CloneNotSupportedException e) {
			logger.error("Cannot clone User. " + agentProfile.getUser(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanCollections(AgentProfile agentProfile) {
		Assert.notNull(agentProfile, "Cannot clean collections of AgentProfile : agent cannot be null");
		logger.info("cleaning collections of agentProfile");

		userService.cleanCollections(agentProfile.getUser());
		organismDepartmentService.cleanCollections(agentProfile.getOrganismDepartment());
		agentProfile.setRoles(new ArrayList<Role>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AgentProfile> findAllGuAdministratorByOrganismId(Long organismId) {
		Assert.notNull(organismId, "organismId is mandatory. null value is forbidden");

		return this.dao.findAllGuAdministratorByOrganismId(organismId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AgentProfile> findAllRepresentativesByOrganism(Organism organism) {
		Assert.notNull(organism, "organism is mandatory. null value is forbidden");

		return this.dao.findAllRepresentativesByOrganism(organism);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AgentProfile> findAllSubstitutesByOrganism(Organism organism) {
		Assert.notNull(organism, "organism is mandatory. null value is forbidden");

		return this.dao.findAllSubstitutesByOrganism(organism);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AgentProfile> findAllByListOfEmailsAndOrganism(List<String> listOfEmails, Organism organism) {
		Assert.notNull(listOfEmails, "listOfEmails cannot be null");
		Assert.notNull(organism, "organism cannot be null");
		return this.dao.findAllByListOfEmailsAndOrganism(listOfEmails, organism);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDashboard(Long id) {

		AgentProfile agentProfile = this.dao.findOne(id);

		if (agentProfile != null && agentProfile.isAgent()) {
			if (StringUtils.isEmpty(agentProfile.getDashboard())) {
				return MyEc3Constants.DEFAULT_DASHBOARD;
			}
			return agentProfile.getDashboard();
		}

		throw new WebResourceNotFoundException(id, MethodType.GET,
				"Agent profile not found");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public String updateOrCreateDashboard(Long id, String dashboard) {
		AgentProfile agentProfile = this.dao.findOne(id);
		if (agentProfile != null && agentProfile.isAgent()) {
			if (dashboard != null) {
				agentProfile.setDashboard(dashboard);
				agentProfile = this.dao.update(agentProfile);
				return agentProfile.getDashboard();
			}
		}

		throw new WebResourceNotFoundException(id, MethodType.PUT,
				"Agent profile not found");

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AgentProfile> getAnnuaire(int page, String filter, String sortBy, String sortDir, int size,
										  String competences, Boolean enableFilter) throws Exception {
		if (page < 1) {
			throw new InvalidParameterException("Page must be greater or equal than 0");
		}
		return dao.findAnnuaire(page -1, filter, sortBy, sortDir, size, competences, enableFilter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getAnnuaireCount(String filter, String competences, Boolean enableFilter) {
		return this.dao.findAnnuaireCount(filter, competences, enableFilter);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrganismDepartment getOrganismDepartmentByAgentProfileId(Long agentProfileId) {
		return this.findOne(agentProfileId).getOrganismDepartment();
	}
	
	
}
