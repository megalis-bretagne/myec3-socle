package org.myec3.socle.synchro.core.service.impl;

import org.myec3.socle.core.service.impl.AbstractGenericServiceImpl;
import org.myec3.socle.synchro.core.domain.dao.SynchroIdentifiantExterneDeltaDao;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterneDelta;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneDeltaService;
import org.springframework.stereotype.Service;

@Service("synchroIdentifiantExterneDeltaService")
public class SynchroIdentifiantExterneDeltaServiceImpl extends AbstractGenericServiceImpl<SynchroIdentifiantExterneDelta, SynchroIdentifiantExterneDeltaDao>
        implements SynchroIdentifiantExterneDeltaService {
    @Override
    public void truncate() {
        dao.truncate();
    }
}
