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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.constants.MyEc3PasswordConstants;
import org.myec3.socle.core.domain.dao.UserDao;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.ConnectionInfos;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.SviProfile;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.StructureService;
import org.myec3.socle.core.service.SviProfileService;
import org.myec3.socle.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.lambdaworks.crypto.SCryptUtil;

/**
 * Concrete Servicve implementation providing methods specific to {@link User}
 * objects. These methods complete or override parent methods from
 * {@link ResourceServiceImpl} services
 *
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Service("userService")
public class UserServiceImpl extends ResourceServiceImpl<User, UserDao> implements UserService {

	// Webapp constants
	/*
	 * private static final String GU_BUNDLE_NAME = "webapp"; private static final
	 * ResourceBundle GU_BUNDLE = ResourceBundle .getBundle(GU_BUNDLE_NAME);
	 */

	public static int expirationTimePassword = 365;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects ({@link AgentProfile} and {@link EmployeeProfile} )
	 */
	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link SviProfile} objects
	 */
	@Autowired
	@Qualifier("sviProfileService")
	private SviProfileService sviProfileService;

	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	@Autowired
	@Qualifier("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;

	@Autowired
	@Qualifier("structureService")
	private StructureService structureService;

	private final Log logger = LogFactory.getLog(UserServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findByUsername(String username) {
		Assert.notNull(username, "username is mandatory and cannot be null");
		return this.dao.findByUsername(username);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findByFunctionalAccountId(Long id) {
		Assert.notNull(id, "functional account id is mandatory and canot be null");
		return this.dao.findByFunctionalAccountId(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void create(User user) {

		Assert.notNull(user, "user is mandatory and canot be null");

		// Set unique name for user
		if (user.getName() != null) {
			user.setName("User " + user.getName() + Calendar.getInstance().getTimeInMillis());
		}

		// Create sviProfile for user
		SviProfile sviProfile = new SviProfile();
		this.sviProfileService.create(sviProfile);

		// Set the sviProfile to the user
		user.setSviProfile(sviProfile);

		// create user
		super.create(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generatePassword() {

		Random random = new Random(System.currentTimeMillis());
		int min = 1;
		int max = 3;

		int numbers = random.nextInt(max - min + 1) + min;
		int characters = MyEc3PasswordConstants.PASSWORD_LENGTH - numbers;

		Random rand = new Random(System.currentTimeMillis());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < characters; i++) {
			int pos = rand.nextInt(MyEc3PasswordConstants.CHARSET_CHARACTERS.length());
			sb.append(MyEc3PasswordConstants.CHARSET_CHARACTERS.charAt(pos));
		}
		for (int i = 0; i < numbers; i++) {
			int pos = rand.nextInt(MyEc3PasswordConstants.CHARSET_NUMBERS.length());
			sb.append(MyEc3PasswordConstants.CHARSET_NUMBERS.charAt(pos));
		}
		String sbString = sb.toString();
		char[] sbArray = sbString.toCharArray();
		Collections.shuffle(Arrays.asList(sbArray));

		return new String(sbArray);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateHashPassword(String password) {

		Assert.notNull(password, "password is mandatory, null value is forbidden");

		String hashword = null;

		if (MyEc3PasswordConstants.isScryptHashingAlgorithm) {
			// generate SCRYPT Hash
			try {
				hashword = SCryptUtil.scrypt(password, MyEc3PasswordConstants.SCRYPT_PARAMETER_N,
						MyEc3PasswordConstants.SCRYPT_PARAMETER_R, MyEc3PasswordConstants.SCRYPT_PARAMETER_P);
			} catch (IllegalStateException ise) {
				logger.error("Exception occured during password hash generation", ise);
			}

		} else {
			// generate SHA1 Hash
			hashword = generateSHA1HashPassword(password);
		}

		return hashword;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateSHA1HashPassword(String password) {

		String hashword = null;

		try {
			MessageDigest hashMessage = MessageDigest.getInstance(MyEc3PasswordConstants.SHA1_HASH);
			hashMessage.update(password.getBytes());
			BigInteger hash = new BigInteger(1, hashMessage.digest());
			hashword = hash.toString(MyEc3PasswordConstants.SHA1_RADIX);
			while (hashword.length() < MyEc3PasswordConstants.SHA1_HASH_LENGTH) {
				hashword = "0" + hashword;
			}
		} catch (NoSuchAlgorithmException nsae) {
			logger.error("Exception occured during password hash generation", nsae);
		}

		return hashword;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPasswordOk(String enteredPassword, String storedPassword) {
		boolean isPassworkOk = false;
		try {
			// SCRYPT
			isPassworkOk = SCryptUtil.check(enteredPassword, storedPassword);
		} catch (IllegalArgumentException iae) {
			// SHA1
			String hashedPassword = generateSHA1HashPassword(enteredPassword);
			isPassworkOk = hashedPassword.equals(storedPassword);
		} catch (Exception e) {
			// ignore
		}
		return isPassworkOk;
	}

	@Override
	public boolean isPasswordConform(String password) {

		// We can do all checks in one regex, but we'll keep it that way to be more
		// readable

		// Check if password is at least 8 char long
		Pattern p = Pattern.compile("\\S{8}\\S*");
		Matcher m = p.matcher(password);

		if (m.matches()) {
			logger.trace("isPasswordConform : password is at least 8 char long");

			// Check if password contains at least one letter
			p = Pattern.compile(".*[a-zA-Z].*");
			m = p.matcher(password);

			if (m.matches()) {
				logger.trace("isPasswordConform : contains at least one letter");

				// Check if password contains at least one figure
				p = Pattern.compile(".*\\d.*");
				m = p.matcher(password);

				if (m.matches()) {
					logger.trace("isPasswordConform : contains at least one figure");
					logger.trace("isPasswordConform : Password is conform");
					return true;
				}
			}
		}

		logger.trace("isPasswordConform : password is not conform");
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateControlKeyNewPassword() {
		String key = System.currentTimeMillis() + "," + generatePassword();
		return key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateCollections(User user) {
		Assert.notNull(user, "user is mandatory, null value is forbidden");

		user.setProfiles(profileService.findAllProfilesByUser(user));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanCollections(User user) {
		Assert.notNull(user, "user is mandatory, null value is forbidden");

		logger.info("cleaning collections of user");
		user.setProfiles(new ArrayList<Profile>());
	}

	public Date generateExpirationDatePassword() {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, expirationTimePassword);

		return cal.getTime();

	}

	public Long getConnectionInfosRelatedUser(ConnectionInfos connectionInfos) {

		Long userId = this.dao.findUserIdByConnectionInfos(connectionInfos);
		return userId;
	}

	public List<User> findUsersByCertificate(String certificate) {
		return this.dao.findUsersByCertificate(certificate);
	}

	public Structure findUserOrganismStructure(User user) {
		Assert.notNull(user, "user is mandatory, null value is forbidden");

		// get the Profile
		Profile userProfile = this.profileService.findByUserId(user.getId());

		if (userProfile != null) {
			// get the AgentProfile
			AgentProfile userAgentProfile = this.agentProfileService.findOne(userProfile.getId());

			if (userAgentProfile != null) {
				// get the OrganismDepartment
				OrganismDepartment userOrganismDepartment = this.organismDepartmentService
						.findOne(userAgentProfile.getOrganismDepartment().getId());
				// get the Organism

				if (userOrganismDepartment != null) {
					Organism userOrganism = this.organismService.findOne(userOrganismDepartment.getOrganism().getId());

					if (userOrganism != null) {
						// get the Structure
						Structure userStructure = this.structureService.findOne(userOrganism.getId());

						if (userStructure != null) {
							// return the structure
							return userStructure;
						}
					}
				}
			}
		}
		// one of the "find" returns null, we can't find structure
		return null;
	}

}
