package org.myec3.socle.webapp.pages.organism.synchro;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.synchro.core.service.SynchronizationLogService;
import org.myec3.socle.webapp.pages.AbtractListSynchronization;
import org.myec3.socle.webapp.pages.Index;
import org.myec3.socle.webapp.pages.organism.agent.View;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Page used to display organism's Synchronization list
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/synchro/ListSynchronization.tml<br
 * />
 *
 *
 */
public class ListSynchronization extends AbtractListSynchronization {


    /** PERSIST OBJECT **/
    /**
     * The organism
     */
    @Getter
    @Setter
    @Persist
    private Organism organism;

    @Inject
    private OrganismService organismService;

    @Inject
    private SynchronizationLogService synchronizationLogService;

    @Inject
    private ProfileService profileService;

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
        resourceTypeModel.put(ResourceType.AGENT_PROFILE,"Agent");
        resourceTypeModel.put(ResourceType.ORGANISM,"Organisme");
        resourceTypeModel.put(ResourceType.ORGANISM_DEPARTMENT,"Service");

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
            return this.logRow.getUsername()+" - ("+this.logRow.getStructureEmail()+")";
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

    /**
     * Go to agent detail
     * @param agentIdentifier the logId
     */
    public Object onSeeAgent(String agentIdentifier) {
        if (agentIdentifier != null) {
            Profile userProfile = profileService.findByUsername(agentIdentifier);
            if (userProfile != null) {
                return pageRedirectLink.createPageRenderLinkWithContext(View.class,userProfile.getId());
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Check if logRow is type AGENT_PROFILE
     * @return  true is AGENT_PROFILE
     */
    public boolean isAgentProfile() {
        if (logRow == null) {
            return false;
        }
        return logRow.getSynchronizationLog().getResourceType().equals(ResourceType.AGENT_PROFILE);
    }
}
