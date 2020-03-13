package org.myec3.socle.synchro.core.domain.dao;

import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneService;

public interface SynchroIdentifiantExterneDao extends
        GenericSynchronizationDao<SynchroIdentifiantExterne> {

    boolean truncate();

    SynchroIdentifiantExterne findByIdSocle(long idSocle, ResourceType resourceType);
}
