package org.myec3.socle.webapp.pages.synchroman.synchronization;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.CompanyDepartmentService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.ResourceService;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.synchro.api.SynchronizationService;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationInitial;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;
import org.myec3.socle.synchro.core.service.SynchronizationInitialService;
import org.myec3.socle.synchro.core.service.SynchronizationLogService;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class DetailSynchronization extends AbstractPage {

	private static Logger logger = LogManager.getLogger(DetailSynchronization.class);

	// Template attributes
	private SynchronizationLog synchronizationLog;

	private Resource resource;

	@Inject
	@Service("synchronizationCoreService")
	private SynchronizationService synchronizationService;

	@Inject
	@Service("agentProfileService")
	private AgentProfileService agentProfileService;

	@Inject
	@Service("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	@Inject
	@Service("organismService")
	private OrganismService organismService;

	@Inject
	@Service("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@Inject
	@Service("companyService")
	private CompanyService companyService;

	@Inject
	@Service("companyDepartmentService")
	private CompanyDepartmentService companyDepartmentService;

	@Persist
	private List<SynchronizationLog> synchronizationLogResult;

	@Persist("Flash")
	private String successMessage;

	@Inject
	private BeanModelSource beanModelSource;

	@Property
	private SynchronizationLog synchronizationLogRow;

	@SuppressWarnings("unused")
	@Property
	private Integer rowIndex;

	@Inject
	@Service("synchronizationLogService")
	private SynchronizationLogService synchronizationLogService;

	@Inject
	@Service("synchronizationInitialService")
	private SynchronizationInitialService synchronizationInitialService;

	@SuppressWarnings("unused")
	@Inject
	private PropertyConduitSource propertyConduitSource;

	@Inject
	private Messages messages;

	@Component
	private Grid synchronizationLogGrid;

	@SetupRender
	public void setupGrid() {
		synchronizationLogGrid.getSortModel().clear();
		synchronizationLogGrid.getSortModel().updateSort("synchronizationDate");
	}

	// Activate n Passivate
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return SearchResult.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		// Get synchronization from the database
		this.synchronizationLog = this.synchronizationLogService.findOne(id);

		if (null == this.synchronizationLog) {
			logger.info("Finding synchronization log failed");
			return Boolean.FALSE;
		}
		SynchronizationInitial synchronizationInitial = this.synchronizationInitialService
				.findBySynchronizationLog(this.synchronizationLog);

		if (synchronizationInitial != null) {
			// Get all synchronizations attached at this synchronization (same
			// initial synchronization id)
			this.synchronizationLogResult = this.synchronizationInitialService
					.findAllSynchronizationLogByInitialSynchronizationId(
							synchronizationInitial.getInitialSynchronizationId());

			// We remove the actual synchronizationLog
			this.synchronizationLogResult.remove(this.synchronizationLog);
		} else {
			// Empty result
			this.synchronizationLogResult = new ArrayList<SynchronizationLog>();
		}

		this.synchronizationLogResult = new ArrayList<SynchronizationLog>();

		logger.info("Finding synchronization log successful");
		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.synchronizationLog != null) ? this.synchronizationLog.getId() : null;
	}

	/**
	 * @return : bean model
	 */
	public BeanModel<SynchronizationLog> getGridModel() {
		BeanModel<SynchronizationLog> model = this.beanModelSource.createDisplayModel(SynchronizationLog.class,
				this.messages);
		model.add("actions", null);
		model.add("application", null);
		model.include("id", "application", "resourceType", "methodType", "synchronizationType", "httpStatus",
				"nbAttempts", "isFinal", "synchronizationDate", "statut", "actions");
		return model;
	}

	// TODO just desactivate not delete
	@OnEvent(value = "action", component = "replay")
	public Object replaySynchronization(Long id) {

		// Get synchronization log from the database
		this.synchronizationLog = this.synchronizationLogService.findOne(id);

		if (null == this.synchronizationLog) {
			logger.info("Finding synchronization log failed");
			return Boolean.FALSE;
		}
		logger.info("Finding synchronization log successful");

		// Get resource to synchronize from the database
		this.resource = (Resource) this.getResourceService(this.synchronizationLog.getResourceType())
				.findOne(this.synchronizationLog.getResourceId());

		if (null == this.resource) {
			logger.error("Finding resource failed");
			return Boolean.FALSE;
		}
		logger.info("Finding resource of type :" + this.resource.getClass() + " successful");

		List<Long> listApplicationIdToResynchronize = new ArrayList<Long>();
		listApplicationIdToResynchronize
				.add(this.synchronizationLog.getSynchronizationSubscription().getApplication().getId());

		if (listApplicationIdToResynchronize.size() > 0) {
			this.resynchronizeResource(resource, listApplicationIdToResynchronize);
		} else {
			logger.error("There are no application to resynchronize");
			return Boolean.FALSE;
		}

		this.successMessage = this.messages.get("replay-success");
		return this;
	}

	/**
	 * 
	 * @param resource
	 * @param listApplicationIdToResynchronize
	 */
	public void resynchronizeResource(Resource resource, List<Long> listApplicationIdToResynchronize) {
		switch (this.synchronizationLog.getMethodType()) {
		case POST:
			this.synchronizationService.propagateCreation(resource, listApplicationIdToResynchronize,
					SynchronizationType.RESYNCHRONIZATION, null);
			break;
		case PUT:
			this.synchronizationService.propagateUpdate(resource, listApplicationIdToResynchronize,
					SynchronizationType.RESYNCHRONIZATION, null);
			break;
		case DELETE:
			this.synchronizationService.propagateDeletion(resource, listApplicationIdToResynchronize,
					SynchronizationType.RESYNCHRONIZATION, null);
			break;
		}
	}

	/**
	 * 
	 * @param resourceType
	 * @return ResourceService
	 */
	@SuppressWarnings("unchecked")
	public ResourceService getResourceService(ResourceType resourceType) {
		logger.debug("Enterring in method getResourceService with resourceType : " + resourceType);

		switch (resourceType) {
		case AGENT_PROFILE:
			return agentProfileService;
		case EMPLOYEE_PROFILE:
			return employeeProfileService;
		case ORGANISM:
			return organismService;
		case ORGANISM_DEPARTMENT:
			return organismDepartmentService;
		case COMPANY:
			return companyService;
		case COMPANY_DEPARTMENT:
			return companyDepartmentService;
		}

		logger.error("no resource service was found for this type of resource : " + resourceType);

		return null;
	}

	public String getIsFinalLabel() {
		if (synchronizationLogRow.getIsFinal()) {
			return "TERMINEE";
		}
		return "EN COURS";
	}

	// Getters n Setters
	/**
	 * @return String
	 */
	public String getSuccessMessage() {
		return this.successMessage;
	}

	/**
	 * 
	 * @param message
	 */
	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}

	/**
	 * 
	 * @return SynchronizationLog
	 */
	public SynchronizationLog getSynchronizationLog() {
		return synchronizationLog;
	}

	/**
	 * 
	 * @param synchronizationLog
	 */
	public void setSynchronizationLog(SynchronizationLog synchronizationLog) {
		this.synchronizationLog = synchronizationLog;
	}

	public List<SynchronizationLog> getSynchronizationLogResult() {
		return synchronizationLogResult;
	}

	public void setSynchronizationLogResult(List<SynchronizationLog> synchronizationLogResult) {
		this.synchronizationLogResult = synchronizationLogResult;
	}

	public SimpleDateFormat getDateFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return dateFormat;
	}

	public String getRowClass() {
		if (synchronizationLogRow.getStatut().equals("ERROR")) {

			// Resource already exists 400, 005
			if ((synchronizationLogRow.getHttpCode() == HttpStatus.BAD_REQUEST.getValue())
					&& (synchronizationLogRow.getErrorCodeType().equals(ErrorCodeType.RESOURCE_ALREADY_EXISTS))) {
				return "greenBackground";
			}

			// DELETE returning an error NOT FOUND 003
			if ((synchronizationLogRow.getHttpCode() == HttpStatus.NOT_FOUND.getValue())
					&& (synchronizationLogRow.getErrorCodeType().equals(ErrorCodeType.RESOURCE_MISSING))
					&& (synchronizationLogRow.getMethodType().equals(MethodType.DELETE))) {
				return "greenBackground";
			}

			if (synchronizationLogRow.getIsFinal()) {
				return "redBackground";
			} else {
				return "orangeBackground";
			}
		}
		return "";
	}
}
