package org.myec3.socle.webapp.pages;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PropertyConduitSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO;
import org.myec3.socle.webapp.components.synchro.SynchroLogFilter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Abstract Page to manage synchronizationList
 */
public abstract class AbtractListSynchronization extends AbstractPage {

    private static final String SYNCHRONIZATION_DATE_COLUMN = "synchronizationDate";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    /**
     * All log associated to organism
     */
    @Getter
    @Persist
    protected List<SynchronizationLogDTO> synchroLogResult;

    /**
     * The log selected to display is details
     */
    @Property
    @Persist
    protected SynchronizationLogDTO synchronizationLog;

    @Getter
    protected Map<ResourceType, String> resourceTypeModel;

    /**
     * List display in Grid
     */
    @Getter
    @Setter
    @Persist
    protected List<SynchronizationLogDTO> synchroLogFilter;

    @Inject
    protected BeanModelSource beanModelSource;

    @Inject
    protected PropertyConduitSource propertyConduitSource;

    @Property
    protected SynchronizationLogDTO logRow;

    @Inject
    protected AjaxResponseRenderer ajaxResponseRenderer;

    /**
     * Zone to display synchronizationLog object
     */
    @InjectComponent
    protected Zone detailLog;

    @InjectComponent
    protected Zone zoneGrid;

    @Component
    protected Grid logGrid;

    @Component(id = "filterLog")
    protected SynchroLogFilter componentSynchroLogFilter;

    @Inject
    protected PageRenderLinkSource pageRedirectLink;

    @SetupRender
    public void setupGrid() {
        // Sort syncroDate by default
        if (this.logGrid.getSortModel().getSortConstraints().isEmpty()) {
            // Twice for ASCENDING
            logGrid.getSortModel().updateSort(SYNCHRONIZATION_DATE_COLUMN);
            logGrid.getSortModel().updateSort(SYNCHRONIZATION_DATE_COLUMN);
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
     * Get Label for identifier column
     * @return  Identifier label
     */
    public abstract String getIdentifierLabel();

    /**
     * Get Label for resource Type column
     * @return resource Label
     */
    public abstract String getResourceLabel();

    /**
     * Create model for table in page
     * @return : bean model
     */
    public BeanModel<SynchronizationLogDTO> getGridModel() {
        BeanModel<SynchronizationLogDTO> model = this.beanModelSource.createDisplayModel(
                SynchronizationLogDTO.class, this.getMessages());
        model.add("resourceType", this.propertyConduitSource
                .create(SynchronizationLogDTO.class, "synchronizationLog.resourceType") );

        model.add("applicationName", this.propertyConduitSource
                .create(SynchronizationLogDTO.class, "synchronizationLog.applicationName") );

        model.add(SYNCHRONIZATION_DATE_COLUMN, this.propertyConduitSource
                .create(SynchronizationLogDTO.class, "synchronizationLog.synchronizationDate") );
        model.add("statut", null );

        model.add("view", null);
        // specific column to display username or structureLabel
        model.add("identifier", null);

        model.include("applicationName","resourceType", "identifier",  SYNCHRONIZATION_DATE_COLUMN, "statut", "view");

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
