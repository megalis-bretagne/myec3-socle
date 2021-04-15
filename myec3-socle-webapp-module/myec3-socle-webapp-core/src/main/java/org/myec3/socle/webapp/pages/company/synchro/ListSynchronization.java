package org.myec3.socle.webapp.pages.company.synchro;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.synchro.core.service.SynchronizationLogService;
import org.myec3.socle.webapp.pages.AbtractListSynchronization;
import org.myec3.socle.webapp.pages.Index;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;

public class ListSynchronization extends AbtractListSynchronization {

    /** PERSIST OBJECT **/
    /**
     * The Company
     */
    @Getter
    @Setter
    @Persist
    private Company company;

    @Inject
    @Named("companyservice")
    private CompanyService companyService;

    @Inject
    @Qualifier("synchronizationLogService")
    private SynchronizationLogService synchronizationLogService;


    @OnEvent(EventConstants.PASSIVATE)
    public Long onPassivate() {
        // redirect to Onactive
        return this.company != null ? this.company.getId() : null;
    }

    @OnEvent(EventConstants.ACTIVATE)
    public Object onActivate(Long id) {
        // Redirect if no Admin
        if (Boolean.FALSE.equals(this.getIsAdmin())) {
            return Index.class;
        }
        // init resourceTypeModel
        resourceTypeModel = new HashMap<>();
//        resourceTypeModel.put(ResourceType.AGENT_PROFILE, ResourceType.EMPLOYEE_PROFILE.name());
//        resourceTypeModel.put(ResourceType.ORGANISM, ResourceType.COMPANY.name());
//        resourceTypeModel.put(ResourceType.ORGANISM_DEPARTMENT, ResourceType.ESTABLISHMENT.name());

        // get Data if scope changed
        if (this.company == null || !id.equals(this.company.getId())) {
            this.company = this.companyService.findOne(id);

            if (this.company == null) {
                return Index.class;
            }
            this.synchroLogResult = synchronizationLogService.findAllByCompany(company);
            //First copy of data in list
            this.synchroLogFilter = new ArrayList<>(this.synchroLogResult);
        }
        return Boolean.TRUE;
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
     * Get Label for resource Type column
     * @return resource Label
     */
    public String getResourceLabel() {
        if (this.logRow == null) {
            return StringUtils.EMPTY;
        }
        if (ResourceType.ORGANISM.equals(this.logRow.getSynchronizationLog().getResourceType())) {
            return "Organisme";
        }
        if (ResourceType.AGENT_PROFILE.equals(this.logRow.getSynchronizationLog().getResourceType())) {
            return "Agent";
        }
        if (ResourceType.ORGANISM_DEPARTMENT.equals(this.logRow.getSynchronizationLog().getResourceType())) {
            return "Service";
        }
        return StringUtils.EMPTY;
    }
}

