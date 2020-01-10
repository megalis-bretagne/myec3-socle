package org.myec3.socle.ws.server.resource;

import org.myec3.socle.core.domain.model.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.myec3.socle.core.domain.model.enums.Country;

/**
 * Interface which defines specific REST methods to manage {@link Country}
 */

@RequestMapping("/country/")
public interface CountryResource {


	/**
	 * Return the list of Countries.
	 *
	 * @return HTTP Response and JSON body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if body containing a
	 *         List of Countries</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 * @see javax.ws.rs.core.Response.Status.OK
	 * @see javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
	 */
	@CrossOrigin
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity getAllCountries();


}
