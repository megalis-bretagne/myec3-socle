package org.myec3.socle.ws.server.resource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.myec3.socle.core.domain.model.InseeGlobal;

/**
 * Interface which defines specific REST methods to manage {@link InseeGlobal}
 */

@RequestMapping("/insee/")
public interface InseeResource {


	/**
	 * Return InseeGlobal (all Insee's informations) of a Company.
	 *
	 * @param companyId  : the Company ID
	 *
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if company is known and body containing an InseeGlobal</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 * @see javax.ws.rs.core.Response.Status.OK
	 * @see javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
	 */
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity getInseeInfoByCompany(@RequestParam("companyId") Long companyId);

}
