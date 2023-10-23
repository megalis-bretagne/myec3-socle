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
package org.myec3.socle.core.service.impl;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.domain.dao.ProjectAccountDao;
import org.myec3.socle.core.domain.model.ProjectAccount;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.ProjectAccountService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.core.service.exceptions.FunctionalAccountCreationException;
import org.myec3.socle.core.service.exceptions.FunctionalAccountDeleteException;
import org.myec3.socle.core.service.exceptions.FunctionalAccountUpdateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link ProjectAccount} objects. These methods complete or override parent
 * methods from {@link Resource} services
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Service("projectAccountService")
public class ProjectAccountServiceImpl extends ResourceServiceImpl<ProjectAccount, ProjectAccountDao>
		implements ProjectAccountService {

	private static final Log logger = LogFactory.getLog(ProjectAccountServiceImpl.class);

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Autowired
	@Qualifier("userService")
	private UserService userService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectAccount findByEmail(String email) {
		Assert.notNull(email, "email is mandatory. null value is forbidden");
		return this.dao.findByEmail(email);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectAccount> findAllByEmail(String email) {
		Assert.notNull(email, "email is mandatory. null value is forbidden");
		return this.dao.findAllByEmail(email);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectAccount findByLogin(String login) {
		Assert.notNull(login, "login is mandatory. null value is forbidden");
		return this.dao.findByLogin(login);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void create(ProjectAccount projectAccount) throws FunctionalAccountCreationException {
		try {
			Assert.notNull(projectAccount, "Cannot create project account : projectAccount cannot be null");
			Assert.isNull(projectAccount.getId(),
					"Cannot project account that have already be persisted (id not null)");

			logger.info("creating new projectAccount with name : " + projectAccount.getName());

			// create User for sso authentification
			User user = new User();
			user.setFirstname("projectAccount" + projectAccount.getName());
			user.setLastname("projectAccount" + projectAccount.getName());
			user.setName(projectAccount.getName());
			user.setUsername(projectAccount.getLogin());
			user.setEnabled(Boolean.TRUE);

			userService.create(user);

			// set user to project account
			projectAccount.setUser(user);

			// Set unique name for project account
			if (projectAccount.getName() != null) {
				projectAccount.setName(
						"ProjectAccount " + projectAccount.getName() + Calendar.getInstance().getTimeInMillis());
			}

			// Create projectAccount
			super.create(projectAccount);
		} catch (RuntimeException re) {
			throw new FunctionalAccountCreationException("Cannot create Project Account " + projectAccount, re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public ProjectAccount update(ProjectAccount projectAccount) throws FunctionalAccountUpdateException {
		try {
			Assert.notNull(projectAccount, "projectAccount is mandatory. null value is forbiden");
			Assert.notNull(projectAccount.getId(), "Updating a detached instance that never be persisted");

			logger.info("update projectAccount with id = " + projectAccount.getId());
			// Get project account's user from the database because is not
			// sending in the xml file
			User foundUser = userService.findByFunctionalAccountId(projectAccount.getId());
			if (foundUser != null) {
				// Update the user
				foundUser.setUsername(projectAccount.getLogin());
				foundUser.setEnabled(projectAccount.getEnabled());
				userService.update(foundUser);

				projectAccount.setUser(foundUser);
				return super.update(projectAccount);
				// return resourceDao.merge(projectAccount);
			}

			logger.error("foundUser of project account id : " + projectAccount.getId() + ", externalId : "
					+ projectAccount.getExternalId() + " returned is null");
		} catch (RuntimeException re) {
			logger.error("Error during updating project account with id : " + projectAccount.getId());
			throw new FunctionalAccountUpdateException("Cannot update Project Account " + projectAccount, re);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void softDelete(Long id) throws FunctionalAccountDeleteException {
		Assert.notNull(id, "id is mandatory.null value is forbidden");
		// just soft delete agent
		try {
			logger.info("deleting projectAccount with id : " + id);
			ProjectAccount projectAccount = this.findOne(id);

			projectAccount.getUser().setEnabled(Boolean.FALSE);
			userService.update(projectAccount.getUser());

			projectAccount.setEnabled(Boolean.FALSE);
			super.update(projectAccount);
		} catch (RuntimeException re) {
			throw new FunctionalAccountDeleteException("Cannot perform soft Delete on Project Account with id " + id,
					re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void softDeleteByExternalId(Long externalId) throws FunctionalAccountDeleteException {
		Assert.notNull(externalId, "externalId is mandatory.null value is forbidden");
		// just soft delete agent
		try {
			logger.info("deleting projectAccount with externalId : " + externalId);
			ProjectAccount projectAccount = this.findByExternalId(externalId);

			projectAccount.getUser().setEnabled(Boolean.FALSE);
			userService.update(projectAccount.getUser());

			projectAccount.setEnabled(Boolean.FALSE);
			super.update(projectAccount);
		} catch (RuntimeException re) {
			throw new FunctionalAccountDeleteException(
					"Cannot perform soft Delete on Project Account with externalId " + externalId, re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectAccount> findAllEnabledByEmailOrUsername(String emailOrUsername) {
		return this.dao.findAllEnabledByEmailOrUsername(emailOrUsername);
	}
}
