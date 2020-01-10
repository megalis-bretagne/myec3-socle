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
import java.util.Date;
import java.util.List;

import org.myec3.socle.core.domain.dao.GenericProfileDao;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.GenericProfileService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.core.service.exceptions.ProfileDeleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * This abstract implementation service provides commons methods that could be
 * used on {@link Profile} child classes
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T> concrete profile child class
 * @param <D> concrete profile child DAO
 */
public abstract class GenericProfileServiceImpl<T extends Profile, D extends GenericProfileDao<T>>
		extends ResourceServiceImpl<T, D> implements GenericProfileService<T> {

	/**
	 * Prefix that will be added to the soft delete profile email
	 */
	public static final String SOFT_DELETE_SUFFIX = "@archive.com";

	/**
	 * Suffix that will be added to the soft delete profile email
	 */
	public static final String SOFT_DELETE_PREFIX = "erased-";

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects. The concrete service implementation is injected by Spring container
	 */
	@Autowired
	private UserService userService;

	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public String getSoftDeletePrefix() {
		return SOFT_DELETE_PREFIX;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public String getSoftDeleteSuffix() {
		return SOFT_DELETE_SUFFIX;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void softDelete(T profile) throws ProfileDeleteException {
		Assert.notNull(profile, "null value is forbidden");
		Assert.notNull(profile, "Removing a detached instance that never be persisted " + profile.toString());

		this.softDelete(profile.getId());
	}

	/**
	 * Perform operations in order to desactivate profile : change mail address and
	 * disable {@l ofi e}
	 * 
	 * @param profile {@li sactivate
	 * @return the updated {@link Profile}
	 */
	protected final T deactivateProfile(T profile) {
		Assert.notNull(profile, "null value is forbidden");
		String erased = this.getSoftDeletePrefix() + Calendar.getInstance().getTimeInMillis()
				+ this.getSoftDeleteSuffix();
		profile.setEmail(erased);
		profile.getUser().setUsername(erased);
		profile.getUser().setEnabled(Boolean.FALSE);
		// profile.getUser().setSyncDelayed(profile.isSyncDelayed());
		profile.setEnabled(Boolean.FALSE);
		return profile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findAllByEmail(String email) {
		Assert.notNull(email, "email is mandatory. null value is forbidden.");

		return this.dao.findAllByEmail(email);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean emailAlreadyExists(String email, T profile) {

		Assert.notNull(email, "email is mandatory. null value is forbidden.");
		Assert.notNull(profile, "profile is mandatory. null value is forbidden.");

		List<T> profilesUsingSameEmail = this.findAllByEmail(email);
		for (T profileItem : profilesUsingSameEmail) {
			// if the only profile holding the email is the same profile than
			// the actual profile : OK
			if (!profileItem.getId().equals(profile.getId())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean usernameAlreadyExists(String username, T profile) {
		Assert.notNull(username, "username is mandatory. null value is forbidden.");
		Assert.notNull(profile, "profile is mandatory. null value is forbidden.");

		User foundUser = this.userService.findByUsername(username);

		// In case of creation of new employee
		if (profile.getId() == null && foundUser == null) {
			return Boolean.FALSE;
		}

		// If it's an update
		// Case of username has changed
		if (profile.getId() != null && foundUser == null) {
			return Boolean.FALSE;
		}

		// Case of username is equal
		if (profile.getId() != null && foundUser != null) {
			// We check if its not profile's username
			if (profile.getUser().getId().equals(foundUser.getId())) {
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findAllProfilesByUser(User user) {
		// validate parameters
		Assert.notNull(user, "user is mandatory. null value is forbidden.");

		return this.dao.findAllProfilesByUser(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findAllProfilesByRole(Role role) {
		// validate parameters
		Assert.notNull(role, "role is mandatory. null value is forbidden.");

		return this.dao.findAllProfilesByRole(role);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T findByEmail(String email) {
		// validate parameters
		Assert.notNull(email, "email is mandatory. null value is forbidden.");

		return this.dao.findByEmail(email);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findAllByCriteria(String email, String username, String firstname, String lastname,
			Long sviProfile) {

		return this.dao.findAllByCriteria(email, username, firstname, lastname, sviProfile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findAllProfileEnabledByEmailOrUsername(String emailOrUsername) {
		Assert.notNull(emailOrUsername, "email or username is mandatory. null value is forbidden.");

		return this.dao.findAllProfileEnabledByEmailOrUsername(emailOrUsername);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findAllProfilesByListOfId(List<Long> listOfIds) {
		Assert.notNull(listOfIds, " List of profile's id is mandatory. null value is forbidden.");

		return this.dao.findAllProfilesByListOfId(listOfIds);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findAllProfileEnabledByExpirationDatePassword(Date expirationDatePassword) {
		// validate parameters
		Assert.notNull(expirationDatePassword, "expiration dat word is mandatory. null value is forbidden.");

		return this.dao.findAllProfileEnabledByExpirationDatePassword(expirationDatePassword);
	}
}
