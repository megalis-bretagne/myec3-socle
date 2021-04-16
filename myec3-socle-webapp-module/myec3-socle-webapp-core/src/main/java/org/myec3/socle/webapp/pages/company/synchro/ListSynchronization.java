package org.myec3.socle.webapp.pages.company.synchro;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.synchro.core.service.SynchronizationLogService;
import org.myec3.socle.webapp.pages.AbtractListSynchronization;
import org.myec3.socle.webapp.pages.Index;
import org.myec3.socle.webapp.pages.company.employee.View;

import java.util.ArrayList;
import java.util.HashMap;

public class ListSynchronization extends AbtractListSynchronization {

    /** PERSIST OBJECT **/
    /**
     * The Company
     */
    @Getter
    @Setter
    @Persist(PersistenceConstants.FLASH)
    private Company company;

    @Inject
    private CompanyService companyService;

    @Inject
    private SynchronizationLogService synchronizationLogService;

    @Inject
    private ProfileService profileService;

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
        resourceTypeModel.put(ResourceType.COMPANY, "Entreprise");
        resourceTypeModel.put(ResourceType.ESTABLISHMENT, "Etablissement");
        resourceTypeModel.put(ResourceType.EMPLOYEE_PROFILE, "Employé");

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
        if (ResourceType.COMPANY.equals(this.logRow.getSynchronizationLog().getResourceType())) {
            return this.logRow.getStructureLabel();
        }
        if (ResourceType.EMPLOYEE_PROFILE.equals(this.logRow.getSynchronizationLog().getResourceType())) {
            return this.logRow.getUsername()+" - "+this.logRow.getStructureEmail();
        }
        if (ResourceType.ESTABLISHMENT.equals(this.logRow.getSynchronizationLog().getResourceType())) {
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
        if (ResourceType.COMPANY.equals(this.logRow.getSynchronizationLog().getResourceType())) {
            return "Entreprise";
        }
        if (ResourceType.EMPLOYEE_PROFILE.equals(this.logRow.getSynchronizationLog().getResourceType())) {
            return "Employé";
        }
        if (ResourceType.ESTABLISHMENT.equals(this.logRow.getSynchronizationLog().getResourceType())) {
            return "Etablissement";
        }
        return StringUtils.EMPTY;
    }

    /**
     * Check if logRow is type EMPLOYEE_PROFILE
     * @return  true is AGENT_PROFILE
     */
    public boolean isEmployeeProfile() {
        if (logRow == null) {
            return false;
        }
        return logRow.getSynchronizationLog().getResourceType().equals(ResourceType.EMPLOYEE_PROFILE);
    }

    /**
     * Go to agent detail
     * @param identifier the logId
     */
    public Object onSeeEmployee(String identifier) {
        if (identifier != null) {
            Profile userProfile = profileService.findByUsername(identifier);
            if (userProfile != null) {
                return pageRedirectLink.createPageRenderLinkWithContext(View.class, userProfile.getId());
            }
        }
        return Boolean.FALSE;
    }
}

