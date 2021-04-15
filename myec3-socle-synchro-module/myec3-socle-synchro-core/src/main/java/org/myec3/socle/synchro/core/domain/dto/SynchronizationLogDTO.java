package org.myec3.socle.synchro.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.myec3.socle.synchro.core.domain.model.SynchronizationLog;

/**
 * DTO for synchronizationLog
 * Use to retrieve all log for Organim ( Organsim/OrganismeDepartement/AgentProfile)
 * Or Company (company/establishement/employee)
 *
 */
@Data
@AllArgsConstructor
public class SynchronizationLogDTO {

    /**
     * Synchronization Log associated
     */
    private SynchronizationLog synchronizationLog;

    /**
     * username if log is for profile
     */
    private String username;

    /**
     * email of entity synchonisation
     */
    private String structureEmail;

    /**
     * Structure Label associated
     */
    private String structureLabel;
}
