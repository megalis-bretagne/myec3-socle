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

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.MethodType;

/**
 * This class defines the methods that returns WebRequestSyntaxException
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@SuppressWarnings("serial")
public class WebRequestSyntaxException extends WebApplicationException {

	/**
	 * Create a WebRequestSyntaxException : the syntax is incorrect
	 * <dl>
	 * <dt>HTTP Status</dt>
	 * <dd>400 "BAD REQUEST"</dd>
	 * <dt>Response body</dt>
	 * <dd>Error: ErrCode 001</dd>
	 * </dl>
	 * 
	 */
	public WebRequestSyntaxException(Resource resource, MethodType methodType,
			String message, String attributeName, String attributeValue) {

		super(Response
				.status(Response.Status.BAD_REQUEST)
				.entity(new Error(ErrorCodeType.SYNTAX_ERROR, "Syntax error",
						message, resource.getId(), methodType, attributeName,
						attributeValue)).type(MediaType.APPLICATION_XML)
				.build());

	}

	/**
	 * Create a WebRequestSyntaxException : the syntax is incorrect
	 * <dl>
	 * <dt>HTTP Status</dt>
	 * <dd>400 "BAD REQUEST"</dd>
	 * <dt>Response body</dt>
	 * <dd>Error: ErrCode 001</dd>
	 * </dl>
	 * 
	 */
	public WebRequestSyntaxException(Long resourceId, MethodType methodType,
			String message) {

		super(Response
				.status(Response.Status.BAD_REQUEST)
				.entity(new Error(ErrorCodeType.SYNTAX_ERROR, "Syntax error",
						message, resourceId, methodType))
				.type(MediaType.APPLICATION_XML).build());

	}
}
