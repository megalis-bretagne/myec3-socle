package org.myec3.socle.synchro.core.service;

import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.IGenericService;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;

import java.util.List;

public interface SynchroIdentifiantExterneService extends IGenericService<SynchroIdentifiantExterne> {

    void truncate();

    SynchroIdentifiantExterne findByIdSocle(long idSocle,ResourceType resourceType);

    SynchroIdentifiantExterne findByAcronyme(String acronyme,ResourceType resourceType);

    List<SynchroIdentifiantExterne> findListByIdSocle(long idSocle, ResourceType resourceType);

}
