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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * This enum provides the list of possible error codes that can be managed
 * during a synchronization. values are :
 * 
 * each code is composed of a label containg the numeri code identifier.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@XmlEnum
public enum ErrorCodeType {

	/**
	 * SYNTAX_ERROR (code: 001) - the syntax of the input message is not correct
	 */
	@XmlEnumValue("001")
	SYNTAX_ERROR("001"),

	/**
	 * FORMAT_ERROR (code: 002) - one of the field of the resource is invalid
	 */
	@XmlEnumValue("002")
	FORMAT_ERROR("002"),

	/**
	 * RESOURCE_MISSING (code: 003) - the resource cannot be found
	 */
	@XmlEnumValue("003")
	RESOURCE_MISSING("003"),

	/**
	 * RELATION_MISSING (code: 004) - one of the relation of the resource cannot
	 * be found
	 */
	@XmlEnumValue("004")
	RELATION_MISSING("004"),

	/**
	 * RESOURCE_ALREADY_EXISTS (code: 005) - the resource already exists
	 */
	@XmlEnumValue("005")
	RESOURCE_ALREADY_EXISTS("005"),

	/**
	 * INTERNAL_SERVER_ERROR (code: 006) - server is not responding
	 */
	@XmlEnumValue("006")
	INTERNAL_SERVER_ERROR("006"),

	/**
	 * INTERNAL_CLIENT_ERROR (code: 007) - client is not responding
	 */
	@XmlEnumValue("007")
	INTERNAL_CLIENT_ERROR("007");

	/**
	 * label of the error code type
	 */
	private final String label;

	private ErrorCodeType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
