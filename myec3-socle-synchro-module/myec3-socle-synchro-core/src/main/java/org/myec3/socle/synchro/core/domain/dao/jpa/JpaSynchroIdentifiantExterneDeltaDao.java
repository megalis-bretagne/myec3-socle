package org.myec3.socle.synchro.core.domain.dao.jpa;



import org.myec3.socle.synchro.core.domain.dao.SynchroIdentifiantExterneDeltaDao;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterneDelta;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;

@Repository("synchroIdentifiantExterneDeltaDao")
public class JpaSynchroIdentifiantExterneDeltaDao extends JpaGenericSynchronizationDao<SynchroIdentifiantExterneDelta>
        implements SynchroIdentifiantExterneDeltaDao {
    @Override
    public Class<SynchroIdentifiantExterneDelta> getType() {
        return SynchroIdentifiantExterneDelta.class;
    }

    @Override
    public boolean truncate() {
        this.getLog().debug("truncate tableSynchroIdentifiantExterneDelta");
        try {
            Query query = this.getEm().createNativeQuery(
                    "truncate table SynchroIdentifiantExterneDelta");
            query.executeUpdate();
            this.getLog().debug("truncate table SynchroIdentifiantExterneDelta successfull.");
            return true;
        }catch (RuntimeException re) {
            this.getLog().error("tableSynchroIdentifiantExterneDelta failed.", re);
            throw re;
        }
    }
}

