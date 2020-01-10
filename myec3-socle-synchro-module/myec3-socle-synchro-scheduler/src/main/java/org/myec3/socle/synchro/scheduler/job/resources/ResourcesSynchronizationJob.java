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
package org.myec3.socle.synchro.scheduler.job.resources;

import java.util.Date;
import java.util.List;

import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.AdminProfileService;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.CompanyDepartmentService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.CustomerService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.ResourceService;
import org.myec3.socle.core.sync.api.ClassType;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationRelationsName;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationError;
import org.myec3.socle.synchro.core.domain.model.SynchronizationFilter;
import org.myec3.socle.synchro.core.domain.model.SynchronizationInitial;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchronizationErrorService;
import org.myec3.socle.synchro.core.service.SynchronizationFilterService;
import org.myec3.socle.synchro.core.service.SynchronizationInitialService;
import org.myec3.socle.synchro.core.service.SynchronizationLogService;
import org.myec3.socle.synchro.core.service.SynchronizationSubscriptionService;
import org.myec3.socle.synchro.scheduler.constants.MyEc3SynchroConstants;
import org.myec3.socle.synchro.scheduler.service.SchedulerService;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.Assert;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T> : Concrete type of the resource to synchronize.
 */
public abstract class ResourcesSynchronizationJob<T extends Resource> extends QuartzJobBean implements StatefulJob {

	private static Logger logger = LoggerFactory.getLogger(ResourcesSynchronizationJob.class);
	private static final String ERROR_STATUT = "ERROR";
	private static final String SUCCESS_STATUT = "SUCCESS";

	/**
	 * All these fields will be injected by Spring. The injection is made
	 * declaratively in application context file (synchroMyec3Context.xml) because
	 * of this class extends QuartzJobBean which is not a Spring service : lifecycle
	 * is managed by quartz.
	 */

	/**
	 * Service used to create new quartz job
	 */
	@Autowired
	@Qualifier("parallelSchedulerService")
	private SchedulerService schedulerService;

	/**
	 * Service used to send REST request by using myec3 REST client
	 */
	@Autowired
	@Qualifier("resourceWsClient")
	private ResourceWsClient resourceWsClientService;

	/**
	 * Service used to send REST request to HTTPS external API
	 */
	@Autowired
	@Qualifier("externalWsClient")
	private ResourceWsClient externalWsClientService;

	/**
	 * Service used to manage {@link SynchronizationError} objects
	 */
	@Autowired
	@Qualifier("synchronizationErrorService")
	private SynchronizationErrorService synchronizationErrorService;

	/**
	 * Service used to manage {@link InitialSynchronization} objects
	 */
	@Autowired
	@Qualifier("synchronizationInitialService")
	private SynchronizationInitialService synchronizationInitialService;

	/**
	 * Service used to manage {@link AdminProfile} objects
	 */
	@Autowired
	@Qualifier("adminProfileService")
	private AdminProfileService adminProfileService;

