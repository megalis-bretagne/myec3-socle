package org.myec3.socle.ws.server.resource;

import org.myec3.socle.core.domain.model.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Interface which defines specific REST methods to manage {@link User}
 */

@RequestMapping("/user/")
public interface UserResource {

	/**
	 * Update user with password behavior (crypting, controlling, ...)
	 *
	 * @param userId : the User ID
	 * @parem User   : the User object  
	 *
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if User is known and body containing the User</li>
	 *         <li>Status 404 "NOT FOUND" if no User found with user object in params</li>
	 *         <li>Status 400 "BAD_REQUEST" if the new password is not conform</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 * @see javax.ws.rs.core.Response.Status.OK
	 * @see javax.ws.rs.core.Response.Status.NOT_FOUND
	 * @see javax.ws.rs.core.Response.Status.BAD_REQUEST
	 * @see javax.ws.rs.core.Response.Status.FORBIDDEN
	 * @see javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
	 */
	@CrossOrigin
	@RequestMapping(value = "{userId}/password", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity updatePassword(@PathVariable("userId") Long userId, @RequestBody User user);


}
