package org.myec3.socle.webapp.components.synchro;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.myec3.socle.core.domain.model.enums.ResourceType;
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
     * Check if log selected is for ORGANISM
     * @return  true if getResourceType if Organism
     */
    public boolean isOrganismProfile() {
        if (this.synchronizationLog != null) {
            return ResourceType.ORGANISM.equals(this.synchronizationLog.getSynchronizationLog().getResourceType());
        }
        return false;
    }

    /**
     * Check if log selected is for Agent Profile
     * @return  true if getResourceType if AGENT_PROFILE
     */
    public boolean isAgentProfile() {
        if (this.synchronizationLog != null) {
            return ResourceType.AGENT_PROFILE.equals(this.synchronizationLog.getSynchronizationLog().getResourceType());
        }
        return false;
    }

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
}
