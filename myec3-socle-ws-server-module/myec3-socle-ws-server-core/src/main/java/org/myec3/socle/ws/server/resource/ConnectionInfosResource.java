package org.myec3.socle.ws.server.resource;

import javax.ws.rs.core.MultivaluedMap;

import org.myec3.socle.core.domain.model.ConnectionInfos;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Interface which defines specific REST methods to manage
 * {@link ConnectionInfos}.
 * 
 */
@RequestMapping("/coninfos")
public interface ConnectionInfosResource {

	/**
	 * Update the ConnectionInfos linked to User with Username and the given
	 * date.
	 * 
	 * @param form : < Username , Timestamp >
	 *
	 * @return HTTP Response:
	 *         <ul>
	 *         <li>Status 200 "OK" if connection infos has been updated</li>
	 *         <li>Status 201 "CREATED" if there was no connections infos attached to the User</li>
	 *         <li>Status 404 "NOT FOUND" if no user exists with the Username given</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 * @see javax.ws.rs.core.Response.Status.OK
	 * @see javax.ws.rs.core.Response.Status.CREATED
	 * @see javax.ws.rs.core.Response.Status.NOT_FOUND
	 */
	@RequestMapping(value = "success", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity connectionSucceeded(@RequestBody MultivaluedMap<String, String> form);

	/**
	 * Return the connection informations for user with given uid
	 * 
	 * @param userId : User id
	 *
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if User is known and body containing connections infos</li>
	 *         <li>Status 404 "NOT FOUND" if no user exists with the userId given or if
	 *         there are no connections infos for the user</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 * @see javax.ws.rs.core.Response.Status.OK
	 * @see javax.ws.rs.core.Response.Status.NOT_FOUND
	 */
	@RequestMapping(value = "user/{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity getConnectionInfoByUserId(@PathVariable("uid") Long userId);

	/**
	 * Return the connection informations for User with given profileId
	 *
	 * @param profileId : Profile id of the profile attached to the user
	 *
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if profile and user are known and body containing connections infos</li>
	 *         <li>Status 404 "NOT FOUND" if no profile exists with the ProfileId given or if
	 *         there are no connections infos for the user</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 * @see javax.ws.rs.core.Response.Status.OK
	 * @see javax.ws.rs.core.Response.Status.NOT_FOUND
	 */
	@RequestMapping(value = "profile/{profileId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity getConnectionInfoByProfileId(@PathVariable("profileId") Long profileId);

}
