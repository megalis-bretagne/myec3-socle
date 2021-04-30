package org.myec3.socle.webapp.pages.synchroman.synchronization;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PropertyConduitSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.*;
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
import org.myec3.socle.webapp.pages.company.DetailCompany;
import org.myec3.socle.webapp.pages.organism.DetailOrganism;
import org.myec3.socle.webapp.pages.organism.agent.View;
import org.myec3.socle.webapp.pages.organism.department.DetailDepartment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class SearchResult extends AbstractPage {

    private static Logger logger = LogManager.getLogger(SearchResult.class);

    // Template attributes
    @Persist
    private List<SynchronizationLog> synchronizationLogResult;

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

    @Inject
    @Service("synchronizationInitialService")
    private SynchronizationInitialService synchronizationInitialService;

    @Inject
    @Service("synchronizationLogService")
    private SynchronizationLogService synchronizationLogService;

    @SuppressWarnings("unused")
    @Property
    private Integer rowIndex;

    private SynchronizationLog synchronizationLog;

    @Property
    private SynchronizationLog synchronizationLogRow;

    @SuppressWarnings("unused")
    @Inject
    private PropertyConduitSource propertyConduitSource;

    @Persist("Flash")
    private String successMessage;

    // Services
    @SuppressWarnings("unused")
    @Inject
    private JavaScriptSupport renderSupport;

    // Services
    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    // // Getters n Setters
    @Component
    private Grid synchronizationLogGrid;

    @Inject
    protected PageRenderLinkSource pageRedirectLink;

    @SetupRender
    public void setupGrid() {
        if (this.synchronizationLogGrid.getSortModel().getSortConstraints().isEmpty()) {
            // Twice for ASCENDING
            synchronizationLogGrid.getSortModel().updateSort("synchronizationDate");
            synchronizationLogGrid.getSortModel().updateSort("synchronizationDate");
        }
    }

    /**
     * @return : bean model
     */
    public BeanModel<SynchronizationLog> getGridModel() {
        BeanModel<SynchronizationLog> model = this.beanModelSource.createDisplayModel(SynchronizationLog.class,
                this.messages);
        model.add("actions", null);
        model.add("application", null);
        model.add("synchronizationInitial", null);
        model.include("application", "resourceType", "resourceId", "synchronizationInitial",
                "httpStatus", "nbAttempts", "isFinal", "synchronizationDate", "actions");
        return model;
    }

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
        Resource resource = (Resource) this.getResourceService(this.synchronizationLog.getResourceType())
                .findOne(this.synchronizationLog.getResourceId());

        if (null == resource) {
            logger.error("Finding resource failed");
            return Boolean.FALSE;
        }
        logger.info("Finding resource of type :" + resource.getClass() + " successful");

        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize
                .add(this.synchronizationLog.getSynchronizationSubscription().getApplication().getId());

        if (!listApplicationIdToResynchronize.isEmpty()) {
            this.resynchronizeResource(resource, listApplicationIdToResynchronize);
        } else {
            logger.error("There are no application to resynchronize");
            return Boolean.FALSE;
        }

        this.successMessage = this.messages.get("replay-success");
        return this;
    }


    /**
     * REdirect to view page given type resource and his id
     * @param ressourceId   resource Identifier
     * @param resourceType  resourceType
     * @return  page
     */
    public Object onSeeResource(String ressourceId, ResourceType resourceType) {
        switch (resourceType) {
            case AGENT_PROFILE:
                return pageRedirectLink.createPageRenderLinkWithContext(View.class, ressourceId);
            case EMPLOYEE_PROFILE:
                return pageRedirectLink.createPageRenderLinkWithContext(org.myec3.socle.webapp.pages.company.employee.View.class, ressourceId);
            case COMPANY:
                return pageRedirectLink.createPageRenderLinkWithContext(DetailCompany.class, ressourceId);
            case ESTABLISHMENT:
                return pageRedirectLink.createPageRenderLinkWithContext(org.myec3.socle.webapp.pages.company.establishment.View.class, ressourceId);
            case ORGANISM:
                return pageRedirectLink.createPageRenderLinkWithContext(DetailOrganism.class, ressourceId);
            case ORGANISM_DEPARTMENT:
                return pageRedirectLink.createPageRenderLinkWithContext(DetailDepartment.class, ressourceId);
            default:
                return Boolean.FALSE;
        }
    }

    /**
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

    public List<SynchronizationLog> getSynchronizationLogResult() {
        return synchronizationLogResult;
    }

    public void setSynchronizationLogResult(List<SynchronizationLog> synchronizationLogResult) {
        this.synchronizationLogResult = synchronizationLogResult;
    }

    public Integer getResultsNumber() {
        if (null == this.synchronizationLogResult)
            return 0;
        return this.synchronizationLogResult.size();
    }

    public SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    public String getIsFinalLabel() {
        return BooleanUtils.isTrue(synchronizationLogRow.getIsFinal()) ? "TERMINEE" : "EN COURS";
    }

    public String getSynchronizationInitialValue() {
        SynchronizationInitial synchronizationInitial = synchronizationInitialService
                .findBySynchronizationLog(synchronizationLogRow);
        if (synchronizationInitial == null) {
            return "";
        }
        return synchronizationInitial.getInitialSynchronizationId().toString();
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
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
                    && (ErrorCodeType.RESOURCE_MISSING.equals(synchronizationLogRow.getErrorCodeType()))
                    && (MethodType.DELETE.equals(synchronizationLogRow.getMethodType()))) {
                return "greenBackground";
            }

            if (BooleanUtils.isTrue(synchronizationLogRow.getIsFinal())) {
                return "redBackground";
            } else {
                return "orangeBackground";
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * @return
     */
    public SynchronizationLog getSynchronizationLog() {
        return synchronizationLog;
    }

    /**
     * @param synchronizationLog
     */
    public void setSynchronizationLog(SynchronizationLog synchronizationLog) {
        this.synchronizationLog = synchronizationLog;
    }
}
