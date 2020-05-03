package org.myec3.socle.synchro.core.domain.dao;

import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneService;

import java.util.List;

public interface SynchroIdentifiantExterneDao extends
        GenericSynchronizationDao<SynchroIdentifiantExterne> {

    boolean truncate();

    SynchroIdentifiantExterne findByIdSocle(long idSocle, ResourceType resourceType);

    List<SynchroIdentifiantExterne> findListByIdSocle(long idSocle, ResourceType resourceType);

    SynchroIdentifiantExterne findByAcronyme(String acronyme, ResourceType resourceType);
}
