package org.myec3.socle.webapp.components.synchro;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.myec3.socle.synchro.core.domain.dto.SynchronizationLogDTO;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Component to display detail of log
 */
public class DetailSynchroLog extends AbstractPage {

    @Parameter(required = true)
    @Property
    private SynchronizationLogDTO synchronizationLog;



    /**
     * Check if log selected is an error
     * @return  true if Status is ERROR
     */
    public boolean isLogError() {
        if (this.synchronizationLog != null) {
            return this.synchronizationLog.getSynchronizationLog().getStatut().equals("ERROR");
        }
        return false;
    }

    /**
     * Get Label for ResourceType of log
     * @return  String label
     */
    public String getTypeLabel() {
        if (this.synchronizationLog != null) {
            switch (this.synchronizationLog.getSynchronizationLog().getResourceType()) {
                case AGENT_PROFILE:
                    return "Agent";
                case EMPLOYEE_PROFILE:
                    return "Employee";
                case COMPANY:
                    return "Entreprise";
                case COMPANY_DEPARTMENT:
                case ORGANISM_DEPARTMENT:
                    return "Service";
                case ESTABLISHMENT:
                    return "Etablissement";
                case ORGANISM:
                    return "Organisme";
                default:
                    return StringUtils.EMPTY;
            }
        }
        return StringUtils.EMPTY;
    }


    public String getResourceLabel() {
        if (this.synchronizationLog != null) {
            switch (this.synchronizationLog.getSynchronizationLog().getResourceType()) {
                case AGENT_PROFILE:
                case EMPLOYEE_PROFILE:
                    return this.synchronizationLog.getUsername()+" - "+this.synchronizationLog.getStructureEmail();
                case COMPANY:
                case ORGANISM:
                    return this.synchronizationLog.getStructureLabel();
                case ORGANISM_DEPARTMENT:
                case ESTABLISHMENT:
                    return this.synchronizationLog.getStructureLabel()+" - "+this.synchronizationLog.getStructureEmail();
                default:
                    return StringUtils.EMPTY;
            }
        }
        return StringUtils.EMPTY;
    }
}
