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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class that describe errors for the ws api with specifics properties
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@XmlRootElement
public class Error {

	private ErrorCodeType errorCode;
	private String errorLabel;
	private String errorMessage;
	private Long resourceId;
	private MethodType methodType;
	private String attributeName;
	private String attributeValue;
	private ClassType classType;

	/**
	 * Default constructor. Do nothing.
	 */
	public Error() {
	}

	/**
	 * Contructor. Initialize errorCode, errorLabel, errorMessage, resourceId
	 * and methodType.
	 * 
	 * @param errorCode
	 *            : error code of the error
	 * @param errorLabel
	 *            : label of the error
	 * @param errorMessage
	 *            : error message
	 * @param resourceId
	 *            : id of the resource
	 * @param methodType
	 *            : type of the method
	 */
	public Error(ErrorCodeType errorCode, String errorLabel,
			String errorMessage, Long resourceId, MethodType methodType) {
		this.errorCode = errorCode;
		this.errorLabel = errorLabel;
		this.errorMessage = errorMessage;
		this.resourceId = resourceId;
		this.methodType = methodType;
	}

	/**
	 * Contructor. Initialize errorCode, errorLabel, errorMessage, resourceId,
	 * methodType and classType.
	 * 
	 * @param errorCode
	 *            : error code of the error
	 * @param errorLabel
	 *            : label of the error
	 * @param errorMessage
	 *            : error message
	 * @param resourceId
	 *            : id of the resource
	 * @param methodType
	 *            : type of the method
	 * @param classType
	 *            : type of the class
	 */
	public Error(ErrorCodeType errorCode, String errorLabel,
			String errorMessage, Long resourceId, MethodType methodType,
			ClassType classType) {
		this.errorCode = errorCode;
		this.errorLabel = errorLabel;
		this.errorMessage = errorMessage;
		this.resourceId = resourceId;
		this.methodType = methodType;
		this.classType = classType;
	}

	/**
	 * Contructor. Initialize errorCode, errorLabel, errorMessage, resourceId,
	 * attributeName and attributeValue.
	 * 
	 * @param errorCode
	 *            : error code of the error
	 * @param errorLabel
	 *            : label of the error
	 * @param errorMessage
	 *            : error message
	 * @param resourceId
	 *            : id of the resource
	 * @param methodType
	 *            : type of the method
	 * @param attributeName
	 *            : attribute name
	 * @param attributeValue
	 *            : attribute value
	 */
	public Error(ErrorCodeType errorCode, String errorLabel,
			String errorMessage, Long resourceId, MethodType methodType,
			String attributeName, String attributeValue) {
		this.errorCode = errorCode;
		this.errorLabel = errorLabel;
		this.errorMessage = errorMessage;
		this.resourceId = resourceId;
		this.methodType = methodType;
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
	}

	/**
	 * Contructor. Initialize errorCode, errorLabel, errorMessage, resourceId,
	 * methodType, attributeName, attributeValue and classType.
	 * 
	 * @param errorCode
	 *            : error code of the error
	 * @param errorLabel
	 *            : label of the error
	 * @param errorMessage
	 *            : error message
	 * @param resourceId
	 *            : id of the resource
	 * @param methodType
	 *            : type of the method
	 * @param attributeName
	 *            : attribute name
	 * @param attributeValue
	 *            : attribute value
	 * @param classType
	 *            : class type
	 */
	public Error(ErrorCodeType errorCode, String errorLabel,
			String errorMessage, Long resourceId, MethodType methodType,
			String attributeName, String attributeValue, ClassType classType) {
		this.errorCode = errorCode;
		this.errorLabel = errorLabel;
		this.errorMessage = errorMessage;
		this.resourceId = resourceId;
		this.methodType = methodType;
		this.classType = classType;
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
	}

	/**
	 * @return the defined error code
	 */
	@XmlElement(required = true)
	public ErrorCodeType getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCodeType errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the error label
	 */
	@XmlElement(required = true)
	public String getErrorLabel() {
		return errorLabel;
	}

	public void setErrorLabel(String errorLabel) {
		this.errorLabel = errorLabel;
	}

	/**
	 * @return the error message
	 */
	@XmlElement(required = true)
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the id of the ressource
	 */
	@XmlElement(required = true)
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @return the type of the method
	 */
	@XmlElement(required = true)
	public MethodType getMethodType() {
		return methodType;
	}

	public void setMethodType(MethodType methodType) {
		this.methodType = methodType;
	}

	/**
	 * @return the attribute name
	 */
	@XmlElement(required = false)
	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * @return the attribute value
	 */
	@XmlElement(required = false)
	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	/**
	 * @return the class type
	 */
	@XmlElement(required = false)
	public ClassType getClassType() {
		return classType;
	}

	public void setClassType(ClassType classType) {
		this.classType = classType;
	}
}
