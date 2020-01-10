package org.myec3.socle.ws.server.resource.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.constants.MyEc3EsbConstants;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.synchro.api.SynchronizationConfiguration;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.synchro.api.SynchronizationService;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.ws.server.resource.CountryResource;
import org.myec3.socle.ws.server.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller for gestion of countries
 */
@RestController
public class CountryResourceImpl implements CountryResource {

	private static final Logger logger = LogManager.getLogger(CountryResourceImpl.class);

	@Override
	public ResponseEntity getAllCountries() {
		return ResponseEntity.ok(Country.getEnumMap());
	}
}
