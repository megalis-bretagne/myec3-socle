package org.myec3.socle.webapp.pages.synchroman.application;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.Request;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.StructureApplicationInfoService;
import org.myec3.socle.core.service.StructureService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class ListStructure extends AbstractPage {

    @Persist(PersistenceConstants.FLASH)
    private String successMessage;

    @Inject
    @Named("structureService")
    private StructureService structureService;

    @Inject
    @Named("structureApplicationInfoService")
    private StructureApplicationInfoService structureApplicationInfoService;

    @Inject
    @Named("agentProfileService")
    private AgentProfileService agentProfileService;

    @Inject
    @Named("employeeProfileService")
    private EmployeeProfileService employeeProfileService;

    @Persist
    private Application application;

    @Persist
    private List<RowTable> rowTables;

    @Property
    private Integer rowIndex;

    @Property
    private RowTable row;

    @Inject
    private BeanModelSource beanModelSource;

    @Component
    private Grid rowTableGrid;

    @Inject
    private Request request;

    private static final String[] HEADER = {"label", "nbMaxLicenses", "nbLicensesUsed"};

    @OnEvent(EventConstants.ACTIVATE)
    public void activation() {
        super.initUser();
    }

    @OnEvent(EventConstants.ACTIVATE)
    public Object onActivate() {
        return Index.class;
    }

    @OnEvent(EventConstants.ACTIVATE)
    public Object onActivate(Long id) {

        // if listParameter is not empty then the user navigates between pagination
        List<String> listParameter = this.request.getParameterNames();

        // get Data if scope changed
        if (this.application == null || !id.equals(this.application.getId()) || listParameter.isEmpty()) {
            this.application = this.applicationService.findOne(id);
            if (null == this.application) {
                return Index.class;
            }
            this.rowTables = new ArrayList<>();

            List<Structure> structureList = this.structureService.findAllStructureByApplication(this.application);
            List<StructureApplicationInfo> structureApplicationInfoList = this.structureApplicationInfoService.findAllByApplication(this.application);
            for (StructureApplicationInfo structureApplicationInfo : structureApplicationInfoList) {
                Structure structure = structureList.stream().filter(
                        s -> s.getId().equals(structureApplicationInfo.getStructureApplicationInfoId().getStructuresId())).findAny().orElse(null);
                if (structure != null) {
                    RowTable rowTable = new RowTable(
                            structure.getId(),
                            structure.getLabel(),
                            structureApplicationInfo.getNbMaxLicenses(),
                            structure.getStructureType().getValue());

                    if (structure.getStructureType().getValue().equals(StructureTypeValue.ORGANISM)) {
                        rowTable.setnbLicensesUsed(this.getnbLicensesByOrganism((Organism) structure));
                    } else {
                        rowTable.setnbLicensesUsed(this.getnbLicensesByCompany((Company) structure));
                    }
                    this.rowTables.add(rowTable);
                }

            }
        }
        return true;
    }

    @SetupRender
    public void setupGrid() {
        rowTableGrid.getSortModel().clear();
        rowTableGrid.getSortModel().updateSort(HEADER[2]);
        rowTableGrid.getSortModel().updateSort(HEADER[2]);
    }

    public BeanModel<RowTable> getGridModel() {
        BeanModel<RowTable> model = this.beanModelSource.createDisplayModel(
                RowTable.class, this.getMessages());
        model.include(HEADER);
        return model;
    }


    private long getnbLicensesByOrganism(Organism organism) {
        List<AgentProfile> agentProfiles = this.agentProfileService.findAllAgentProfilesByOrganismAndApplication(organism, application);
        return agentProfiles.size();
    }

    private long getnbLicensesByCompany(Company company) {
        List<EmployeeProfile> employeeProfiles = this.employeeProfileService.findAllEmployeeProfilesByCompanyAndApplication(company, application);
        return employeeProfiles.size();
    }

    @OnEvent(EventConstants.PASSIVATE)
    public Long onPassivate() {
        return (this.application != null) ? this.application.getId() : null;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public List<RowTable> getRowTables() {
        return rowTables;
    }

    public boolean isOrganisme() {
        return this.row.structureType.equals(StructureTypeValue.ORGANISM);
    }

    public static class RowTable {
        private final Long id;
        private final String label;
        private final Long nbMaxLicenses;
        private Long nbLicensesUsed;
        private final StructureTypeValue structureType;

        public RowTable(Long id, String label, Long nbMaxLicenses, StructureTypeValue structureType) {
            this.id = id;
            this.label = label;
            this.nbMaxLicenses = nbMaxLicenses;
            this.structureType = structureType;
        }

        public Long getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public Long getnbMaxLicenses() {
            return nbMaxLicenses;
        }

        public Long getnbLicensesUsed() {
            return nbLicensesUsed;
        }

        public void setnbLicensesUsed(Long nbLicensesUsed) {
            this.nbLicensesUsed = nbLicensesUsed;
        }

        public StructureTypeValue getStructureType() {
            return structureType;
        }
    }

}
