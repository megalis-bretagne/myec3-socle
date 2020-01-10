/**
 * This ressource is use for update profil
 */
package org.myec3.socle.ws.server.resource;


import org.myec3.socle.ws.server.dto.FullProfileDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Interface which defines methode availables for profile
 *
 * @author Charles Bourrée
 */
@RequestMapping("/profil/")
public interface ProfilResource {

	/**
	 * Return a Profile for a given profileId.
	 *
	 * @param profileId  : Profile Id
	 *
	 * @return HTTP Response and JSON body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if Profile is known and body containing the profile</li>
	 *         <li>Status 404 "NOT FOUND" if no profile exists for the given profileId</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 * @see javax.ws.rs.core.Response.Status.OK
	 * @see javax.ws.rs.core.Response.Status.NOT_FOUND
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity getProfile(@RequestHeader("uid") Long profileId);

	/**
	 * Update a profile with object given.
	 *
	 * @param profileId  : Profile Id to be updated
	 * @param profile    : New information for the profile 
	 *
	 * @return HTTP Response and JSON body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if Profile is known and body containing the updated profile</li>
	 *         <li>Status 404 "NOT FOUND" if no profile exists for the given profileId</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 * @see javax.ws.rs.core.Response.Status.OK
	 * @see javax.ws.rs.core.Response.Status.NOT_FOUND
	 */
	@RequestMapping(value = "/", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity updateProfile(@RequestHeader("uid") Long profileId, @RequestBody FullProfileDto profile);

	/**
	 * Return informations of user using his uid.
	 * 
	 * @param profileId : Profile Id
	 * 
	 * @return HTTP Response and XML body with:
	 *          <ul>
	 *          <li>Status 200 "OK" if Profile is known and body containing the Profile infoss</li>
	 *          <li>Status 404 "NOT FOUND" if no profile exists for the given profileId</li>
	 *          <li>Other statuses : errors</li>
	 *          </ul>
	 *  @see javax.ws.rs.core.Response.Status.OK
	 *  @see javax.ws.rs.core.Response.Status.NOT_FOUND
	 */
	//@CrossOrigin Only for local tests
	@RequestMapping(value = "/informations", method = RequestMethod.GET)
	ResponseEntity getInformations(@RequestHeader("uid") Long profileId);
}
