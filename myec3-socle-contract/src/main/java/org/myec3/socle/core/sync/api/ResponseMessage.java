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
 * Class describing a response message composed of an {@link HttpStatus} enum
 * and an {@link Error} property
 * 
 * This object is then used by the synchronization service to manage error handling process  
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class ResponseMessage {

	private HttpStatus httpStatus;
	private Error error;

	/**
	 * Default constructor. Do nothing.
	 */
	public ResponseMessage() {
	}

	/**
	 * Contructor. Initialize the {@link HttpStatus}
	 * 
	 * @param httpStatus
	 *            : HttpStatus value of the response message to create
	 */
	public ResponseMessage(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	/**
	 * Contructor. Initialize the {@link HttpStatus} and {@link Error}
	 * 
	 * @param httpStatus
	 *            : HttpStatus value of the response message to create
	 * @param error
	 *            : the error attached at the response
	 */
	public ResponseMessage(HttpStatus httpStatus, Error error) {
		this.httpStatus = httpStatus;
		this.error = error;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	/**
	 * @return the HttpStatus corresponding at the client response received
	 */
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setError(Error error) {
		this.error = error;
	}

	/**
	 * @return the Error corresponding at the client response received
	 */
	public Error getError() {
		return error;
	}

}
