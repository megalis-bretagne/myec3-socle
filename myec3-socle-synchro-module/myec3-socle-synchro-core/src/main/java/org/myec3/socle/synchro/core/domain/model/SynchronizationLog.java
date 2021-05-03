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
package org.myec3.socle.synchro.core.domain.model;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * This class is used to log all informations about synchronizations sent at the
 * remote applications which have subscribed at the synchronization process
 * (@See SynchronizationSubscription).<br />
 * 
 * Thanks to this class it's possible to know exactly if a given synchronization
 * is successful, otherwise you have the possibility to know why through
 * {@link SynchronizationError}.<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
public class SynchronizationLog implements Serializable, PE {

	private static final long serialVersionUID = -6503845346771367675L;

	private Long id;
	private Long resourceId;
	private ResourceType resourceType;
	private Long responseMessageResourceId;
	private int httpCode;
	private HttpStatus httpStatus;
	private ErrorCodeType errorCodeType;
	private String errorLabel;
	private String errorMessage;
	private MethodType methodType;
	private Date synchronizationDate;
	private int nbAttempts;
	private SynchronizationSubscription synchronizationSubscription;
	private String applicationName;
	private Boolean isFinal;
	private String statut;
	private SynchronizationType synchronizationType;
	private String sendingApplication;

	/**
	 * Default constructor. Do nothing.
	 */
	public SynchronizationLog() {
	}

	/**
	 * Technical id of the SynchronizationLog
	 * 
	 * @return the id for this SynchronizationLog
	 */
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the ID of the {@link Resource} which has been synchronized.
	 */
	@Column(nullable = false)
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @return the type of the resource which has been synchronized.
	 * @see ResourceType
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @return the ID returned by the distant application in case of error. There
	 *         are two possibilities for that field to not be null :
	 *         <ul>
	 *         <li>If the error concern the the resource sent, then the
	 *         responseMessageResourceId is equals at the resource id</li>
	 *         <li>If a parent resource is missing, then the
	 *         responseMessageResourceId is equals at the parent resource id. In
	 *         this case the error handling mecanism send the missing resource
	 *         before send back the origin resource synchronized</li>
	 *         </ul>
	 */
	@Column(nullable = true)
	public Long getResponseMessageResourceId() {
		return responseMessageResourceId;
	}

	public void setResponseMessageResourceId(Long responseMessageResourceId) {
		this.responseMessageResourceId = responseMessageResourceId;
	}

	/**
	 * @return the HTTP code returned by the distant application. To see all
	 *         possible values @see HttpStatus.
	 */
	@Column(nullable = false)
	public int getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

	/**
	 * @return the {@link HttpStatus} corresponding at the HTTP code returned by the
	 *         distant application.
	 * 
	 * @see HttpStatus
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	/**
	 * @return the {@link ErrorCodeType} corresponding at the error returned by the
	 *         distant application.
	 * 
	 * @see ErrorCodeType
	 * @see Error
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	public ErrorCodeType getErrorCodeType() {
		return errorCodeType;
	}

	public void setErrorCodeType(ErrorCodeType errorCodeType) {
		this.errorCodeType = errorCodeType;
	}

	/**
	 * @return the error label returned by the distant application during the
	 *         synchronization process if an error has occured.
	 * @see Error
	 */
	@Column(nullable = true)
	public String getErrorLabel() {
		return errorLabel;
	}

	public void setErrorLabel(String errorLabel) {
		this.errorLabel = errorLabel;
	}

	/**
	 * @return the error message returned by the distant application during the
	 *         synchronization process if an error has occured.
	 * 
	 * @see Error
	 */
	@Column(nullable = true, columnDefinition = "TEXT")
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the date of the synchronization.
	 */
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getSynchronizationDate() {
		return synchronizationDate;
	}

	public void setSynchronizationDate(Date synchronizationDate) {
		this.synchronizationDate = synchronizationDate;
	}

	/**
	 * @return SUCCESS in case of synchronization success. Else return ERROR.
	 */
	@Column(nullable = false)
	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	/**
	 * @return the method type {@link MethodType} used during the synchronization
	 *         process.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public MethodType getMethodType() {
		return methodType;
	}

	public void setMethodType(MethodType methodType) {
		this.methodType = methodType;
	}

	/**
	 * @return the number of attempts for a given synchronization. There is a limit
	 *         of attempts defined into {@link MyEc3SynchroConstants}
	 */
	@Column(nullable = false)
	public int getNbAttempts() {
		return nbAttempts;
	}

	public void setNbAttempts(int nbAttempts) {
		this.nbAttempts = nbAttempts;
	}

	/**
	 * @return true if there will be no attempt to synchronize the resource
	 *         {@link Resource}.
	 */
	@Column(nullable = false)
	public Boolean getIsFinal() {
		if (isFinal == null) {
			isFinal = Boolean.FALSE;
		}
		return isFinal;
	}

	public void setIsFinal(Boolean isFinal) {
		this.isFinal = isFinal;
	}

	/**
	 * @return the {@link SynchronizationSubscription} used during this
	 *         synchronization.
	 */
	@ManyToOne
	@JoinColumn(nullable = false)
	public SynchronizationSubscription getSynchronizationSubscription() {
		return synchronizationSubscription;
	}

	public void setSynchronizationSubscription(SynchronizationSubscription synchronizationSubscription) {
		this.synchronizationSubscription = synchronizationSubscription;
	}

	/**
	 * @return the distant {@link Application} name which has received the
	 *         synchronization.
	 */
	@Column(nullable = false)
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * @return the type of the synchronization {@link SynchronizationType} used to
	 *         synchronize the resource.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public SynchronizationType getSynchronizationType() {
		return synchronizationType;
	}

	public void setSynchronizationType(SynchronizationType synchronizationType) {
		this.synchronizationType = synchronizationType;
	}

	/**
	 * @return the name of the application which has sent the synchronization
	 *         {@link SynchronizationType}.
	 */
	@Column(nullable = true)
	public String getSendingApplication() {
		return sendingApplication;
	}

	public void setSendingApplication(String sendingApplication) {
		this.sendingApplication = sendingApplication;
	}
}
