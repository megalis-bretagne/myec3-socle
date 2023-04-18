package org.myec3.socle.webapp.pages.synchroman.synchronization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.beanmodel.services.PropertyConduitSource;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.SdmResource;
import org.myec3.socle.core.service.*;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.tools.SynchronizationMarshaller;
import org.myec3.socle.synchro.api.SynchronizationService;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationInitial;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;
import org.myec3.socle.synchro.core.service.SdmConverterService;
import org.myec3.socle.synchro.core.service.SynchronizationInitialService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.company.DetailCompany;
import org.myec3.socle.webapp.pages.organism.DetailOrganism;
import org.myec3.socle.webapp.pages.organism.agent.View;
import org.myec3.socle.webapp.pages.organism.department.DetailDepartment;
import org.myec3.socle.webapp.utils.JsonStreamResponse;
import org.myec3.socle.webapp.utils.XmlStreamResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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
    @Service("establishmentService")
    private EstablishmentService establishmentService;


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
    @Service("sdmConverterService")
    private SdmConverterService sdmConverterService;


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


    /**
     * Download XML or Json Export of the current version of resource synchronized
     *
     * @param applicationName sending Application
     * @param resourceId      resource Identifier
     * @param resourceType    resource Type
     * @return page
     */
    public StreamResponse onDownloadSynchroFile(String applicationName, String resourceId, ResourceType resourceType) {

        StreamResponse response = null;
        //Check again that current user is SA
        if (super.getIsAdmin() || super.getIsGlobalManagerAgent()) {
            //Build file name regarding synchro parameter
            final String filename = applicationName + "_" + resourceType + "_" + resourceId;
                //Specific treatement for "Salle des marchés" that export json instead of XML
                if (MyEc3ApplicationConstants.SDM_APPLICATION.equals(applicationName)) {
                    response = new JsonStreamResponse(filename,
                            getJsonSynchroFile(Long.parseLong(resourceId), resourceType)
                    );
                } else {
                    response = new XmlStreamResponse(filename,
                            getXmlSynchroFile(Long.parseLong(resourceId), resourceType)
                    );
                }
        }
        return response;
    }

    /**
     * Generate XML export file
     *
     * @param resourceId   resource Identifier
     * @param resourceType  resource Type
     * @return XML file as input stream
     */
    protected InputStream getXmlSynchroFile(Long resourceId, ResourceType resourceType) {
        ByteArrayOutputStream baOutputStream = null;
        Resource resource = null;
        switch (resourceType) {
            case AGENT_PROFILE:
                resource = agentProfileService.findOne(resourceId);
                break;
            case EMPLOYEE_PROFILE:
                resource = employeeProfileService.findOne(resourceId);
                break;
            case COMPANY:
                resource = companyService.findOne(resourceId);
                break;
            case COMPANY_DEPARTMENT:
                resource = companyDepartmentService.findOne(resourceId);
            case ESTABLISHMENT:
                resource = establishmentService.findOne(resourceId);
                break;
            case ORGANISM:
                resource = organismService.findOne(resourceId);
                break;
            case ORGANISM_DEPARTMENT:
                resource = organismDepartmentService.findOne(resourceId);
                break;
            default:
                resource = null;
                break;
        }
        baOutputStream = SynchronizationMarshaller.marshalResource(resource);
        return new ByteArrayInputStream(baOutputStream.toByteArray());
    }

    /**
     * Generate JSON export file
     *
     * @param resourceId  resource Identifier
     * @param resourceType  resource Type
     * @return JSON file as input stream
     */
    protected InputStream getJsonSynchroFile(Long resourceId, ResourceType resourceType) {
        InputStream inputStream = null;
        SdmResource sdmResource = null;
        try {
            switch (resourceType) {
                case AGENT_PROFILE:
                    AgentProfile agentProfile = agentProfileService.findOne(resourceId);
                    sdmResource = sdmConverterService.convertToSdmAgent(agentProfile);
                    break;
                case EMPLOYEE_PROFILE:
                    EmployeeProfile employeeProfile = employeeProfileService.findOne(resourceId);
                    sdmResource = sdmConverterService.convertToSdmInscrit(employeeProfile);
                    break;
                case COMPANY:
                    Company company = companyService.findOne(resourceId);
                    sdmResource = sdmConverterService.convertSdmEntreprise(company);
                    break;
                case COMPANY_DEPARTMENT:
                    break;
                case ESTABLISHMENT:
                    Establishment establishment = establishmentService.findOne(resourceId);
                    sdmResource = sdmConverterService.convertSdmEtablissement(establishment);
                    break;
                case ORGANISM:
                    Organism organism = organismService.findOne(resourceId);
                    sdmResource = sdmConverterService.convertSdmOrganisme(organism);
                    break;
                case ORGANISM_DEPARTMENT:
                    OrganismDepartment organismDepartment = organismDepartmentService.findOne(resourceId);
                    sdmResource = sdmConverterService.convertToSdmService(organismDepartment);
                    break;
                default:
                    sdmResource = null;
                    break;
            }
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(sdmResource);
            inputStream = new ByteArrayInputStream(json.getBytes());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return inputStream;
    }


    /**
     * Redirect to view page given type resource and his id
     *
     * @param ressourceId  resource Identifier
     * @param resourceType resourceType
     * @return page
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
            default:
                break;
        }
    }

    /**
     * @param resourceType
     * @return ResourceService
     */
    @SuppressWarnings("unchecked")
    public ResourceService getResourceService(ResourceType resourceType) {
        if (logger.isDebugEnabled()) {
            logger.debug("Enterring in method getResourceService with resourceType : {} ", resourceType.name());
        }

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
            default:
                return null;
        }
    }

    /**
     * Get Label for resource Type column
     *
     * @return resource Label
     */
    public String getResourceLabel() {
        if (this.synchronizationLogRow == null) {
            return StringUtils.EMPTY;
        }
        switch (this.synchronizationLogRow.getResourceType()) {
            case AGENT_PROFILE:
                return "Agent";
            case EMPLOYEE_PROFILE:
                return "Employé";
            case COMPANY:
                return "Entreprise";
            case ESTABLISHMENT:
                return "Etablissement";
            case ORGANISM:
                return "Organisme";
            case ORGANISM_DEPARTMENT:
                return "Service";
            default:
                return StringUtils.EMPTY;
        }
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
