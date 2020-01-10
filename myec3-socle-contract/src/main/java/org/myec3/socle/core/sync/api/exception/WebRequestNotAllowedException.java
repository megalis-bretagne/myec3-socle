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
package org.myec3.socle.core.sync.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.core.sync.api.MethodType;

/**
 * This class defines the methods that returns WebRequestNotAllowedException
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@SuppressWarnings("serial")
public class WebRequestNotAllowedException extends WebApplicationException {

	/**
	 * Create a WebRequestNotAllowedException : This request is not allowed
	 * <dl>
	 * <dt>HTTP Status</dt>
	 * <dd>405 "Method Not Allowed"</dd>
	 * <dt>Response body</dt>
	 * <dd>Error: ErrCode xxx</dd>
	 * </dl>
	 * 
	 */
	public WebRequestNotAllowedException(Long resourceId, MethodType methodType) {

		// Method not allowed = http status 415
		super(Response
				.status(HttpStatus.METHOD_NOT_ALLOWED.getValue())
				.entity(new Error(null, "Request not allowed",
						"You have not the right to use this method",
						resourceId, methodType))
				.type(MediaType.APPLICATION_XML).build());

	}
}
