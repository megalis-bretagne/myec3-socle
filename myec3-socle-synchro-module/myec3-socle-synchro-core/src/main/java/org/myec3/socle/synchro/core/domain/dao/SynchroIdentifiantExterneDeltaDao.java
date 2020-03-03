package org.myec3.socle.synchro.core.domain.dao;

import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterneDelta;
import org.myec3.socle.synchro.core.domain.model.SynchronizationError;

import java.util.List;

public interface SynchroIdentifiantExterneDeltaDao extends
        GenericSynchronizationDao<SynchroIdentifiantExterneDelta> {

    boolean truncate();
}