	/**
	 * Service used to manage {@link AgentProfile} objects
	 */
	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Service used to manage {@link EmployeeProfile} objects
	 */
	@Autowired
	@Qualifier("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	/**
	 * Service used to manage {@link Organism} objects
	 */
	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;

	/**
	 * Service used to manage {@link OrganismDepartment} objects
	 */
	@Autowired
	@Qualifier("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	/**
	 * Service used to manage {@link Company} objects
	 */
	@Autowired
	@Qualifier("companyService")
	private CompanyService companyService;

	/**
	 * Service used to manage {@link CompanyDepartment} objects
	 */
	@Autowired
	@Qualifier("companyDepartmentService")
	private CompanyDepartmentService companyDepartmentService;

	/**
	 * Service used to manage {@link Establishment} objects
	 */
	@Autowired
	@Qualifier("establishmentService")
	private EstablishmentService establishmentService;

	/**
	 * Service used to manage {@link Customer} objects
	 */
	@Autowired
	@Qualifier("customerService")
	private CustomerService customerService;

	/**
	 * Service used to manage {@link SynchronizationSubscription} objects
	 */
	@Autowired
	@Qualifier("synchronizationSubscriptionService")
	private SynchronizationSubscriptionService synchronizationSubscriptionService;

	/**
	 * Service used to manage {@link SynchronizationLog} objects
	 */
	@Autowired
	@Qualifier("synchronizationLogService")
	private SynchronizationLogService synchronizationLogService;

	/**
	 * Service used to manage {@link SynchronizationFilter} objects
	 */
	@Autowired
	@Qualifier("synchronizationFilterService")
	private SynchronizationFilterService synchronizationFilterService;

	/**
	 * Response message returned by the web service
	 * 
	 * @see ResponseMessage.class
	 */
	private ResponseMessage responseMessage;

	/**
	 * Id of the synchronizationError. The first time that the synchronization job
	 * is called synchronizationErrorId is equal at null
	 */
	private Long synchronizationErrorId;

	/**
	 * Id initial of the synchronization. Represent the first time that the
	 * synchronization job is called
	 */
	private Long initialSynchronizationId;

	/**
	 * Job Type of synchronization : CREATE, UPDATE, DELETE
	 * 
	 * @see SynchronizationJobType.class
	 */
	private SynchronizationJobType synchronizationJobType;

	/**
	 * Type of synchronization : SYNCHRONIZATION, ERROR_HANDLING,
	 * RESYNCHRONIZATION...
	 * 
	 * @see SynchronizationType.class
	 */
	private SynchronizationType synchronizationType;

	/**
	 * Application sending JMS message
	 */
	private String sendingApplication;

	/**
	 * The resource to synchronize
	 * 
	 * @see Resource.class
	 */
	private T resource;

	/**
	 * The list of role of the profile (agentProfile or EmployeeProfile)
	 */
	private List<Role> profileListRoles;

	/**
	 * Represent the subscription of the synchronization used during the
	 * synchronization of the current Resource
	 */
	private SynchronizationSubscription subscription;

	/**
	 * @return an AdminProfileService
	 */
	public AdminProfileService getAdminProfileService() {
		return adminProfileService;
	}

	/**
	 * @return an AgentProfileService
	 */
	public AgentProfileService getAgentProfileService() {
		return agentProfileService;
	}

	/**
	 * @param agentProfileService the agentProfileService to set
	 */
	public void setAgentProfileService(AgentProfileService agentProfileService) {
		this.agentProfileService = agentProfileService;
	}

	/**
	 * @return an EmployeeProfileService
	 */
	public EmployeeProfileService getEmployeeProfileService() {
		return employeeProfileService;
	}

	/**
	 * @param employeeProfileService the employeeProfileService to set
	 */
	public void setEmployeeProfileService(EmployeeProfileService employeeProfileService) {
		this.employeeProfileService = employeeProfileService;
	}

	/**
	 * @return an CompanyService
	 */
	public CompanyService getCompanyService() {
		return companyService;
	}

	/**
	 * @param companyService the companyService to set
	 */
	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	/**
	 * @return an CompanyDepartmentService
	 */
	public CompanyDepartmentService getCompanyDepartmentService() {
		return companyDepartmentService;
	}

	/**
	 * @param companyDepartmentService the companyDepartmentService to set
	 */
	public void setCompanyDepartmentService(CompanyDepartmentService companyDepartmentService) {
		this.companyDepartmentService = companyDepartmentService;
	}

	/**
	 * @return an EstablishmentService
	 */
	public EstablishmentService getEstablishmentService() {
		return establishmentService;
	}

	/**
	 * @param establishmentService the establishmentService to set
	 */
	public void setEstablishmentService(EstablishmentService establishmentService) {
		this.establishmentService = establishmentService;
	}

	/**
	 * @return an customerService
	 */
	public CustomerService getCustomerService() {
		return customerService;
	}

	/**
	 * @param customerService the customerService to set
	 */
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	/**
	 * @return an OrganismService
	 */
	public OrganismService getOrganismService() {
		return organismService;
	}

	/**
	 * @param organismService the organismService to set
	 */
	public void setOrganismService(OrganismService organismService) {
		this.organismService = organismService;
	}

	/**
	 * @return an OrganismDepartmentService
	 */
	public OrganismDepartmentService getOrganismDepartmentService() {
		return organismDepartmentService;
	}

	/**
	 * @param organismDepartmentService the organismDepartmentService to set
	 */
	public void setOrganismDepartmentService(OrganismDepartmentService organismDepartmentService) {
		this.organismDepartmentService = organismDepartmentService;
	}

	/**
	 * @return an synchronizationFilterService
	 */
	public SynchronizationFilterService getSynchronizationFilterService() {
		return synchronizationFilterService;
	}

	/**
	 * @param synchronizationFilterService the synchronizationFilterService to set
	 */
	public void setSynchronizationFilterService(SynchronizationFilterService synchronizationFilterService) {
		this.synchronizationFilterService = synchronizationFilterService;
	}

	/**
	 * @param synchronizationErrorService the synchronizationErrorService to set
	 */
	public void setSynchronizationErrorService(SynchronizationErrorService synchronizationErrorService) {
		this.synchronizationErrorService = synchronizationErrorService;
	}

	/**
	 * @return the synchronization error
	 */
	public SynchronizationErrorService getSynchronizationErrorService() {
		return synchronizationErrorService;
	}

	/**
	 * @return the synchronization initial service
	 */
	public SynchronizationInitialService getSynchronizationInitialService() {
		return synchronizationInitialService;
	}

	/**
	 * @param synchronizationInitialService the synchronizationInitialService to set
	 */
	public void setSynchronizationInitialService(SynchronizationInitialService synchronizationInitialService) {
		this.synchronizationInitialService = synchronizationInitialService;
	}

	/**
	 * @return the SynchronizationSubscriptionService
	 */
	public SynchronizationSubscriptionService getSynchronizationSubscriptionService() {
		return synchronizationSubscriptionService;
	}

	/**
	 * @param synchronizationSubscriptionService the
	 *                                           synchronizationSubscriptionService
	 *                                           to set
	 */
	public void setSynchronizationSubscriptionService(
			SynchronizationSubscriptionService synchronizationSubscriptionService) {
		this.synchronizationSubscriptionService = synchronizationSubscriptionService;
	}

	/**
	 * @return the SynchronizationLogService
	 */
	public SynchronizationLogService getSynchronizationLogService() {
		return synchronizationLogService;
	}

	/**
	 * @param synchronizationLogService the SynchronizationLogService to set
	 */
	public void setSynchronizationLogService(SynchronizationLogService synchronizationLogService) {
		this.synchronizationLogService = synchronizationLogService;
	}

	/**
	 * @return the resourceWsClientService
	 */
	public ResourceWsClient getResourceWsClientService() {
		return resourceWsClientService;
	}

	/**
	 * @param resourceWsClientService the resourceWsClientService to set
	 */
	public void setResourceWsClientService(ResourceWsClient resourceWsClientService) {
		this.resourceWsClientService = resourceWsClientService;
	}

	/**
	 * @return the externalWsClientService
	 */
	public ResourceWsClient getExternalWsClientService() {
		return externalWsClientService;
	}

	/**
	 * @param externalWsClientService the externalWsClientService to set
	 */
	public void setExternalWsClientService(ResourceWsClient externalWsClientService) {
		this.externalWsClientService = externalWsClientService;
	}

	/**
	 * @return the SchedulerService
	 */
	public SchedulerService getSchedulerService() {
		return schedulerService;
	}

	/**
	 * @param schedulerService the schedulerService to set
	 */
	public void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	/**
	 * @return the list of roles of a {@link Profile}
	 */
	public List<Role> getProfileListRoles() {
		return profileListRoles;
	}

	/**
	 * @param profileListRoles the profileListRoles to set
	 */
	public void setProfileListRoles(List<Role> profileListRoles) {
		this.profileListRoles = profileListRoles;
	}

	/**
	 * @return the initial synchronization id
	 */
	public Long getInitialSynchronizationId() {
		return initialSynchronizationId;
	}

	/**
	 * @param initialSynchronizationId the initialSynchronizationId to set
	 */
	public void setInitialSynchronizationId(Long initialSynchronizationId) {
		this.initialSynchronizationId = initialSynchronizationId;
	}

	/**
	 * @return a the synchronization error id
	 */
	public Long getSynchronizationErrorId() {
		return synchronizationErrorId;
	}

	/**
	 * @param synchronizationErrorId the synchronizationErrorId to set
	 */
	public void setSynchronizationErrorId(Long synchronizationErrorId) {
		this.synchronizationErrorId = synchronizationErrorId;
	}

	/**
	 * @return the synchronization type used during the synchronization
	 */
	public SynchronizationType getSynchronizationType() {
		return synchronizationType;
	}

	/**
	 * @param synchronizationType the synchronizationType to set
	 */
	public void setSynchronizationType(SynchronizationType synchronizationType) {
		this.synchronizationType = synchronizationType;
	}

	/**
	 * @return the application which has triggered the synchronization
	 */
	public String getSendingApplication() {
		return sendingApplication;
	}

	/**
	 * @param sendingApplication the sendingApplication to set
	 */
	public void setSendingApplication(String sendingApplication) {
		this.sendingApplication = sendingApplication;
	}

	/**
	 * @return the synchronizationJobType
	 */
	public SynchronizationJobType getSynchronizationJobType() {
		return synchronizationJobType;
	}

	/**
	 * @param synchronizationJobType the synchronizationJobType to set
	 */
	public void setSynchronizationJobType(SynchronizationJobType synchronizationJobType) {
		this.synchronizationJobType = synchronizationJobType;
	}

	/**
	 * @return the resource synchronized
	 */
	public T getResource() {
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(T resource) {
		this.resource = resource;
	}

	/**
	 * @return the subscription used to synchronize the Resource
	 */
	public SynchronizationSubscription getSubscription() {
		return subscription;
	}

	/**
	 * @param subscription the subscription to set
	 */
	public void setSubscription(SynchronizationSubscription subscription) {
		this.subscription = subscription;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * @param responseMessage the responseMessage to set
	 */
	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	/**
	 * @return the responseMessage
	 */
	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	/**
	 * Method called by the synchronization job. Calls POST method of the web
	 * service and retrieve its response message
	 */
	public abstract ResponseMessage create(T resource, SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient);

	/**
	 * Method called by the synchronization job. Calls PUT method of the web service
	 * and retrieve its response message
	 */
	public abstract ResponseMessage update(T resource, SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient);

	/**
	 * Method called by the synchronization job. Calls DELETE method of the web
	 * service and retrieve its response message
	 */
	public abstract ResponseMessage delete(T resource, SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient);

	/**
	 * Method inherited from quartz framework and called when job is ready to be
	 * launched. Calls the correct service depending on the resource synchronization
	 * job type.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

		logger.debug("Enterring method executeInternal");
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		SynchronizationSubscription synchronizationSubscription = (SynchronizationSubscription) jobDataMap
				.get("subscription");
		T synchronizationResource = (T) jobDataMap.get("resource");

		// If it's an agent or an employee we retrieve and set his role
		if (synchronizationResource.getClass().getSuperclass().equals(Profile.class)) {
			if (jobDataMap.containsKey("roles")) {
				this.setProfileListRoles((List<Role>) jobDataMap.get(SynchronizationRelationsName.ROLES));
				logger.debug("[Enterring method executeInternal] Roles of profile : " + this.getProfileListRoles());
				// We set the list of role to the profile
				((Profile) synchronizationResource).setRoles(this.getProfileListRoles());
			}
		}

		switch (this.getSynchronizationJobType()) {
		case CREATE:
			this.setResponseMessage(this.create(synchronizationResource, synchronizationSubscription,
					this.getResourceWsClient(synchronizationSubscription)));
			break;
		case UPDATE:
			this.setResponseMessage(this.update(synchronizationResource, synchronizationSubscription,
					this.getResourceWsClient(synchronizationSubscription)));
			break;
		case DELETE:
			this.setResponseMessage(this.delete(synchronizationResource, synchronizationSubscription,
					this.getResourceWsClient(synchronizationSubscription)));
			break;
		}

		// We check the response returned by the web service client
		this.checkResponseMessage(this.getResponseMessage(), (Resource) synchronizationResource,
				synchronizationSubscription);
	}

	private ResourceWsClient getResourceWsClient(SynchronizationSubscription synchronizationSubscription) {
		if (synchronizationSubscription.getHttps()) {
			return externalWsClientService;
		} else {
			return resourceWsClientService;
		}
	}

	/**
	 * Check the responseMessage returned by the web service client
	 * 
	 * @param responseMessage             : the response returned by the WS client
	 * @param resource                    : the {@link Resource} synchronized
	 * @param synchronizationSubscription : the {@link synchronizationSubscription}
	 *                                    concerned by the synchronization
	 */
	protected void checkResponseMessage(ResponseMessage responseMessage, Resource resource,
			SynchronizationSubscription synchronizationSubscription) {
		logger.debug("Starting checkResponseMessage");

		// Save synchronization log into the database (SUCCESS OR ERROR)
		this.manageSynchronizationLog(resource, responseMessage, synchronizationSubscription);

		// If HTTP status > 204 the response message is consider as an error
		if (responseMessage.getHttpStatus().getValue() > HttpStatus.NO_CONTENT.getValue()) {
			this.logErrorContainedInResponseMessage(responseMessage, synchronizationSubscription);
		}
		// we check if this resource is already contained in the synchronization
		// table (synchronizationerror)
		// of the database. In this case, the row is deleted
		else {
			this.manageSynchronizationSuccess(resource);
		}

		// Check the http status
		switch (this.getResponseMessage().getHttpStatus()) {
		// Success cases
		case OK:
			logger.info("[checkResponseMessage][OK] " + "HTTP Status 200 : synchronization successful to "
					+ synchronizationSubscription.getApplication().getName());
			break;

		case CREATED:
			logger.info("[checkResponseMessage][CREATED] " + "HTTP Status 201 : synchronization successful to "
					+ synchronizationSubscription.getApplication().getName());
			break;

		case ACCEPTED:
			logger.info("[checkResponseMessage][ACCEPTED] " + "HTTP Status 202 : synchronization successful to "
					+ synchronizationSubscription.getApplication().getName());
			break;

		case NO_CONTENT:
			logger.info("[checkResponseMessage][NO CONTENT] " + "HTTP Status 204 : synchronization successful to "
					+ synchronizationSubscription.getApplication().getName());
			break;

		// Error cases
		case BAD_REQUEST:
			logger.info("[checkResponseMessage][BAD_REQUEST][" + responseMessage.getError().getMethodType()
					+ "] HTTP Status 400 : synchronization error ");
			// check the error code type in order to know what is the action to
			// perform
			this.checkResponseMessageErrorCodeType(responseMessage, resource, synchronizationSubscription,
					this.synchronizationJobType);
			break;

		case UNAUTHORIZED:
			// Nothing to do, the synchronization error is already logged into
			// the database
			logger.info("[checkResponseMessage][UNAUTHORIZED][" + responseMessage.getError().getMethodType()
					+ "] HTTP Status 401 : synchronization error ");
			break;

		case FORBIDDEN:
			// Nothing to do, the synchronization error is already logged into
			// the database
			logger.info("[checkResponseMessage][FORBIDDEN][" + responseMessage.getError().getMethodType()
					+ "] HTTP Status 403 : synchronization error ");
			break;

		case NOT_FOUND:
			logger.info("[checkResponseMessage][NOT FOUND][" + responseMessage.getError().getMethodType()
					+ "] HTTP Status 404 : synchronization error ");
			// check the error code type in order to know what is the action to
			// perform
			this.checkResponseMessageErrorCodeType(responseMessage, resource, synchronizationSubscription,
					this.synchronizationJobType);
			break;

		case METHOD_NOT_ALLOWED:
			// Nothing to do, the synchronization error is already logged into
			// the database
			logger.info("[checkResponseMessage][METHOD NOT ALLOWED][" + responseMessage.getError().getMethodType()
					+ "] HTTP Status 405 : synchronization error ");
			break;

		case INTERNAL_SERVER_ERROR:
			logger.info("[checkResponseMessage][INTERNAL SERVER ERROR][" + responseMessage.getError().getMethodType()
					+ "] HTTP Status 500 : synchronization error ");
			// check the error code type in order to know what is the action to
			// perform
			this.checkResponseMessageErrorCodeType(responseMessage, resource, synchronizationSubscription,
					this.synchronizationJobType);
			break;

		case SERVER_UNAVAILABLE:
			logger.info("[checkResponseMessage][SERVER UNAVAILABLE][" + responseMessage.getError().getMethodType()
					+ "] HTTP Status 503 : synchronization error ");

			// Log the error
			logger.error(
					"Impossible de contacter l'application " + synchronizationSubscription.getApplication().getName()
							+ ". La synchronisation de la ressource d'id : " + resource.getId() + " a echoue.");

			// Manage the synchronization error in order to know what we must do
			this.manageSynchronizationError(responseMessage, resource, synchronizationSubscription,
					this.synchronizationJobType, null);
			break;
		}
	}

	/**
	 * Check the error code type contained into the response message returned by the
	 * web service client
	 * 
	 * @param responseMessage             : the response returned by the WS client
	 * @param resource                    : the {@link Resource} synchronized
	 * @param synchronizationSubscription : the {@link synchronizationSubscription}
	 *                                    concerned by the synchronization
	 * @param synchronizationJobType      : the action to perform on the distant
	 *                                    application (CREATE,UPDATE, DELETE...)
	 */
	protected void checkResponseMessageErrorCodeType(ResponseMessage responseMessage, Resource resource,
			SynchronizationSubscription synchronizationSubscription, SynchronizationJobType synchronizationJobType) {
		logger.debug("Starting checkResponseMessageErrorCodeType");
		ErrorCodeType errorCodeType = responseMessage.getError().getErrorCode();

		if (errorCodeType != null) {
			// check error code type contained into the response message
			switch (errorCodeType) {
			case SYNTAX_ERROR:
				// Http code : 400, error code type : 001 (SYNTAX ERROR)
				logger.info("[ERROR] 400 [ERROR_CODE_TYPE] 001 : syntax error");
				// Manage the synchronization error
				this.manageSynchronizationError(responseMessage, resource, synchronizationSubscription,
						synchronizationJobType, ErrorCodeType.SYNTAX_ERROR);
				break;

			case FORMAT_ERROR:
				// Http code : 400, error code type : 002
				logger.info("[ERROR] 400 [ERROR_CODE_TYPE] 002 : Format error");
				// Manage the synchronization error
				this.manageSynchronizationError(responseMessage, resource, synchronizationSubscription,
						synchronizationJobType, ErrorCodeType.FORMAT_ERROR);
				break;

			case RESOURCE_MISSING:
				// Http code : 404, error code type : 003
				// In this case the error returned is not important because we
				// wanted to delete the resource
				logger.info("[INFO] 404 [ERROR_CODE_TYPE] 003 : Resource missing");
				break;

			case RELATION_MISSING:
				// Http code : 404, error code type : 004
				logger.info("[ERROR] 404 [ERROR_CODE_TYPE] 004 : Relation missing");

				// We resynchronize the resource missing
				this.reSynchronizeResourceDependency(responseMessage, synchronizationSubscription);
				// Then we manage the synchronization error of the actual
				// resource
				this.manageSynchronizationError(responseMessage, resource, synchronizationSubscription,
						synchronizationJobType, ErrorCodeType.RELATION_MISSING);
				break;

			case RESOURCE_ALREADY_EXISTS:
				// Http code : 400, error code type : 005
				logger.info("[ERROR] 400 [ERROR_CODE_TYPE] 005 : Resource already exist");
				// As the resource already exists we send a PUT to ensure that
				// the resource is updated
				responseMessage.getError().setMethodType(MethodType.PUT);
				this.manageSynchronizationError(responseMessage, resource, synchronizationSubscription,
						SynchronizationJobType.UPDATE, ErrorCodeType.RESOURCE_ALREADY_EXISTS);

				break;

			case INTERNAL_SERVER_ERROR:
				// Http code : 500, error code type : 006
				logger.info("[ERROR] 500 [ERROR_CODE_TYPE] 006 : Internal server errror");

				logger.error("La synchronisation de la ressource d'id : " + resource.getId() + " vers l'application "
						+ synchronizationSubscription.getApplication().getName()
						+ " a provoque une erreur interne au sein de l'application "
						+ synchronizationSubscription.getApplication().getName());

				// Manage the synchronization error in order to know what we
				// must do
				this.manageSynchronizationError(responseMessage, resource, synchronizationSubscription,
						synchronizationJobType, null);
				break;

			case INTERNAL_CLIENT_ERROR:
				// Http code : 400, error code type : 007
				logger.info("[ERROR] 400 [ERROR_CODE_TYPE] 007 : Internal client errror");

				logger.error("La synchronisation de la ressource d'id : " + resource.getId() + " vers l'application "
						+ synchronizationSubscription.getApplication().getName()
						+ " a provoque une erreur au niveau du client webservice");

				// Manage the synchronization error in order to know what we
				// must do
				this.manageSynchronizationError(responseMessage, resource, synchronizationSubscription,
						synchronizationJobType, ErrorCodeType.INTERNAL_CLIENT_ERROR);
				break;
			}
		} else {
			logger.error("Impossible to continue handling errors because errorCodeType is null");
		}
	}

	/**
	 * Check if this resource is contained in the synchronization table of the
	 * database. Delete the corresponding synchronizationError row in this case
	 * 
	 * @param resource : the resource synchronized
	 */
	public void manageSynchronizationSuccess(Resource resource) {
		logger.debug("Starting manageSynchronizationSuccess");
		if (this.synchronizationErrorId != null) {
			synchronizationErrorService.deleteById(synchronizationErrorId);
		}
	}

	/**
	 * Create and return new synchronization error (number of attempts = 1)
	 * 
	 * @param resource               : the {@link Resource} synchronized
	 * @param synchronizationJobType : the action to perform on the distant
	 *                               application (CREATE,UPDATE, DELETE...)
	 */
	public SynchronizationError createNewSynchronizationError(Resource resource,
			SynchronizationJobType synchronizationJobType) {
		logger.debug("Starting createNewSynchronizationError");
		// Create new SynchronizationError
		SynchronizationError newSynchronizationError = new SynchronizationError();

		newSynchronizationError.setMethodType(this.convertSynchronizationJobTypeToMethodType(synchronizationJobType));
		newSynchronizationError.setNbAttempts(1);
		newSynchronizationError.setResourceId(resource.getId());

		// Persist SynchronizationError into the database
		this.synchronizationErrorService.create(newSynchronizationError);

		return newSynchronizationError;
	}

	/**
	 * Check the synchronization error and increase the number of attempts if less
	 * than 5
	 * 
	 * @param synchronizationJobType      : the action to perform on the distant
	 *                                    application (CREATE,UPDATE, DELETE...)
	 * @param resource                    : the {@link Resource} synchronized
	 * @param synchronizationSubscription : the {@link synchronizationSubscription}
	 *                                    concerned by the synchronization
	 */
	public void checkSynchronizationError(SynchronizationJobType synchronizationJobType, Resource resource,
			SynchronizationSubscription synchronizationSubscription, ErrorCodeType errorCodeType) {
		logger.debug("Starting checkSynchronizationError");
		// Find SynchronizationError from database
		Long synchronisationErrorId = new Long(this.synchronizationErrorId);
		SynchronizationError newSynchronizationError = synchronizationErrorService.findOne(synchronisationErrorId);

		// check number of attempts. If synchronization error > at the maximum
		// defined in the properties file we delete it
		if (synchronizationErrorService.findOne(newSynchronizationError.getId())
				.getNbAttempts() >= MyEc3SynchroConstants.MAX_ATTEMPTS) {
			synchronizationErrorService.delete(newSynchronizationError);
			logger.error("The error handling of resource : " + resource.getName() + ", id : " + resource.getId()
					+ " have failed " + MyEc3SynchroConstants.MAX_ATTEMPTS
					+ " times. So the resource is not synchronized correctly with the application "
					+ synchronizationSubscription.getApplication().getName());
		} else {
			// Update synchronization error. Increment the number of attempts
			newSynchronizationError.setNbAttempts(newSynchronizationError.getNbAttempts() + 1);

			synchronizationErrorService.update(newSynchronizationError);

			// Create new delayed job
			this.createNewDelayedSynchronizationJob(newSynchronizationError, synchronizationJobType, resource,
					synchronizationSubscription,
					this.getDelay(newSynchronizationError, responseMessage.getHttpStatus(), errorCodeType));
		}
	}

	/**
	 * Create new synchronization error and lunch a new delayed job if it's the
	 * first time that the error occured.
	 * 
	 * @param responseMessage             : the response returned by the web service
	 *                                    client
	 * @param resource                    : the {@link Resource} synchronized
	 * @param synchronizationSubscription : the {@link SynchronizationSubscription}
	 *                                    concerned by the synchronization
	 * @param synchronizationJobType      : the action to perform on the distant
	 *                                    application (CREATE,UPDATE, DELETE...)
	 * @param errorCodeType               : the error code returned by the distant
	 *                                    application
	 */
	public void manageSynchronizationError(ResponseMessage responseMessage, Resource resource,
			SynchronizationSubscription synchronizationSubscription, SynchronizationJobType synchronizationJobType,
			ErrorCodeType errorCodeType) {
		logger.debug("Starting manageSynchronizationError");
		// if it is the first time the error occurs for this synchronization job
		if (this.synchronizationErrorId == null) {
			// Create new delayed job
			SynchronizationError newSynchronizationError = createNewSynchronizationError(resource,
					synchronizationJobType);

			this.createNewDelayedSynchronizationJob(newSynchronizationError, synchronizationJobType, resource,
					synchronizationSubscription,
					this.getDelay(newSynchronizationError, responseMessage.getHttpStatus(), errorCodeType));
		} else {
			// check the number of attempts of the synchronization error
			this.checkSynchronizationError(synchronizationJobType, resource, synchronizationSubscription,
					errorCodeType);
		}
	}

	/**
	 * Return the delay to use before send and other request depending on the
	 * synchronizationError, HttpStatus and the errorCodeType. Possible values are
	 * defined into the file synchronization.properties !
	 * 
	 * @param synchronizationError : the {@link SynchronizationError} concerning the
	 *                             resource to synchronize
	 * @param httpStatus           : the HttpStatus returned by the distant
	 *                             application concerned by the synchronization of
	 *                             the resource
	 * @param errorCodeType        : the error code returned by the distant
	 *                             application
	 */
	public Long getDelay(SynchronizationError synchronizationError, HttpStatus httpStatus,
			ErrorCodeType errorCodeType) {
		logger.debug("Starting getDelay");
		// HTTP status between 400 and 405
		if ((httpStatus.getValue() >= HttpStatus.BAD_REQUEST.getValue())
				&& (httpStatus.getValue() < HttpStatus.INTERNAL_SERVER_ERROR.getValue())) {

			// If it's an error with HTTP status = 404 and errorcode = null
			// Its in the case of the resynchronization of a missing resource
			// (404, 004)
			if (errorCodeType == null) {
				if (synchronizationError.getNbAttempts() > MyEc3SynchroConstants.HTTP_CODE_400_DELAY.length) {
					return MyEc3SynchroConstants.HTTP_CODE_400_DELAY[MyEc3SynchroConstants.HTTP_CODE_400_DELAY.length
							- 1];
				} else {
					return MyEc3SynchroConstants.HTTP_CODE_400_DELAY[synchronizationError.getNbAttempts() - 1];
				}
			}

			// If it's an error with HTTP status = 400 and errorcode = 007
			// Its in the case of internal client exception
			if (errorCodeType.equals(ErrorCodeType.INTERNAL_CLIENT_ERROR)) {
				if (synchronizationError.getNbAttempts() > MyEc3SynchroConstants.HTTP_CODE_500_DELAY.length) {
					return MyEc3SynchroConstants.HTTP_CODE_500_DELAY[MyEc3SynchroConstants.HTTP_CODE_500_DELAY.length
							- 1];
				} else {
					return MyEc3SynchroConstants.HTTP_CODE_500_DELAY[synchronizationError.getNbAttempts() - 1];
				}
			}

			if (!errorCodeType.equals(ErrorCodeType.RELATION_MISSING)) {
				if (synchronizationError.getNbAttempts() > MyEc3SynchroConstants.HTTP_CODE_400_DELAY.length) {
					return MyEc3SynchroConstants.HTTP_CODE_400_DELAY[MyEc3SynchroConstants.HTTP_CODE_400_DELAY.length
							- 1];
				} else {
					return MyEc3SynchroConstants.HTTP_CODE_400_DELAY[synchronizationError.getNbAttempts() - 1];
				}
			} else {
				if (synchronizationError
						.getNbAttempts() > MyEc3SynchroConstants.HTTP_CODE_404_ERROR_CODE_404_DELAY.length) {
					return MyEc3SynchroConstants.HTTP_CODE_404_ERROR_CODE_404_DELAY[MyEc3SynchroConstants.HTTP_CODE_404_ERROR_CODE_404_DELAY.length
							- 1];
				} else {
					return MyEc3SynchroConstants.HTTP_CODE_404_ERROR_CODE_404_DELAY[synchronizationError.getNbAttempts()
							- 1];
				}
			}
		}

		// HTTP status 500 or 503
		if (httpStatus.getValue() >= HttpStatus.INTERNAL_SERVER_ERROR.getValue()) {
			if (synchronizationError.getNbAttempts() > MyEc3SynchroConstants.HTTP_CODE_500_DELAY.length) {
				return MyEc3SynchroConstants.HTTP_CODE_500_DELAY[MyEc3SynchroConstants.HTTP_CODE_500_DELAY.length - 1];
			} else {
				return MyEc3SynchroConstants.HTTP_CODE_500_DELAY[synchronizationError.getNbAttempts() - 1];
			}
		}

		return null;
	}

	/**
	 * Create a new delayed synchronization job in case of error, in order to
	 * resynchronize correctly the {@link Resource} on the distant application.
	 * 
	 * @param synchronizationError        : the {@link SynchronizationError}
	 *                                    concerning the resource to synchronize
	 * @param synchronizationJobType      : the action to perform on the distant
	 *                                    application (CREATE,UPDATE, DELETE...)
	 * @param resource                    : the {@link Resource} to resynchronize
	 * @param synchronizationSubscription : the {@link SynchronizationSubscription}
	 *                                    concerned by the error and which must be
	 *                                    resynchronize
	 * @param delay                       : the delay to use before sending a new
	 *                                    request
	 * 
	 * @see SchudulerServiceImpl.java
	 */
	public void createNewDelayedSynchronizationJob(SynchronizationError synchronizationError,
			SynchronizationJobType synchronizationJobType, Resource resource,
			SynchronizationSubscription synchronizationSubscription, Long delay) {
		logger.info("Starting createNewDelayedSynchronizationJob");
		logger.info("Creating new delayed Synchronization Job");
		schedulerService.addDelayedResourceTrigger(this.initialSynchronizationId, synchronizationError, resource,
				this.getProfileListRoles(), synchronizationSubscription, synchronizationJobType, delay,
				SynchronizationType.ERROR_HANDLING, this.getSendingApplication());
	}

	/**
	 * Resynchronize a {@link Resource} dependency when an error 404 with error code
	 * 004 occures (RELATION MISSING). We must send the {@link Resource} missing
	 * before resynchronize the main {@link Resource}. ie : To create an
	 * {@link Employee} the distant application must know his {Company}. If the
	 * distant application doesn't know the company of the employee we must send the
	 * company and AFTER the employee!
	 * 
	 * @param responseMessage             : the response returned by the WS client
	 * @param synchronizationSubscription : the {@link SynchronizationSubscription}
	 *                                    concerned by the synchronization
	 */
	@SuppressWarnings("unchecked")
	public void reSynchronizeResourceDependency(ResponseMessage responseMessage,
			SynchronizationSubscription synchronizationSubscription) {
		logger.debug("Starting reSynchronizeResourceDependency");
		// Get the resource dependency through the resource id contained in the
		// response message
		if ((responseMessage.getError().getResourceId() != null)
				&& (responseMessage.getError().getClassType() != null)) {
			Resource resourceDependency = null;
			try {
				// Retrieve the business service corresponding
				resourceDependency = (Resource) this.getResourceService(responseMessage.getError().getClassType())
						.findOne(responseMessage.getError().getResourceId());

			} catch (Exception ex) {
				logger.error("[reSynchronizeResourceDependency] impossible to execute method findById with id : "
						+ responseMessage.getError().getResourceId() + " " + ex.getCause() + " " + ex.getMessage());
			}
			if (resourceDependency != null) {
				// Populate resource dependency collections
				this.populateCollectionsOfResourceDependency(resourceDependency,
						responseMessage.getError().getClassType());

				// We hide some values that we must not be submited into the xml
				this.hideValuesOfResource(resourceDependency, synchronizationSubscription,
						responseMessage.getError().getClassType());

				// Get the synchronization subscription of the resource
				// dependency
				SynchronizationSubscription newSynchronizationSubscription = synchronizationSubscriptionService
						.findByResourceTypeAndApplicationId(this.getResourceType(resourceDependency),
								synchronizationSubscription.getApplication().getId());

				if (newSynchronizationSubscription == null) {
					logger.error("Impossible to resynchronize resourceDependency with ID : "
							+ resourceDependency.getId() + " because no application is subscribed to this resource");
				} else {
					// We manage the resource dependency synchronization error
					this.manageResourceDependencySynchronizationError(responseMessage, resourceDependency,
							newSynchronizationSubscription, SynchronizationJobType.UPDATE);
				}
			} else {
				logger.error("[reSynchronizeResourceDependency] Application : "
						+ synchronizationSubscription.getApplication().getName()
						+ " will be not synchronized because no " + responseMessage.getError().getClassType().getLabel()
						+ " was found with id = " + responseMessage.getError().getResourceId());
			}
		} else {
			logger.error(
					"Impossible to resynchronize resourceDependency because the resource id or classType contained in the response message is NULL : resourceiD : "
							+ responseMessage.getError().getResourceId() + ", classType : "
							+ responseMessage.getError().getClassType());
		}
	}

	/**
	 * Manage the synchronization error of a resource dependency.
	 * 
	 * @param responseMessage             : the response returned by the WS client
	 * @param resource                    : the dependendy of the main resource to
	 *                                    synchronize
	 * @param synchronizationSubscription : the {@link SynchronizationSubscription}
	 *                                    concerned by the synchronization
	 * @param synchronizationJobType      : the action to perform on the distant
	 *                                    application (CREATE,UPDATE, DELETE...)
	 */
	public void manageResourceDependencySynchronizationError(ResponseMessage responseMessage, Resource resource,
			SynchronizationSubscription synchronizationSubscription, SynchronizationJobType synchronizationJobType) {
		logger.debug("Starting manageResourceDependencySynchronizationError");
		// we check if it's the first time error for this resource dependency
		if ((this.synchronizationErrorId == null)
				|| ((this.synchronizationErrorId != null)
						&& (!this.synchronizationJobType.equals(synchronizationJobType)))
				|| ((this.synchronizationErrorId != null)
						&& (this.synchronizationJobType.equals(synchronizationJobType))
						&& (!(this.resource.getClass().equals(resource.getClass()))))) {

			SynchronizationError newSynchronizationError = createNewSynchronizationError(resource,
					synchronizationJobType);

			// Create new delayed job
			this.createNewDelayedSynchronizationJob(newSynchronizationError, synchronizationJobType, resource,
					synchronizationSubscription,
					this.getDelay(newSynchronizationError, responseMessage.getHttpStatus(), null));
		} else {
			this.checkSynchronizationError(synchronizationJobType, resource, synchronizationSubscription, null);
		}
	}

	/**
	 * Populate collections of resource dependency before replay the synchronization
	 */
	public void populateCollectionsOfResourceDependency(Resource resourceDependency, ClassType classType) {
		Assert.notNull(resourceDependency, "resourceDependency cannot be null");
		Assert.notNull(classType, "classType cannot be null");
		// We populate resource dependency's collection depending on its
		// resourceType
		switch (classType) {
		case ADMIN_PROFILE:
			this.adminProfileService.populateCollections((AdminProfile) resourceDependency);
			break;
		case AGENT_PROFILE:
			this.agentProfileService.populateCollections((AgentProfile) resourceDependency);
			break;
		case EMPLOYEE_PROFILE:
			this.employeeProfileService.populateCollections((EmployeeProfile) resourceDependency);
			break;
		case CUSTOMER:
			break;
		case ORGANISM:
			this.organismService.populateCollections((Organism) resourceDependency);
			break;
		case ORGANISM_DEPARTMENT:
			this.organismDepartmentService.populateCollections((OrganismDepartment) resourceDependency);
			break;
		case COMPANY:
			this.companyService.populateCollections((Company) resourceDependency);
			break;
		case COMPANY_DEPARTMENT:
			this.companyDepartmentService.populateCollections((CompanyDepartment) resourceDependency);
			break;
		case ESTABLISHMENT:
			this.establishmentService.populateCollections((Establishment) resourceDependency);
		}
	}

	/**
	 * This method allows to hide some values that musn't be present into the XML to
	 * send. This method is called only in case of RELATION_MISSING by method
	 * reSynchronizeResourceDependency.
	 * 
	 * @param resourceDependency : the ressource missing to synchronize before
	 *                           synchronize the main resource
	 * @param classType          : the ressource missing class
	 */
	public void hideValuesOfResource(Resource resourceDependency,
			SynchronizationSubscription synchronizationSubscription, ClassType classType) {
		Assert.notNull(resourceDependency, "resourceDependency cannot be null");
		Assert.notNull(classType, "classType cannot be null");

		switch (classType) {
		case ADMIN_PROFILE:
			if (resourceDependency instanceof AdminProfile) {
				this.synchronizationFilterService.filter((AdminProfile) resourceDependency,
						synchronizationSubscription);
			}
			break;
		case AGENT_PROFILE:
			if (resourceDependency instanceof AgentProfile) {
				this.synchronizationFilterService.filter((AgentProfile) resourceDependency,
						synchronizationSubscription);
			}
			break;
		case EMPLOYEE_PROFILE:
			if (resourceDependency instanceof EmployeeProfile) {
				this.synchronizationFilterService.filter((EmployeeProfile) resourceDependency,
						synchronizationSubscription);
			}
			break;
		case CUSTOMER:
			if (resourceDependency instanceof Customer) {
				this.synchronizationFilterService.filter((Customer) resourceDependency, synchronizationSubscription);
			}
			break;
		case ORGANISM:
			if (resourceDependency instanceof Organism) {
				this.synchronizationFilterService.filter((Organism) resourceDependency, synchronizationSubscription);
			}
			break;
		case COMPANY:
			if (resourceDependency instanceof Company) {
				this.synchronizationFilterService.filter((Company) resourceDependency, synchronizationSubscription);
			}
			break;
		case ORGANISM_DEPARTMENT:
			if (resourceDependency instanceof OrganismDepartment) {
				this.synchronizationFilterService.filter((OrganismDepartment) resourceDependency,
						synchronizationSubscription);
			}
			break;
		case COMPANY_DEPARTMENT:
			if (resourceDependency instanceof CompanyDepartment) {
				this.synchronizationFilterService.filter((CompanyDepartment) resourceDependency,
						synchronizationSubscription);
			}
			break;
		case ESTABLISHMENT:
			if (resourceDependency instanceof Establishment) {
				this.synchronizationFilterService.filter((Establishment) resourceDependency,
						synchronizationSubscription);
			}
		}
	}

	/**
	 * Convert a MethodType to an SynchronizationJobType
	 * 
	 * @param MethodType : the method type to convert
	 * @return SynchronizationJobType : the {@link SynchronizationJobType}
	 *         corresponding at the given {@link MethodType}
	 */
	public SynchronizationJobType convertMethodTypeToSynchronizationJobType(MethodType methodType) {
		logger.debug("Starting convertMethodTypeToSynchronizationJobType");
		Assert.notNull(methodType, "methodType cannot be null");
		switch (methodType) {
		case POST:
			return SynchronizationJobType.CREATE;
		case PUT:
			return SynchronizationJobType.UPDATE;
		case DELETE:
			return SynchronizationJobType.DELETE;
		}
		return null;
	}

	/**
	 * Convert a SynchronizationJobType to an MethodType
	 * 
	 * @param synchronizationJobType : the {@link SynchronizationJobType} to convert
	 * @return MethodType : the {@link MethodType} corresponding at the given
	 *         {@link SynchronizationJobType}
	 */
	public MethodType convertSynchronizationJobTypeToMethodType(SynchronizationJobType synchronizationJobType) {
		Assert.notNull(synchronizationJobType, "synchronizationJobType cannot be null");
		logger.debug("Starting convertSynchronizationJobTypeToMethodType");
		switch (synchronizationJobType) {
		case CREATE:
			return MethodType.POST;
		case UPDATE:
			return MethodType.PUT;
		case DELETE:
			return MethodType.DELETE;
		}
		return null;
	}

	/**
	 * Return the {@link ResourceType} of a given {@link Resource}
	 * 
	 * @param resource : the given {@link Resource}
	 * @return ResourceType : the {@link ResourceType} corresponding at the given
	 *         {@link Resource}
	 */
	public ResourceType getResourceType(Resource resource) {
		logger.debug("Starting getResourceType");
		Assert.notNull(resource, "resource cannot be null");
		if (resource != null) {
			logger.debug("Starting getResourceType with resource : " + resource.getName());
			if (Organism.class.equals(resource.getClass())) {
				return ResourceType.ORGANISM;
			}
			if (OrganismDepartment.class.equals(resource.getClass())) {
				return ResourceType.ORGANISM_DEPARTMENT;
			}
			if (Company.class.equals(resource.getClass())) {
				return ResourceType.COMPANY;
			}
			if (CompanyDepartment.class.equals(resource.getClass())) {
				return ResourceType.COMPANY_DEPARTMENT;
			}
			if (Establishment.class.equals(resource.getClass())) {
				return ResourceType.ESTABLISHMENT;
			}
			if (AdminProfile.class.equals(resource.getClass())) {
				return ResourceType.ADMIN_PROFILE;
			}
			if (AgentProfile.class.equals(resource.getClass())) {
				return ResourceType.AGENT_PROFILE;
			}
			if (EmployeeProfile.class.equals(resource.getClass())) {
				return ResourceType.EMPLOYEE_PROFILE;
			}
			if (Customer.class.equals(resource.getClass())) {
				return ResourceType.CUSTOMER;
			}

			// If the resource concrete type is not one of those described
			// behind,
			// We don't really know how to resynchronize it, so we throw an
			// Exception
			logger.error(getErrorMessage(resource.getClass().toString()));
			throw new NoClassDefFoundError(getErrorMessage(resource.getClass().toString()));
		} else {
			logger.error("[getResourceType] resource is null");
		}
		return null;
	}

	/**
	 * Display all informations about the error to the console
	 * 
	 * @param responseMessage             : the response returned by the WS client
	 * @param synchronizationSubscription : the {@link SynchronizationSubscription}
	 *                                    concerned by the synchronization
	 */
	public void logErrorContainedInResponseMessage(ResponseMessage responseMessage,
			SynchronizationSubscription synchronizationSubscription) {
		logger.error("[logErrorInResponseMessage]");

		logger.error("[HTTP STATUS] : " + responseMessage.getHttpStatus());
		logger.error("[SENDING APPLICATION] : " + this.getSendingApplication());
		logger.error("[APPLICATION SYNCHRONIZED] : " + synchronizationSubscription.getApplication().getName());
		logger.error("[APPLICATION URI] : " + synchronizationSubscription.getUri());
		logger.error("[RESOURCE TYPE] : " + synchronizationSubscription.getResourceLabel().toString());
		logger.error("[RESOURCE ID] : " + responseMessage.getError().getResourceId());
		logger.error("[METHOD TYPE] : " + responseMessage.getError().getMethodType());
		logger.error("[ERROR CODE] : " + responseMessage.getError().getErrorCode());
		logger.error("[ERROR LABEL] : " + responseMessage.getError().getErrorLabel());
		logger.error("[ERROR MESSAGE] : " + responseMessage.getError().getErrorMessage());
	}

	/**
	 * Persist all the informations about the synchronization into the database
	 * 
	 * @param resource                    : the {@link Resource} synchronized
	 * @param responseMessage             : the response returned by the WS client
	 * @param synchronizationSubscription : the {@link SynchronizationSubscription}
	 *                                    concerned by the synchronization
	 */
	public void manageSynchronizationLog(Resource resource, ResponseMessage responseMessage,
			SynchronizationSubscription synchronizationSubscription) {
		logger.debug("Starting manageSynchronizationLog");
		SynchronizationError newSynchronizationError = new SynchronizationError();

		// Create new synchronizationLog
		SynchronizationLog synchronizationLog = new SynchronizationLog();
		synchronizationLog.setResourceId(resource.getId());
		synchronizationLog.setResourceType(this.getResourceType(resource));
		synchronizationLog.setHttpCode(responseMessage.getHttpStatus().getValue());
		synchronizationLog.setHttpStatus(responseMessage.getHttpStatus());
		synchronizationLog.setMethodType(this.convertSynchronizationJobTypeToMethodType(this.synchronizationJobType));
		synchronizationLog.setSynchronizationDate(new Date());
		synchronizationLog.setSynchronizationSubscription(synchronizationSubscription);
		synchronizationLog.setApplicationName(synchronizationSubscription.getApplication().getName());
		synchronizationLog.setSynchronizationType(this.getSynchronizationType());
		synchronizationLog.setSendingApplication(this.getSendingApplication());

		// if it's the first time that the synchronization has been triggered
		if (this.synchronizationErrorId == null) {
			synchronizationLog.setNbAttempts(0);
			synchronizationLog.setIsFinal(this.checkIfSynchronizationIsFinal(synchronizationLog, responseMessage));
		} else {
			// get the synchronization error
			newSynchronizationError = synchronizationErrorService.findOne(this.synchronizationErrorId);

			// Set number of attempts of the synchronization error
			synchronizationLog.setNbAttempts(newSynchronizationError.getNbAttempts());

			// Set isFinal value
			if (newSynchronizationError.getNbAttempts() >= MyEc3SynchroConstants.MAX_ATTEMPTS) {
				synchronizationLog.setIsFinal(Boolean.TRUE);
			} else {
				synchronizationLog.setIsFinal(Boolean.FALSE);
			}

		}

		// In case of an error
		if (responseMessage.getHttpStatus().getValue() > HttpStatus.NO_CONTENT.getValue()) {
			synchronizationLog.setResponseMessageResourceId(responseMessage.getError().getResourceId());
			synchronizationLog.setErrorCodeType(responseMessage.getError().getErrorCode());
			synchronizationLog.setErrorLabel(responseMessage.getError().getErrorLabel());
			synchronizationLog.setErrorMessage(responseMessage.getError().getErrorMessage());
			synchronizationLog.setStatut(ERROR_STATUT);
		} else {
			// In case of success
			synchronizationLog.setIsFinal(Boolean.TRUE);
			synchronizationLog.setStatut(SUCCESS_STATUT);
		}

		try {
			// persist the synchronizationLog
			synchronizationLogService.create(synchronizationLog);
		} catch (RuntimeException ex) {
			// Internal server error during create synchronizationLog into the
			// database
			logger.error("[manageSynchronizationLog] creation error : " + ex);
			logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
			logger.error("[SENDING APPLICATION]: " + synchronizationLog.getSendingApplication());
			logger.error("[APPLICATION NAME]: " + synchronizationLog.getApplicationName());
			logger.error("[ERROR LABEL]: " + synchronizationLog.getErrorLabel());
			logger.error("[ERROR MESSAGE]: " + synchronizationLog.getErrorMessage());
			logger.error("[HTTP CODE]: " + synchronizationLog.getHttpCode());
			logger.error("[NB ATTEMPTS]: " + synchronizationLog.getNbAttempts());
			logger.error("[STATUT]: " + synchronizationLog.getStatut());
			logger.error("[RESOURCE ID]: " + synchronizationLog.getId());
			logger.error("[RESOURCE RELATION ID]: " + synchronizationLog.getResponseMessageResourceId());
			logger.error("[ERROR CODE TYPE]: " + synchronizationLog.getErrorCodeType());
			logger.error("[IS FINAL]: " + synchronizationLog.getIsFinal());
			logger.error("[METHOD TYPE]: " + synchronizationLog.getMethodType());
			logger.error("[RESOURCE TYPE]: " + synchronizationLog.getResourceType());
			logger.error("[SYNCHRONIZATION DATE]: " + synchronizationLog.getSynchronizationDate().toString());
			logger.error("[SYNCHRONIZATION TYPE]: " + synchronizationLog.getSynchronizationType().getValue());
		}

		// Manage synchronizationInitial
		this.manageSynchronizationInitial(synchronizationLog);
	}

	/**
	 * This method allows to save the initial synchronization in order to know all
	 * synchronizations attached at the first synchronization
	 * 
	 * @param synchronizationLog : all informations about the current
	 *                           synchronization task
	 */
	public void manageSynchronizationInitial(SynchronizationLog synchronizationLog) {
		logger.debug("Starting manageSynchronizationInitial");

		if (synchronizationLog != null) {

			// Create new Synchronization initial
			SynchronizationInitial newSynchronizationInitial = new SynchronizationInitial();

			// if it's the first time that the synchronization has been
			// triggered
			// we get the id of synchronization log in order to know what is the
			// id of the first synchronization

			if ((this.synchronizationErrorId == null) && (this.initialSynchronizationId == null)) {
				// We set the initial synchronization id attribute
				this.initialSynchronizationId = synchronizationLog.getId();
				newSynchronizationInitial.setInitialSynchronizationId(synchronizationLog.getId());
			} else {
				newSynchronizationInitial.setInitialSynchronizationId(this.initialSynchronizationId);
			}

			newSynchronizationInitial.setSynchronizationLog(synchronizationLog);
			synchronizationInitialService.create(newSynchronizationInitial);
		} else {
			logger.error(
					"[manageSynchronizationInitial] Impossible to save initial synchronization because synchronizalog sent is null");
		}
	}

	/**
	 * This method check if it's the last time that the synchronization is triggered
	 * 
	 * @param synchronizationLog : all informations about the current
	 *                           synchronization task
	 * @param responseMessage    : the response returned by the WS client
	 * 
	 * @return true if it's the last time that the synchronization is triggered
	 */
	public Boolean checkIfSynchronizationIsFinal(SynchronizationLog synchronizationLog,
			ResponseMessage responseMessage) {
		logger.debug("Starting checkIfSynchronizationIsFinal");
		// If it's an HTTP code = 200 or 201
		if (responseMessage.getHttpStatus().getValue() <= HttpStatus.NO_CONTENT.getValue()) {
			return Boolean.TRUE;
		}

		// if it's an HTTP code = 401
		if (responseMessage.getHttpStatus().equals(HttpStatus.UNAUTHORIZED)) {
			return Boolean.TRUE;
		}

		// if it's an HTTP code = 403
		if (responseMessage.getHttpStatus().equals(HttpStatus.FORBIDDEN)) {
			return Boolean.TRUE;
		}

		// if it's an HTTP code = 405
		if (responseMessage.getHttpStatus().equals(HttpStatus.METHOD_NOT_ALLOWED)) {
			return Boolean.TRUE;
		}

		if (responseMessage.getError() != null) {
			if (responseMessage.getError().getErrorCode() != null) {
				// if it's an HTTP code = 404 with error code =003
				// (RESOURCE_MISSING)
				if (((responseMessage.getHttpStatus().equals(HttpStatus.NOT_FOUND))
						&& (responseMessage.getError().getErrorCode().equals(ErrorCodeType.RESOURCE_MISSING)))) {
					return Boolean.TRUE;
				}

				// if it's an HTTP code = 400 with error code =005
				// (RESOURCE_ALREADY_EXISTS)
				if ((responseMessage.getHttpStatus().equals(HttpStatus.BAD_REQUEST))
						&& (responseMessage.getError().getErrorCode().equals(ErrorCodeType.RESOURCE_ALREADY_EXISTS))) {
					return Boolean.TRUE;
				}
			} else {
				logger.error("Attribute ErrorCode of responseMessage is null, impossible to continue handling error");
			}
		} else {
			logger.error("Object Error of responseMessage is null, impossible to continue handling error");
		}
		return Boolean.FALSE;
	}

	/**
	 * Private method used to build the error message depending of the resource
	 * concrete class
	 * 
	 * @param className : name of the resource concrete class
	 * @return the complete error message
	 */
	private String getErrorMessage(String className) {
		StringBuilder builder = new StringBuilder();
		builder.append("No matching Resource type found for class '").append(className).append(
				"' !! Synchronization error cannot be resolved. Please consider adding a corresponding Resource type.");
		return builder.toString();
	}

	/**
	 * This method allows to return the correct business service depending on the
	 * concrete resource's class
	 * 
	 * @param classType : the resource's class
	 * @return the correct business service depending on {@link ClassType}
	 */
	@SuppressWarnings("rawtypes")
	public ResourceService getResourceService(ClassType classType) {
		logger.debug("Enterring in method getResourceService with classType : " + classType);

		switch (classType) {
		case ADMIN_PROFILE:
			return adminProfileService;
		case AGENT_PROFILE:
			return agentProfileService;
		case EMPLOYEE_PROFILE:
			return employeeProfileService;
		case CUSTOMER:
			return customerService;
		case ORGANISM:
			return organismService;
		case ORGANISM_DEPARTMENT:
			return organismDepartmentService;
		case COMPANY:
			return companyService;
		case COMPANY_DEPARTMENT:
			return companyDepartmentService;
		}
		logger.error("no resource service was found for this type of resource : " + classType);
		return null;
	}
}
