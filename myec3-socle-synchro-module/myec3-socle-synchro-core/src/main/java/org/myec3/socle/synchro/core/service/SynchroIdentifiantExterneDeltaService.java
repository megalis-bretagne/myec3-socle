package org.myec3.socle.synchro.core.service;

import org.myec3.socle.core.service.IGenericService;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterneDelta;
import org.myec3.socle.synchro.core.domain.model.SynchronizationError;

import java.util.List;

public interface SynchroIdentifiantExterneDeltaService extends IGenericService<SynchroIdentifiantExterneDelta> {

    void truncate();
}
