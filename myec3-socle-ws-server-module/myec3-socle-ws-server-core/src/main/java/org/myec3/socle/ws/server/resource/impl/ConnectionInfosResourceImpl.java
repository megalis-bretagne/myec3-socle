package org.myec3.socle.ws.server.resource.impl;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.ConnectionInfos;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.ConnectionInfosService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.ws.server.resource.ConnectionInfosResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Scope("prototype")
public class ConnectionInfosResourceImpl implements ConnectionInfosResource {

	private static final Logger logger = LogManager.getLogger(ConnectionInfosResourceImpl.class);

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	@Autowired
	@Qualifier("connectionInfosService")
	private ConnectionInfosService connectionInfosService;

	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity connectionSucceeded(MultivaluedMap<String, String> form) {
		String userName = form.getFirst("userName");
		String timestamp = form.getFirst("timestamp");
		logger.debug("Going to update connectionInfo of profile " + userName + " : new date is " + timestamp);
		User user = userService.findByUsername(userName);
		if (user == null) {
			logger.debug("User is null... It does not exist");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		ConnectionInfos conInfos = user.getConnectionInfos();
		Long newDateMili = Long.parseLong(timestamp);
		if (conInfos == null) {
			logger.debug("No conInfo linked to this profile... It must be the first connection");
			ConnectionInfos newConInfos = new ConnectionInfos(newDateMili);
			connectionInfosService.create(newConInfos);
			user.setConnectionInfos(newConInfos);
			userService.update(user);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} else {
			logger.debug("updating conInfos...");
			connectionInfosService.update(conInfos, newDateMili);
		}
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity getConnectionInfoByUserId(Long userId) {
		User user = userService.findOne(userId);
		if (user != null) {
			if (user.getConnectionInfos() != null) {
				return ResponseEntity.ok().body(user.getConnectionInfos());
			}
			logger.debug("User with id " + userId + " has no connection informations");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		logger.debug("User is null... It does not exist");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@Override
	public ResponseEntity getConnectionInfoByProfileId(Long profileId) {
		Profile profile = profileService.findOne(profileId);
		if (profile != null) {
			User user = profile.getUser();
			if (user != null) {
				if (user.getConnectionInfos() != null) {
					return ResponseEntity.ok().body(user.getConnectionInfos());
				}
				logger.debug("User with id " + user.getId() + " has no connection informations");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			logger.error("Profile with id " + profileId + " has no user...");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		logger.debug("Profile is null... It does not exist");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

}
