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
package org.myec3.socle.ws.server.validator.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.core.sync.api.ClassType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestSyntaxException;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.myec3.socle.core.sync.api.exception.WebResourceRelationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * 
 * Implementation of the validator that make a validation on
 * {@link AgentProfile} during REST requests.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("agentProfileValidator")
public class AgentProfileValidatorImpl extends ResourceValidatorManagerImpl<AgentProfile> {

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Autowired
	@Qualifier("roleService")
	private RoleService roleService;

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Autowired
	@Qualifier("userService")
	private UserService userService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link OrganismDepartment} objects
	 */
	@Autowired
	@Qualifier("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	// Properties for JWT Key
	private static final String SOCLE_CORE_BUNDLE_NAME = "socleCore";
	private static final ResourceBundle SOCLECORE_BUNDLE = ResourceBundle.getBundle(SOCLE_CORE_BUNDLE_NAME);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateGet(Long resourceId) {
		// Check param
		Assert.notNull(resourceId, "AgentProfile's id can't be null");
		logger.debug("[validateGet] of agent ");

		// Check if an agent with this id exists into the database
		if (!this.validateResourceExists(resourceId)) {
			throw new WebResourceNotFoundException(resourceId, MethodType.GET,
					"No agent exist with this id : " + resourceId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateUpdate(AgentProfile resource, Long resourceId, AgentProfile foundAgentProfile) {
		// Check params
		Assert.notNull(resource, "AgentProfile can't be null");
		Assert.notNull(resourceId, "AgentProfile's id can't be null");
		Assert.notNull(foundAgentProfile, "Found agentProfile can't be null");
		logger.debug("[validateUpdate] of agentProfile with name = " + resource.getName());

		// Validate bean content
		this.validateBeanContent(resource, MethodType.PUT);

		// Check that resource's id is equal at the resourceId
		logger.debug("Check that resource's id is equal at the resourceId");
		if (!(resource.getId().equals(resourceId))) {
			throw new WebRequestSyntaxException(resource, MethodType.PUT,
					"Id sent not corresponding at the agentProfile's id sent : " + resource.getId(), "externalId",
					resource.getExternalId().toString());
		}

		// Check agent's roles
		this.validAgentRoles(resource, MethodType.PUT);

		// We check that the user of the agent exists
		logger.debug("Checking that the user of the agent exists");
		User foundUser = userService.findOne(resource.getUser().getId());
		if (foundUser == null) {
			throw new WebResourceNotFoundException(resource, MethodType.PUT,
					"No user exists with this Id : " + resource.getUser().getId(), "externalId",
					resource.getExternalId().toString());
		}

		// If email have been modified we check that the agent's new email
		// not already exists into the database
		// (table agentProfile only)
		logger.debug("Checking agent's email not already exist");
		if (!(resource.getEmail().equals(foundAgentProfile.getEmail()))) {
			// we check that the agent's new email not already exists into
			// the database
			this.validateEmailUniqueConstraints(resource, resource.getEmail(), MethodType.PUT);
		}

		// Check that the organismDepartment of the agent sent exists
		this.validAgentOrganismDepartment(resource, MethodType.PUT);

		// Check that the organism of the agent sent exists
		this.validAgentOrganism(resource, MethodType.PUT);

		// If the agent has an parentDepartment we must check if it exists
		// into the database
		this.validAgentParentOrganismDepartment(resource, MethodType.PUT);

		// Check if the password is correct
		logger.debug("Checking if agent's password is correct");
		if (resource.getUser().getNewPassword() != null) {

			String enteredOldPassword = resource.getUser().getPassword();
			String storedPassword = foundUser.getPassword();

			if (!userService.isPasswordOk(enteredOldPassword, storedPassword)) {
				// the value of old password is not correct
				throw new WebRequestSyntaxException(resource, MethodType.PUT,
						"Agent's old password is not correct- agent's id : " + resource.getId(), "externalId",
						resource.getExternalId().toString());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateUpdateCreation(AgentProfile resource) {
		// Check param
		Assert.notNull(resource, "AgentProfile can't be null");
		logger.debug("[validateUpdateCreation] of agentProfile with name = " + resource.getName());

		// Validate bean content
		this.validateBeanContent(resource, MethodType.PUT);

		// Check agent's roles
		this.validAgentRoles(resource, MethodType.PUT);

		// Check that the organismDepartment of the agent sent exists
		this.validAgentOrganismDepartment(resource, MethodType.PUT);

		// we check that the agent's email not already exists into
		// the database
		this.validateEmailUniqueConstraints(resource, resource.getEmail(), MethodType.PUT);

		// We check if the userName not already exists
		this.validateUsernameUniqueContraints(resource, resource.getUser().getUsername(), MethodType.PUT);

		// Check that the organism of the agent sent exists
		this.validAgentOrganism(resource, MethodType.PUT);

		// If the agent has an parentDepartment we must check if it exists
		// into the database
		this.validAgentParentOrganismDepartment(resource, MethodType.PUT);

		// Check if password is not null
		logger.debug("Checking if agent's password is not null");
		if (resource.getUser().getPassword() == null) {
			// the value of old password is not correct
			throw new WebRequestSyntaxException(resource.getId(), MethodType.PUT, "Agent's password cannot be null");
		}

		// Prepare resource before creation
		this.prepareAgentProfileUpdateCreation(resource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateResourceCollections(AgentProfile resource) {
		// Check param
		Assert.notNull(resource, "AgentProfile can't be null");
		logger.debug("[populateResourceCollections] of agent : " + resource.getName());

		// populate agentProfile's roles
		this.agentProfileService.populateCollections(resource);
	}

	/**
	 * This method allows to prepare the agent profile before an update creation
	 * (PUT transforming in POST)
	 * 
	 * @param agentProfile : agentProfile to validate
	 */
	public void prepareAgentProfileUpdateCreation(AgentProfile agentProfile) {
		// Check param
		Assert.notNull(agentProfile, "agentProfile cannot be null");
		logger.debug("[prepareAgentProfileUpdateCreation] of agent : " + agentProfile.getName());

		// Set agentProfile Id to null before creation
		agentProfile.setId(null);

		// Prepare user of agentProfile
		agentProfile.getUser().setId(null);
		if (agentProfile.getUser().getSviProfile() != null) {
			agentProfile.getUser().getSviProfile().setId(null);
		}

		// Create the user of the agentProfile
		userService.create(agentProfile.getUser());
	}

	/**
	 * This method allows to validate agent's roles
	 * 
	 * @param resource   : agentProfile to validate
	 * @param methodType : method type (GET, PUT, POST, DELETE)
	 */
	public void validAgentRoles(AgentProfile resource, MethodType methodType) {
		// Check params
		Assert.notNull(resource, "AgentProfile can't be null");
		Assert.notNull(methodType, "MethodType can't be null");
		logger.debug("[validAgentRoles] of agent : " + resource.getName());

		// Check that the agent have at least one role
		// get the list of roles of the agent
		logger.debug("Checking that the agent have at least one role get the list of roles of the agent");
		List<Role> listRoles = resource.getRoles();
		if (listRoles.size() < 1) {
			throw new WebRequestSyntaxException(resource.getId(), methodType, "Agent must have at least one role");
		}

		// We check that all the roles of the agent sent exist
		logger.debug("Checking that all the roles of the agent sent exist");
		for (Role role : listRoles) {
			Role foundRole = roleService.findOne(role.getId());
			if (foundRole == null) {
				throw new WebResourceNotFoundException(role.getId(), methodType,
						"No role exists with this Id : " + role.getId());
			}
		}
	}

	/**
	 * This method allows to validate agent's organism
	 * 
	 * @param resource   : agentProfile to validate
	 * @param methodType : method type (GET, PUT, POST, DELETE)
	 */
	public void validAgentOrganism(AgentProfile resource, MethodType methodType) {
		// Check params
		Assert.notNull(resource, "AgentProfile can't be null");
		Assert.notNull(methodType, "MethodType can't be null");
		logger.debug("[validAgentOrganism] of agent : " + resource.getName());

		// Check that the organism of the agent sent exists
		logger.debug("Checking agent's organism exist");
		if (organismService.findOne(resource.getOrganismDepartment().getOrganism().getId()) == null) {
			// No Organism exists for this agent
			logger.error("[validAgentOrganism] of agent"
					+ " throw an WebResourceRelationNotFoundException : No organism exists with id : "
					+ resource.getOrganismDepartment().getOrganism().getId());

			throw new WebResourceRelationNotFoundException(resource.getOrganismDepartment().getOrganism().getId(),
					methodType,
					"No organism exists with this Id : " + resource.getOrganismDepartment().getOrganism().getId(),
					ClassType.ORGANISM);
		}
	}

	/**
	 * This method allows to validate agent's organismDepartment
	 * 
	 * @param resource   : agentProfile to validate
	 * @param methodType : method type (GET, PUT, POST, DELETE)
	 */
	public void validAgentOrganismDepartment(AgentProfile resource, MethodType methodType) {
		// Check params
		Assert.notNull(resource, "AgentProfile can't be null");
		Assert.notNull(methodType, "MethodType can't be null");
		logger.debug("[validAgentOrganismDepartment] of agent : " + resource.getName());

		// Check that the organismDepartment of the agent sent exists
		logger.debug("Checking agent's organismDepartment exist");
		if (organismDepartmentService.findOne(resource.getOrganismDepartment().getId()) == null) {
			// No Organism Department exists for this agent
			logger.error("[validAgentOrganismDepartment] of agent"
					+ " throw an ResourceRelationNotFoundException : No organism department exists with id : "
					+ resource.getOrganismDepartment().getOrganism());

			throw new WebResourceRelationNotFoundException(resource, resource.getOrganismDepartment().getId(),
					methodType,
					"No organism department exists with this Id : " + resource.getOrganismDepartment().getId(),
					"externalId", resource.getExternalId().toString(), ClassType.ORGANISM_DEPARTMENT);
		}
	}

	/**
	 * This method allows to validate agent's parent department
	 * 
	 * @param resource   : the agentProfile to validate
	 * @param methodType : method type (GET, PUT, POST, DELETE)
	 */
	public void validAgentParentOrganismDepartment(AgentProfile resource, MethodType methodType) {
		// Check params
		Assert.notNull(resource, "[validAgentParentOrganismDepartment] AgentProfile can't be null");
		Assert.notNull(methodType, "[validAgentParentOrganismDepartment] MethodType can't be null");
		logger.debug("[validAgentParentOrganismDepartment] of agent : " + resource.getName());

		// If the agent has an parentDepartment we must check if it exists
		// into the database
		logger.debug("Checking agent's parentDepartment exist");
		if (resource.getOrganismDepartment().getParentDepartment() != null) {
			// Check if parent department exists into the database
			if (this.organismDepartmentService
					.findOne(resource.getOrganismDepartment().getParentDepartment().getId()) == null) {
				throw new WebResourceRelationNotFoundException(
						resource.getOrganismDepartment().getParentDepartment().getId(), methodType,
						"No parent department exists with this Id : "
								+ resource.getOrganismDepartment().getParentDepartment().getId(),
						ClassType.ORGANISM_DEPARTMENT);
			}

			// check if the organism contained into the parent department exists
			// into the database
			if (this.organismService
					.findOne(resource.getOrganismDepartment().getParentDepartment().getOrganism().getId()) == null) {
				throw new WebResourceRelationNotFoundException(
						resource.getOrganismDepartment().getParentDepartment().getOrganism().getId(), methodType,
						"No parent department's organism exists with this Id : "
								+ resource.getOrganismDepartment().getParentDepartment().getOrganism().getId(),
						ClassType.ORGANISM);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareResource(AgentProfile resource, MethodType methodType) {
		// Check params
		Assert.notNull(resource, "[prepareResource] AgentProfile can't be null");
		Assert.notNull(methodType, "[prepareResource] MethodType can't be null");
		logger.debug("[prepareResource] of agent : " + resource.getName());

		// We check if the password have been updated
		if (resource.getUser().getNewPassword() != null) {
			// We set the new password into the old password
			resource.getUser().setPassword(resource.getUser().getNewPassword());
		}
	}

	/**
	 * This method allows to check if the agentProfile already exists into the
	 * database by using the given UID
	 * 
	 * @param agentId : the agentProfile's id
	 * @return true if an agentProfile exists with this id
	 */
	@Override
	public Boolean validateResourceExists(Long agentId) {
		// Check param
		Assert.notNull(agentId, "[validateResourceExists] AgentProfile's id can't be null");
		logger.debug("[validateResourceExists] of agent ");

		// Check if an agent exists with this id
		AgentProfile foundAgent = agentProfileService.findOne(agentId);
		if (foundAgent != null) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void filterResource(AgentProfile resource, MethodType methodType) {
		filterResource(resource, methodType, Boolean.FALSE);
	}

	/**
	 * Unused method
	 */
	@Override
	public void validateCreate(AgentProfile resource) {
		// NOTHING TODO
	}

	/**
	 * Unused method
	 */
	@Override
	public void validateDelete(Long resourceId) {
		// NOTHING TODO
	}

	public void filterResource(AgentProfile resource, MethodType methodType, Boolean withHiddenRoles) {
		Assert.notNull(resource, "[filterResource] resource can't be null");
		Assert.notNull(methodType, "[filterResource] methodType can't be null");

		// In all cases we set password and controlKeyNewPassword of user to
		// null
		resource.getUser().setPassword(null);
		resource.getUser().setControlKeyNewPassword(null);

		if (methodType.equals(MethodType.GET)) {
			// We send only current department of agent with parent department.
			// We
			// don't transmit the entire tree
			if (!resource.getOrganismDepartment().isRootDepartment()) {
				resource.getOrganismDepartment().getParentDepartment().setParentDepartment(null);
				resource.getOrganismDepartment().setRootDepartment(Boolean.FALSE);

				// We don't send the list of applications of parentDepartment
				resource.getOrganismDepartment().getParentDepartment().getOrganism()
						.setApplications(new ArrayList<Application>());
			}

			// We don't send the list of applications of organism department
			resource.getOrganismDepartment().getOrganism().setApplications(new ArrayList<Application>());

			// We don't send organism's structures
			resource.getOrganismDepartment().getOrganism().setChildStructures(new ArrayList<Structure>());
			resource.getOrganismDepartment().getOrganism().setParentStructures(new ArrayList<Structure>());

			if (withHiddenRoles == null || !withHiddenRoles) {
				// We don't send the hidden roles
				resource.setRoles(resource.getRolesWithoutHidden());
			}

			// We create the JWT for application Super Chef Outils (name = "super chef -
			// outil - RGPD")
			Application appSuperChefOutils = applicationService.findByName("super chef - outil - RGPD");
			if (appSuperChefOutils != null) {
				resource.getRoles().stream()
						.filter(role -> appSuperChefOutils.getUrl().equals(role.getApplication().getUrl())).findFirst()
						.ifPresent((Role role) -> addJWTToURL(role, getAgentJWT(resource)));
			}

		}
	}

	private static void addJWTToURL(Role role, String jwt) {
		role.getApplication().setUrl(role.getApplication().getUrl() + jwt);
	}

	private String getAgentJWT(AgentProfile resource) {
		Algorithm algorithm = Algorithm.HMAC256(SOCLECORE_BUNDLE.getString("JWTSecretKey"));

		return JWT.create().withClaim("organismId", resource.getOrganismDepartment().getOrganism().getId())
				.withClaim("organismLabel", resource.getOrganismDepartment().getOrganism().getLabel())
				.withClaim("profileId", resource.getId()).withClaim("profileEmail", resource.getEmail())
				.sign(algorithm);
	}

}
