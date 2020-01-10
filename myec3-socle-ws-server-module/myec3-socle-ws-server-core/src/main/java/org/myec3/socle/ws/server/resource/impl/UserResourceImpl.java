package org.myec3.socle.ws.server.resource.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.constants.MyEc3EsbConstants;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.synchro.api.SynchronizationConfiguration;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.synchro.api.SynchronizationService;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.ws.server.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Scope("prototype")
public class UserResourceImpl implements UserResource {

	private static final Logger logger = LogManager.getLogger(UserResourceImpl.class);

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Autowired
	@Qualifier("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationNotificationService;

	@Autowired
	@Qualifier("synchronizationCoreService")
	private SynchronizationService synchronizationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity updatePassword(Long userId, User user) {

		Assert.notNull(user, "User cannot be null");
		logger.debug("Changing password for user with id : " + user.getId());

		// Check if known agent
		User foundUser = userService.findOne(user.getId());
		if (foundUser == null) {
			logger.debug("User not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(user);
		}

		// Controlling given password
		if (!userService.isPasswordOk(user.getPassword(), foundUser.getPassword())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		if (!userService.isPasswordConform(user.getNewPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		// SCrypt password
		foundUser.setPassword(userService.generateHashPassword(user.getNewPassword()));

		// Dates handling (yeap, i do not believe what client sent me).
		Date currentDate = new Date();
		foundUser.setModifDatePassword(currentDate);
		foundUser.setExpirationDatePassword(userService.generateExpirationDatePassword());

		// Update user without questioning if employee or agent 'cause we do not care
		userService.update(foundUser);

		return ResponseEntity.ok().body(foundUser);
	}

}
