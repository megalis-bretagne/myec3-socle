package org.myec3.socle.webapp.pages.organism.synchro;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO;
import org.myec3.socle.synchro.core.service.SynchronizationLogService;
import org.myec3.socle.webapp.components.synchro.SynchroLogFilter;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Page used to display organism's Synchronization list
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/synchro/ListSynchronization.tml<br
 * />
 *
 *
 */
public class ListSynchronization extends AbstractPage {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    /** PERSIST OBJECT **/
    /**
     * The organism
     */
    @Getter
    @Setter
    @Persist
    private Organism organism;

    /**
     * All log associated to organism
     */
    @Getter
    @Persist
    private List<SynchronizationLogDTO> synchroLogResult;

    /**
     * The log selected to display is details
     */
    @Property
    @Persist
    private SynchronizationLogDTO synchronizationLog;

    @Getter
    private Map<ResourceType, String> resourceTypeModel;

    /**
     * List display in Grid
     */
    @Getter
    @Setter
    @Persist
    private List<SynchronizationLogDTO> synchroLogFilter;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private PropertyConduitSource propertyConduitSource;

    @Inject
    @Named("organismService")
    private OrganismService organismService;

    @Inject
    @Qualifier("synchronizationLogService")
    private SynchronizationLogService synchronizationLogService;

    @Property
    private SynchronizationLogDTO logRow;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    /**
     * Zone to display synchronizationLog object
     */
    @InjectComponent
    private Zone detailLog;

    @InjectComponent
    private Zone zoneGrid;

    @Component
    private Grid logGrid;

    @Component(id = "filterLog")
    private SynchroLogFilter componentSynchroLogFilter;

    @OnEvent(EventConstants.PASSIVATE)
    public Long onPassivate() {
        // redirect to Onactive
        return this.organism != null ? this.organism.getId() : null;
    }

    @OnEvent(EventConstants.ACTIVATE)
    public Object onActivate(Long id) {
        // Redirect if no Admin
        if (Boolean.FALSE.equals(this.getIsAdmin())) {
            return Index.class;
        }
        // init resourceTypeModel
        resourceTypeModel = new HashMap<>();
        resourceTypeModel.put(ResourceType.AGENT_PROFILE, ResourceType.AGENT_PROFILE.name());
        resourceTypeModel.put(ResourceType.ORGANISM, ResourceType.ORGANISM.name());
        resourceTypeModel.put(ResourceType.ORGANISM_DEPARTMENT, ResourceType.ORGANISM_DEPARTMENT.name());

        // get Data if scope changed
        if (this.organism == null || !id.equals(this.organism.getId())) {
            this.organism = this.organismService.findOne(id);

            if (this.organism == null) {
                return Index.class;
            }
            this.synchroLogResult = synchronizationLogService.findAllByOrganism(organism);
            //First copy of data in list
            this.synchroLogFilter = new ArrayList<>(this.synchroLogResult);
        }
        return Boolean.TRUE;
    }

    @SetupRender
    public void setupGrid() {
        // Sort syncroDate by default
        if (this.logGrid.getSortModel().getSortConstraints().isEmpty()) {
            // Twice for ASCENDING
            logGrid.getSortModel().updateSort("synchronizationDate");
            logGrid.getSortModel().updateSort("synchronizationDate");
        }
    }

    /**
     * update Grid component after filter ok
     */
    @OnEvent(value = "logFilterDone")
    public void updateGridZoneWhenFilterDone() {
        this.synchroLogFilter = this.componentSynchroLogFilter.getSynchroLogMatching();
        this.ajaxResponseRenderer.addRender(zoneGrid);
    }

    /**
     * Select Log du display detail
     * @param logId the logId
     */
    public void onSelectLog(Long logId) {
        if (synchroLogFilter != null) {
            synchronizationLog = synchroLogFilter
                    .stream().filter(synchronizationLogDTO -> synchronizationLogDTO.getSynchronizationLog().getId().equals(logId))
                    .findFirst()
                    .orElse(null);
            ajaxResponseRenderer.addRender(detailLog);
            // refresh zone to apply style css on row selected
            ajaxResponseRenderer.addRender(zoneGrid);
        }
    }

    /**
     * Create model for table in page
     * @return : bean model
     */
    public BeanModel<SynchronizationLogDTO> getGridModel() {
        BeanModel<SynchronizationLogDTO> model = this.beanModelSource.createDisplayModel(
                SynchronizationLogDTO.class, this.getMessages());
        model.add("resourceType", this.propertyConduitSource
                .create(SynchronizationLogDTO.class, "synchronizationLog.resourceType") );

        model.add("synchronizationDate", this.propertyConduitSource
                .create(SynchronizationLogDTO.class, "synchronizationLog.synchronizationDate") );
        model.add("statut", this.propertyConduitSource
                .create(SynchronizationLogDTO.class, "synchronizationLog.statut") );

        model.add("actions", null);
        // specific column to display username or structureLabel
        model.add("identifier", null);

        model.include("resourceType", "identifier",  "synchronizationDate", "statut", "actions");

        return model;
    }

    public String getStatutImage() {
        return this.logRow.getSynchronizationLog().getStatut().equals("ERROR") ? "ko.png" : "ok.png";
    }

    /**
     * Get Date format du display synchronization Date
     * @return  DateFormat
     */
    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * Get Label for identifier column
     * @return  Identifier label
     */
    public String getIdentifierLabel() {
        if (this.logRow == null) {
            return StringUtils.EMPTY;
        }
        if (ResourceType.ORGANISM.equals(this.logRow.getSynchronizationLog().getResourceType())) {
            return this.logRow.getStructureLabel();
        }
        if (ResourceType.AGENT_PROFILE.equals(this.logRow.getSynchronizationLog().getResourceType())) {
            return this.logRow.getUsername()+" - "+this.logRow.getStructureEmail();
        }
        if (ResourceType.ORGANISM_DEPARTMENT.equals(this.logRow.getSynchronizationLog().getResourceType())) {
            return this.logRow.getStructureLabel()+" - "+this.logRow.getStructureEmail();
        }
        return StringUtils.EMPTY;
    }

    /**
     * Get row class for grid
     * @return  "selected" css CLASS if row is selected
     */
    public String getRowClass() {
        if (this.logRow == null || this.synchronizationLog == null) {
            return StringUtils.EMPTY;
        }

        if (this.synchronizationLog.getSynchronizationLog().getId().equals(this.logRow.getSynchronizationLog().getId())){
            return "selected";
        }
        return StringUtils.EMPTY;
    }

}
