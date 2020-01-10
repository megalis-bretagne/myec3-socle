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
package org.myec3.socle.core.sync.api;

/**
 * This enum provides the list of managed HTTP Status.<br/>
 * For more informations about HTTP status code @see
 * <http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html><br/>
 * enum objects are composed of a numeric value (HTTP code) and a human readable
 * label.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public enum HttpStatus {

	/**
	 * OK (200)
	 */
	OK(200, "Ok"),

	/**
	 * CREATED (201)
	 */
	CREATED(201, "Created"),

	/**
	 * ACCEPTED (202)
	 */
	ACCEPTED(202, "Accepted"),

	/**
	 * NO_CONTENT(204)
	 */
	NO_CONTENT(204, "No Content"),

	/**
	 * BAD_REQUEST(400)
	 */
	BAD_REQUEST(400, "Bad Request"),

	/**
	 * UNAUTHORIZED(401)
	 */
	UNAUTHORIZED(401, "Unauthorized"),

	/**
	 * FORBIDDEN(403)
	 */
	FORBIDDEN(403, "Forbidden"),

	/**
	 * NOT_FOUND(404)
	 */
	NOT_FOUND(404, "Not Found"),

	/**
	 * METHOD_NOT_ALLOWED(405)
	 */
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

	/**
	 * INTERNAL_SERVER_ERROR(500)
	 */
	INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

	/**
	 * SERVER_UNAVAILABLE(503)
	 */
	SERVER_UNAVAILABLE(503, "Server Unavailable");

	/**
	 * Numeric HTTP status code
	 */
	private final int value;

	/**
	 * HTTP Status label
	 */
	private final String label;

	private HttpStatus(int value, String label) {
		this.value = value;
		this.label = label;
	}

	public int getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

}
