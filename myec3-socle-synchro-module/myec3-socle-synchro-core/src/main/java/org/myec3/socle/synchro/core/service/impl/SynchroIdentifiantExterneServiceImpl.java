package org.myec3.socle.synchro.core.service.impl;

import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.impl.AbstractGenericServiceImpl;
import org.myec3.socle.synchro.core.domain.dao.SynchroIdentifiantExterneDao;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneService;
import org.springframework.stereotype.Service;

@Service("synchroIdentifiantExterneService")
public class SynchroIdentifiantExterneServiceImpl extends AbstractGenericServiceImpl<SynchroIdentifiantExterne, SynchroIdentifiantExterneDao>
        implements SynchroIdentifiantExterneService {
    @Override
    public void truncate() {
        dao.truncate();
    }

    @Override
    public SynchroIdentifiantExterne findByIdSocle(long idSocle,ResourceType resourceType) {
        return dao.findByIdSocle(idSocle, resourceType);
    }

    @Override
    public SynchroIdentifiantExterne findByAcronyme(String acronyme,ResourceType resourceType) {
        return dao.findByAcronyme(acronyme, resourceType);
    }

}
