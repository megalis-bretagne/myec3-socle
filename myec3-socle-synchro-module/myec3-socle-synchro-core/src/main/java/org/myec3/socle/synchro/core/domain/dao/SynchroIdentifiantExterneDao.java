package org.myec3.socle.synchro.core.domain.dao;

import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;

public interface SynchroIdentifiantExterneDao extends
        GenericSynchronizationDao<SynchroIdentifiantExterne> {

    boolean truncate();
}
