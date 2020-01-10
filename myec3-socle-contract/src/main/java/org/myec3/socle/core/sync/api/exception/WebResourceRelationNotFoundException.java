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
import org.myec3.socle.core.sync.api.ClassType;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.MethodType;

/**
 * This class defines the methods that returns WebResourceRelationNotFoundException
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@SuppressWarnings("serial")
public class WebResourceRelationNotFoundException extends
		WebApplicationException {

	/**
	 * Create a WebResourceRelationNotFoundException : a relation was not found
	 * 
	 * <dl>
	 * <dt>HTTP Status</dt>
	 * <dd>404 "NOT FOUND"</dd>
	 * <dt>Response body</dt>
	 * <dd>Error: ErrCode 004</dd>
	 * </dl>
	 * 
	 */
	public WebResourceRelationNotFoundException(Resource resource,
			Long relationId, MethodType methodType, String message,
			String attributeName, String attributeValue, ClassType classType) {

		super(Response
				.status(Response.Status.NOT_FOUND)
				.entity(new Error(ErrorCodeType.RELATION_MISSING,
						"Relation missing", message, relationId, methodType,
						attributeName, attributeValue, classType))
				.type(MediaType.APPLICATION_XML).build());

	}

	/**
	 * Create a WebResourceRelationNotFoundException : a relation was not found
	 * 
	 * <dl>
	 * <dt>HTTP Status</dt>
	 * <dd>404 "NOT FOUND"</dd>
	 * <dt>Response body</dt>
	 * <dd>Error: ErrCode 004</dd>
	 * </dl>
	 * 
	 */
	public WebResourceRelationNotFoundException(Long relationId,
			MethodType methodType, String message, ClassType classType) {

		super(Response
				.status(Response.Status.NOT_FOUND)
				.entity(new Error(ErrorCodeType.RELATION_MISSING,
						"Relation missing", message, relationId, methodType,
						classType)).type(MediaType.APPLICATION_XML).build());

	}
}
